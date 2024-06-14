package it.uniroma3.siw_food.controller;

import it.uniroma3.siw_food.exception.ResourceNotFoundException;
import it.uniroma3.siw_food.model.Credentials;
import it.uniroma3.siw_food.model.Ingredient;
import it.uniroma3.siw_food.repository.CredentialsRepository;
import it.uniroma3.siw_food.service.ChefService;
import it.uniroma3.siw_food.service.UploadFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import it.uniroma3.siw_food.model.Chef;
import it.uniroma3.siw_food.model.Recipe;
import it.uniroma3.siw_food.repository.ChefRepository;
import it.uniroma3.siw_food.repository.RecipeRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * This controller handles all the administrative functions for the application.
 * It includes CRUD operations for both Chefs and Recipes, as well as file uploads and user authentication.
 */
@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private ChefRepository chefRepository;

    @Autowired
    private RecipeRepository recipeRepository;

    @Autowired
    private UploadFileService uploadFileService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private CredentialsRepository credentialsRepository;

    @Autowired
    private ChefService chefService;

    private static String UPLOADED_FOLDER = "src/main/resources/static/uploads/";


    /**
     * Displays the admin panel.
     *
     * @return the admin panel view
     */
    @GetMapping
    public String adminPanel() {
        return "admin/admin-panel";
    }

    // CRUD operations for Chefs

    /**
     * Lists all the chefs.
     *
     * @param model the model to add attributes to
     * @return the view to list chefs
     */
    @GetMapping("/chefs")
    public String listChefs(Model model) {
        List<Chef> chefs = chefRepository.findAll();
        model.addAttribute("chefs", chefs);
        return "admin/chefs/list";
    }

    /**
     * Shows the form to create a new chef.
     *
     * @param model the model to add attributes to
     * @return the view to create a chef
     */
    @GetMapping("/chefs/new")
    public String createChefForm(Model model) {
        model.addAttribute("chef", new Chef());
        return "admin/chefs/create";
    }

    /**
     * Saves a new chef to the database.
     *
     * @param chef        the chef to be saved
     * @param credentials the credentials of the chef
     * @param file        the photo file of the chef
     * @return the redirect URL to the list of chefs
     */
    @PostMapping("/chefs")
    public String saveChef(@ModelAttribute Chef chef,
                           @ModelAttribute Credentials credentials,
                           @RequestParam("file") MultipartFile file) {
        // Save file and get name string
        String filename = uploadFileService.store(file);

        // Set name file
        chef.setPhoto(filename);

        credentials.setPassword(passwordEncoder.encode(credentials.getPassword()));
        credentials.setRole("USER");
        credentials.setChef(chef);
        chefRepository.save(chef);
        credentialsRepository.save(credentials);
        return "redirect:/admin/chefs";
    }

    /**
     * Shows the form to edit an existing chef.
     *
     * @param id    the ID of the chef to be edited
     * @param model the model to add attributes to
     * @return the view to edit a chef
     */
    @GetMapping("/chefs/edit/{id}")
    public String editChefForm(@PathVariable Long id, Model model) {
        Chef chef = chefRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid chef Id:" + id));
        model.addAttribute("chef", chef);
        return "admin/chefs/edit";
    }

    /**
     * Updates an existing chef in the database.
     *
     * @param id                 the ID of the chef to be updated
     * @param chef               the updated chef
     * @param credentials        the credentials of the chef
     * @param file               the photo file of the chef
     * @param redirectAttributes the attributes for the redirect URL
     * @return the redirect URL to the list of chefs
     */
    @PostMapping("/chefs/update/{id}")
    public String updateChef(@PathVariable Long id,
                             @ModelAttribute("chef") Chef chef,
                             @ModelAttribute Credentials credentials,
                             @RequestParam("file") MultipartFile file,
                             RedirectAttributes redirectAttributes) {
        chef.setId(id);

        // Save the file and get the file name
        String filename = uploadFileService.store(file);

        // Assign the file name to the chef's photo field
        chef.setPhoto(filename);

        // Update the password in Credentials
        if (credentials == null) {
            credentials = new Credentials();
            credentials.setChef(chef);
            credentials.setUsername(chef.getUsername());
        }

        // Only update the password if a new one is provided.
        if (chef.getPassword() != null && !chef.getPassword().isEmpty()) {
            PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            credentials.setPassword(passwordEncoder.encode(chef.getPassword()));
        }
        chef.setCredentials(credentials);

        // Hash the password before saving
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        chef.setPassword(passwordEncoder.encode(chef.getPassword()));

        chefRepository.save(chef);
        credentials.setChef(chef);

        redirectAttributes.addFlashAttribute("message", "Chef updated successfully!");
        return "redirect:/admin/chefs";
    }

    /**
     * Shows the form to delete an existing chef.
     *
     * @param id    the ID of the chef to be deleted
     * @param model the model to add attributes to
     * @return the view to delete a chef
     */
    @GetMapping("/chefs/delete/{id}")
    public String deleteChefForm(@PathVariable Long id, Model model) {
        Chef chef = chefRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid chef Id:" + id));
        model.addAttribute("chef", chef);
        return "admin/chefs/delete";
    }

    /**
     * Deletes an existing chef from the database.
     *
     * @param id                 the ID of the chef to be deleted
     * @param redirectAttributes the attributes for the redirect URL
     * @return the redirect URL to the list of chefs
     */
    @PostMapping("/chefs/delete/{id}")
    public String deleteChef(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Chef chef = chefRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid chef Id:" + id));
        List<Recipe> recipes = recipeRepository.findByChefId(id);

        // Delete all recipes associated with the chef
        recipeRepository.deleteAll(recipes);

        chefRepository.delete(chef);
        redirectAttributes.addFlashAttribute("message", "Chef deleted successfully!");
        return "redirect:/admin/chefs";
    }

    // CRUD operations for Recipes

    /**
     * Lists all the recipes.
     *
     * @param model the model to add attributes to
     * @return the view to list recipes
     */
    @GetMapping("/recipes")
    public String listRecipes(Model model) {
        List<Recipe> recipes = recipeRepository.findAll();
        model.addAttribute("recipes", recipes);
        return "admin/recipes/list";
    }

    /**
     * Shows the form to create a new recipe.
     *
     * @param model the model to add attributes to
     * @return the view to create a recipe
     */
    @GetMapping("/recipes/new")
    public String createRecipeForm(Model model) {
        model.addAttribute("recipe", new Recipe());
        return "admin/recipes/create";
    }

    /**
     * Saves a new recipe to the database.
     *
     * @param recipe              the recipe to be saved
     * @param ingredientNames     the names of the ingredients
     * @param ingredientQuantities the quantities of the ingredients
     * @param file                the photo file of the recipe
     * @param redirectAttributes  the attributes for the redirect URL
     * @return the redirect URL to the list of recipes
     */
    @PostMapping("/recipes")
    public String saveRecipe(@ModelAttribute("recipe") Recipe recipe,
                             @RequestParam("ingredientName") List<String> ingredientNames,
                             @RequestParam("ingredientQuantity") List<String> ingredientQuantities,
                             @RequestParam("file") MultipartFile file,
                             RedirectAttributes redirectAttributes) {
        Chef authenticatedChef = chefService.getAuthenticatedChef();

        // Save the file and get the file name
        String filename = uploadFileService.store(file);

        // Assign the file name to the recipe's photo field
        recipe.setPhoto(filename);

        List<Ingredient> ingredients = new ArrayList<>();
        for (int i = 0; i < ingredientNames.size(); i++) {
            ingredients.add(new Ingredient(ingredientNames.get(i), ingredientQuantities.get(i), recipe));
        }
        recipe.setIngredients(ingredients);
        recipe.setChef(authenticatedChef); // Associate the recipe with the authenticated chef
        recipeRepository.save(recipe);
        redirectAttributes.addFlashAttribute("message", "Recipe created successfully!");
        return "redirect:/admin/recipes";
    }

    /**
     * Shows the form to edit an existing recipe.
     *
     * @param id    the ID of the recipe to be edited
     * @param model the model to add attributes to
     * @return the view to edit a recipe
     */
    @GetMapping("/recipes/edit/{id}")
    public String editRecipeForm(@PathVariable Long id, Model model) {
        Recipe recipe = recipeRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid recipe Id:" + id));
        model.addAttribute("recipe", recipe);
        return "admin/recipes/edit";
    }

    /**
     * Updates an existing recipe in the database.
     *
     * @param id                 the ID of the recipe to be updated
     * @param recipeDetails      the updated recipe
     * @param ingredientNames    the names of the ingredients
     * @param ingredientQuantities the quantities of the ingredients
     * @param file               the photo file of the recipe
     * @param redirectAttributes the attributes for the redirect URL
     * @return the redirect URL to the list of recipes
     */
    @PostMapping("/recipes/update/{id}")
    public String updateRecipe(@PathVariable Long id,
                               @ModelAttribute("recipe") Recipe recipeDetails,
                               @RequestParam("ingredientName") List<String> ingredientNames,
                               @RequestParam("ingredientQuantity") List<String> ingredientQuantities,
                               @RequestParam("file") MultipartFile file,
                               RedirectAttributes redirectAttributes) {
        // Search for the existing recipe in the database
        Recipe recipe = recipeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Recipe not exist with id :" + id));

        // Save file and get name string
        String filename = uploadFileService.store(file);

        // Set name file
        recipe.setPhoto(filename);

        // Update recipe properties
        recipe.setName(recipeDetails.getName());
        recipe.setDescription(recipeDetails.getDescription());
        recipe.setPhotos(recipeDetails.getPhotos());
        recipe.setChef(recipeDetails.getChef());

        // Create a list of ingredients and add it to the recipe
        List<Ingredient> ingredients = new ArrayList<>();
        for (int i = 0; i < ingredientNames.size(); i++) {
            ingredients.add(new Ingredient(ingredientNames.get(i), ingredientQuantities.get(i), recipe));
        }
        recipe.setIngredients(ingredients);

        // Save the updated recipe in the database
        recipeRepository.save(recipe);

        // Update the rating of the associated chef
        Chef chef = recipe.getChef();
        if (chef != null) {
            ChefController.updateChefRating(chef);
        }
        redirectAttributes.addFlashAttribute("message", "Recipe updated successfully!");
        return "redirect:/admin/recipes";
    }

    /**
     * Shows the form to delete an existing recipe.
     *
     * @param id    the ID of the recipe to be deleted
     * @param model the model to add attributes to
     * @return the view to delete a recipe
     */
    @GetMapping("/recipes/delete/{id}")
    public String deleteRecipeForm(@PathVariable Long id, Model model) {
        Recipe recipe = recipeRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid recipe Id:" + id));
        model.addAttribute("recipe", recipe);
        return "admin/recipes/delete";
    }

    /**
     * Deletes an existing recipe from the database.
     *
     * @param id                 the ID of the recipe to be deleted
     * @param redirectAttributes the attributes for the redirect URL
     * @return the redirect URL to the list of recipes
     */
    @PostMapping("/recipes/delete/{id}")
    public String deleteRecipe(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Recipe recipe = recipeRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid recipe Id:" + id));
        recipeRepository.delete(recipe);
        redirectAttributes.addFlashAttribute("message", "Recipe deleted successfully!");
        return "redirect:/admin/recipes";
    }
}
