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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import it.uniroma3.siw_food.exception.ResourceNotFoundException;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.OptionalDouble;

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
    private RecipeService recipeService;

    @GetMapping("/list")
    public String getAllRecipes(Model model) {
        List<Recipe> recipes = recipeRepository.findAll();
        model.addAttribute("recipes", recipes);
        return "list-recipes";
    }

    @GetMapping("/new")
    public String createRecipeForm(Model model) {
        model.addAttribute("recipe", new Recipe());
        model.addAttribute("ingredient", new Ingredient());
        return "create-recipe";
    }

    @PostMapping
    public String createRecipe(@ModelAttribute("recipe") Recipe recipe,
                               @RequestParam("ingredientName") List<String> ingredientNames,
                               @RequestParam("ingredientQuantity") List<String> ingredientQuantities) {
        Chef authenticatedChef = chefService.getAuthenticatedChef();
        if (authenticatedChef == null) {
            // Manejar el caso donde no hay chef autenticado
            return "redirect:/login";
        }

        List<Ingredient> ingredients = new ArrayList<>();
        for (int i = 0; i < ingredientNames.size(); i++) {
            ingredients.add(new Ingredient(ingredientNames.get(i), ingredientQuantities.get(i), recipe));
        }
        recipe.setIngredients(ingredients);
        recipe.setChef(authenticatedChef); // Asociar la receta con el chef autenticado
        recipeRepository.save(recipe);
        return "redirect:/recipes/list";
    }


    @GetMapping("/edit/{id}")
    public String editRecipeForm(@PathVariable Long id, Model model) {
        Recipe recipe = recipeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Recipe not exist with id :" + id));
        model.addAttribute("recipe", recipe);
        model.addAttribute("ingredient", new Ingredient());
        return "edit-recipe";
    }

    @PostMapping("/update/{id}")
    public String updateRecipe(@PathVariable Long id, @ModelAttribute("recipe") Recipe recipeDetails, @RequestParam("ingredientName") List<String> ingredientNames, @RequestParam("ingredientQuantity") List<String> ingredientQuantities) {
        Recipe recipe = recipeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Recipe not exist with id :" + id));

        recipe.setName(recipeDetails.getName());
        recipe.setDescription(recipeDetails.getDescription());
        recipe.setPhotos(recipeDetails.getPhotos());
        recipe.setChef(recipeDetails.getChef());

        List<Ingredient> ingredients = new ArrayList<>();
        for (int i = 0; i < ingredientNames.size(); i++) {
            ingredients.add(new Ingredient(ingredientNames.get(i), ingredientQuantities.get(i), recipe));
        }
        recipe.setIngredients(ingredients);

        recipeRepository.save(recipe);

        // Actualizar la valoración del chef asociado
        Chef chef = recipe.getChef();
        if (chef != null) {
            ChefController.updateChefRating(chef);
        }
        return "redirect:/recipes/list";
    }

    @GetMapping("/delete/{id}")
    public String deleteRecipeForm(@PathVariable Long id, Model model) {
        Recipe recipe = recipeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Recipe not exist with id :" + id));
        model.addAttribute("recipe", recipe);
        return "delete-recipe";
    }

    @PostMapping("/delete/{id}")
    public String deleteRecipe(@PathVariable Long id) {
        Recipe recipe = recipeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Recipe not exist with id :" + id));
        recipeRepository.delete(recipe);
        return "redirect:/recipes/list";
    }

    @GetMapping("/{id}")
    public String getRecipeDetails(@PathVariable Long id, Model model) {
        Recipe recipe = recipeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Recipe not exist with id :" + id));
        model.addAttribute("recipe", recipe);
        model.addAttribute("ratings", ratingRepository.findByRecipe(recipe));
        return "recipe-details";
    }

    @PostMapping("/rate/{id}")
    public String rateRecipe(@PathVariable Long id, @RequestParam("score") int score,
                             @RequestParam("comment") String comment) {
        Recipe recipe = recipeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Recipe not exist with id :" + id));

        // Guardar la nueva valoración
        Rating rating = new Rating(score,comment, recipe);
        ratingRepository.save(rating);

        // Calcular la media de las valoraciones de la receta
        List<Rating> ratings = ratingRepository.findByRecipe(recipe);
        OptionalDouble averageRating = ratings.stream().mapToInt(Rating::getScore).average();
        recipe.setRating((int) averageRating.orElse(0));
        recipeRepository.save(recipe);

        // Actualizar la media de las valoraciones del chef
        Chef chef = recipe.getChef();
        if (chef != null) {
            List<Recipe> recipes = chef.getRecipes();
            OptionalDouble chefAverageRating = recipes.stream()
                    .filter(r -> r.getRating() > 0) // Filtrar recetas que tengan valoraciones
                    .mapToInt(Recipe::getRating)
                    .average();
            chef.setRating((int) chefAverageRating.orElse(0));
            chefRepository.save(chef); // Asegurarse de guardar el chef con la nueva media
        }

        return "redirect:/recipes/" + id;
    }

}
