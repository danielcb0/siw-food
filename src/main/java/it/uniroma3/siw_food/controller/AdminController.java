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


    @GetMapping
    public String adminPanel() {
        return "admin/admin-panel";
    }

    // CRUD operations for Chefs
    @GetMapping("/chefs")
    public String listChefs(Model model) {
        List<Chef> chefs = chefRepository.findAll();
        model.addAttribute("chefs", chefs);
        return "admin/chefs/list";
    }

    @GetMapping("/chefs/new")
    public String createChefForm(Model model) {
        model.addAttribute("chef", new Chef());
        return "admin/chefs/create";
    }

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

    @GetMapping("/chefs/edit/{id}")
    public String editChefForm(@PathVariable Long id, Model model) {
        Chef chef = chefRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid chef Id:" + id));
        model.addAttribute("chef", chef);
        return "admin/chefs/edit";
    }

    @PostMapping("/chefs/update/{id}")
    public String updateChef(@PathVariable Long id,
                             @ModelAttribute("chef") Chef chef,
                             @ModelAttribute Credentials credentials,
                             @RequestParam("file") MultipartFile file,
                             RedirectAttributes redirectAttributes) {
        chef.setId(id);


        // Guardar el archivo y obtener el nombre del archivo
        String filename = uploadFileService.store(file);

        // Asignar el nombre del archivo al campo photo del chef
        chef.setPhoto(filename);

        // Update the password in Credentials
         if (credentials == null) {
            credentials = new Credentials();
            credentials.setChef(chef);
            credentials.setUsername(chef.getUsername()); // Asegurarse de que las credenciales tengan el nombre de usuario
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

    @GetMapping("/chefs/delete/{id}")
    public String deleteChefForm(@PathVariable Long id, Model model) {
        Chef chef = chefRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid chef Id:" + id));
        model.addAttribute("chef", chef);
        return "admin/chefs/delete";
    }

    @PostMapping("/chefs/delete/{id}")
    public String deleteChef(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Chef chef = chefRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid chef Id:" + id));
        List<Recipe> recipes = recipeRepository.findByChefId(id);

        // Eliminar todas las recetas asociadas al chef
        recipeRepository.deleteAll(recipes);

        chefRepository.delete(chef);
        redirectAttributes.addFlashAttribute("message", "Chef deleted successfully!");
        return "redirect:/admin/chefs";
    }


    // CRUD operations for Recipes
    @GetMapping("/recipes")
    public String listRecipes(Model model) {
        List<Recipe> recipes = recipeRepository.findAll();
        model.addAttribute("recipes", recipes);
        return "admin/recipes/list";
    }

    @GetMapping("/recipes/new")
    public String createRecipeForm(Model model) {
        model.addAttribute("recipe", new Recipe());
        return "admin/recipes/create";
    }

    @PostMapping("/recipes")
    public String saveRecipe(@ModelAttribute("recipe") Recipe recipe,
                             @RequestParam("ingredientName") List<String> ingredientNames,
                             @RequestParam("ingredientQuantity") List<String> ingredientQuantities,
                             @RequestParam("file") MultipartFile file,
                             RedirectAttributes redirectAttributes) {
        Chef authenticatedChef = chefService.getAuthenticatedChef();


        // Guardar el archivo y obtener el nombre del archivo
        String filename = uploadFileService.store(file);

        // Asignar el nombre del archivo al campo photo del chef
        recipe.setPhoto(filename);

        List<Ingredient> ingredients = new ArrayList<>();
        for (int i = 0; i < ingredientNames.size(); i++) {
            ingredients.add(new Ingredient(ingredientNames.get(i), ingredientQuantities.get(i), recipe));
        }
        recipe.setIngredients(ingredients);
        recipe.setChef(authenticatedChef); // Asociar la receta con el chef autenticado
        recipeRepository.save(recipe);
        redirectAttributes.addFlashAttribute("message", "Recipe created successfully!");
        return "redirect:/admin/recipes";
    }

    @GetMapping("/recipes/edit/{id}")
    public String editRecipeForm(@PathVariable Long id, Model model) {
        Recipe recipe = recipeRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid recipe Id:" + id));
        model.addAttribute("recipe", recipe);
        return "admin/recipes/edit";
    }

    @PostMapping("/recipes/update/{id}")
    public String updateRecipe(@PathVariable Long id,
                               @ModelAttribute("recipe") Recipe recipeDetails,
                               @RequestParam("ingredientName") List<String> ingredientNames,
                               @RequestParam("ingredientQuantity") List<String> ingredientQuantities,
                               @RequestParam("file") MultipartFile file,
                               RedirectAttributes redirectAttributes) {



        // Buscar la receta existente en la base de datos
        Recipe recipe = recipeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Recipe not exist with id :" + id));


        // Save file and get name string
        String filename = uploadFileService.store(file);

        // Set name file
        recipe.setPhoto(filename);

        // Actualizar las propiedades de la receta
        recipe.setName(recipeDetails.getName());
        recipe.setDescription(recipeDetails.getDescription());
        recipe.setPhotos(recipeDetails.getPhotos());
        recipe.setChef(recipeDetails.getChef());

        // Crear una lista de ingredientes y agregarla a la receta
        List<Ingredient> ingredients = new ArrayList<>();
        for (int i = 0; i < ingredientNames.size(); i++) {
            ingredients.add(new Ingredient(ingredientNames.get(i), ingredientQuantities.get(i), recipe));
        }
        recipe.setIngredients(ingredients);

        // Guardar la receta actualizada en la base de datos
        recipeRepository.save(recipe);

        // Actualizar la valoración del chef asociado
        Chef chef = recipe.getChef();
        if (chef != null) {
            ChefController.updateChefRating(chef);
        }
        redirectAttributes.addFlashAttribute("message", "Recipe updated successfully!");
        return "redirect:/admin/recipes";
    }

    @GetMapping("/recipes/delete/{id}")
    public String deleteRecipeForm(@PathVariable Long id, Model model) {
        Recipe recipe = recipeRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid recipe Id:" + id));
        model.addAttribute("recipe", recipe);
        return "admin/recipes/delete";
    }

    @PostMapping("/recipes/delete/{id}")
    public String deleteRecipe(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Recipe recipe = recipeRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid recipe Id:" + id));
        recipeRepository.delete(recipe);
        redirectAttributes.addFlashAttribute("message", "Recipe deleted successfully!");
        return "redirect:/admin/recipes";
    }
}