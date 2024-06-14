package it.uniroma3.siw_food.controller;

import it.uniroma3.siw_food.model.Chef;
import it.uniroma3.siw_food.model.Ingredient;
import it.uniroma3.siw_food.model.Rating;
import it.uniroma3.siw_food.model.Recipe;
import it.uniroma3.siw_food.repository.ChefRepository;
import it.uniroma3.siw_food.repository.RatingRepository;
import it.uniroma3.siw_food.repository.RecipeRepository;
import it.uniroma3.siw_food.service.RecipeService;
import it.uniroma3.siw_food.service.ChefService;
import it.uniroma3.siw_food.service.UploadFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import it.uniroma3.siw_food.exception.ResourceNotFoundException;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.OptionalDouble;

/**
 * This controller handles CRUD operations for recipes and related functionalities.
 */
@Controller
@RequestMapping("/recipes")
public class RecipeController {

    @Autowired
    private RecipeRepository recipeRepository;

    @Autowired
    private RatingRepository ratingRepository;

    @Autowired
    private ChefRepository chefRepository;

    @Autowired
    private ChefService chefService;

    @Autowired
    private UploadFileService uploadFileService;

    @Autowired
    private RecipeService recipeService;

    /**
     * Lists all recipes.
     *
     * @param model the model to add attributes to
     * @return the view to list all recipes
     */
    @GetMapping("/list")
    public String getAllRecipes(Model model) {
        List<Recipe> recipes = recipeRepository.findAll();
        model.addAttribute("recipes", recipes);
        return "list-recipes";
    }

    /**
     * Displays the form to create a new recipe.
     *
     * @param model the model to add attributes to
     * @return the view to create a new recipe
     */
    @GetMapping("/new")
    public String createRecipeForm(Model model) {
        model.addAttribute("recipe", new Recipe());
        model.addAttribute("ingredient", new Ingredient());
        return "create-recipe";
    }

    /**
     * Creates a new recipe and uploads the associated photo.
     *
     * @param recipe             the recipe to be created
     * @param ingredientNames    the names of the ingredients
     * @param ingredientQuantities the quantities of the ingredients
     * @param file               the photo file of the recipe
     * @return the redirect URL to the list of recipes
     */
    @PostMapping
    public String createRecipe(@ModelAttribute("recipe") Recipe recipe,
                               @RequestParam("ingredientName") List<String> ingredientNames,
                               @RequestParam("ingredientQuantity") List<String> ingredientQuantities,
                               @RequestParam("file") MultipartFile file) {
        Chef authenticatedChef = chefService.getAuthenticatedChef();
        if (authenticatedChef == null) {
            // Handle the case where no chef is authenticated
            return "redirect:/login";
        }

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
        return "redirect:/recipes/list";
    }

    /**
     * Displays the form to edit an existing recipe.
     *
     * @param id    the ID of the recipe to be edited
     * @param model the model to add attributes to
     * @return the view to edit a recipe
     */
    @GetMapping("/edit/{id}")
    public String editRecipeForm(@PathVariable Long id, Model model) {
        Recipe recipe = recipeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Recipe not exist with id :" + id));
        model.addAttribute("recipe", recipe);
        model.addAttribute("ingredient", new Ingredient());
        return "edit-recipe";
    }

    /**
     * Updates an existing recipe.
     *
     * @param id                 the ID of the recipe to be updated
     * @param recipeDetails      the updated recipe details
     * @param ingredientNames    the names of the ingredients
     * @param ingredientQuantities the quantities of the ingredients
     * @param file               the photo file of the recipe
     * @return the redirect URL to the list of recipes
     */
    @PostMapping("/update/{id}")
    public String updateRecipe(@PathVariable Long id,
                               @ModelAttribute("recipe") Recipe recipeDetails,
                               @RequestParam("ingredientName") List<String> ingredientNames,
                               @RequestParam("ingredientQuantity") List<String> ingredientQuantities,
                               @RequestParam("file") MultipartFile file) {
        // Find the existing recipe in the database
        Recipe recipe = recipeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Recipe not exist with id :" + id));

        // Save the file and get the file name
        String filename = uploadFileService.store(file);

        // Assign the file name to the recipe's photo field
        recipe.setPhoto(filename);

        // Update recipe properties
        recipe.setName(recipeDetails.getName());
        recipe.setDescription(recipeDetails.getDescription());
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

        return "redirect:/recipes/list";
    }

    /**
     * Displays the form to delete an existing recipe.
     *
     * @param id    the ID of the recipe to be deleted
     * @param model the model to add attributes to
     * @return the view to delete a recipe
     */
    @GetMapping("/delete/{id}")
    public String deleteRecipeForm(@PathVariable Long id, Model model) {
        Recipe recipe = recipeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Recipe not exist with id :" + id));
        model.addAttribute("recipe", recipe);
        return "delete-recipe";
    }

    /**
     * Deletes an existing recipe from the database.
     *
     * @param id the ID of the recipe to be deleted
     * @return the redirect URL to the list of recipes
     */
    @PostMapping("/delete/{id}")
    public String deleteRecipe(@PathVariable Long id) {
        Recipe recipe = recipeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Recipe not exist with id :" + id));
        recipeRepository.delete(recipe);
        return "redirect:/recipes/list";
    }

    /**
     * Displays the details of a specific recipe.
     *
     * @param id    the ID of the recipe
     * @param model the model to add attributes to
     * @return the view to display recipe details
     */
    @GetMapping("/{id}")
    public String getRecipeDetails(@PathVariable Long id, Model model) {
        Recipe recipe = recipeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Recipe not exist with id :" + id));
        model.addAttribute("recipe", recipe);
        model.addAttribute("ratings", ratingRepository.findByRecipe(recipe));
        return "recipe-details";
    }

    /**
     * Rates a specific recipe.
     *
     * @param id      the ID of the recipe to be rated
     * @param score   the score of the rating
     * @param comment the comment for the rating
     * @return the redirect URL to the recipe details page
     */
    @PostMapping("/rate/{id}")
    public String rateRecipe(@PathVariable Long id, @RequestParam("score") int score,
                             @RequestParam("comment") String comment) {
        Recipe recipe = recipeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Recipe not exist with id :" + id));

        // Save the new rating
        Rating rating = new Rating(score, comment, recipe);
        ratingRepository.save(rating);

        // Calculate the average rating of the recipe
        List<Rating> ratings = ratingRepository.findByRecipe(recipe);
        OptionalDouble averageRating = ratings.stream().mapToInt(Rating::getScore).average();
        recipe.setRating((int) averageRating.orElse(0));
        recipeRepository.save(recipe);

        // Update the average rating of the chef
        Chef chef = recipe.getChef();
        if (chef != null) {
            List<Recipe> recipes = chef.getRecipes();
            OptionalDouble chefAverageRating = recipes.stream()
                    .filter(r -> r.getRating() > 0) // Filter recipes that have ratings
                    .mapToInt(Recipe::getRating)
                    .average();
            chef.setRating((int) chefAverageRating.orElse(0));
            chefRepository.save(chef); // Ensure the chef is saved with the new average
        }

        return "redirect:/recipes/" + id;
    }
}
