package it.uniroma3.siw_food.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import it.uniroma3.siw_food.exception.ResourceNotFoundException;
import it.uniroma3.siw_food.model.Recipe;
import it.uniroma3.siw_food.repository.RecipeRepository;

@Controller
@RequestMapping("/recipes")
public class ThymeleafRecipeController {

    @Autowired
    private RecipeRepository recipeRepository;

    @GetMapping
    public String viewRecipes(Model model) {
        model.addAttribute("recipes", recipeRepository.findAll());
        return "list-recipes";
    }

    @GetMapping("/new")
    public String createRecipeForm(Model model) {
        model.addAttribute("recipe", new Recipe());
        return "create-recipe";
    }

    @PostMapping
    public String saveRecipe(Recipe recipe) {
        recipeRepository.save(recipe);
        return "redirect:/recipes";
    }

    @GetMapping("/edit/{id}")
    public String editRecipeForm(@PathVariable Long id, Model model) {
        Recipe recipe = recipeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Recipe not exist with id :" + id));
        model.addAttribute("recipe", recipe);
        return "edit-recipe";
    }

    @PostMapping("/update/{id}")
    public String updateRecipe(@PathVariable Long id, Recipe recipeDetails) {
        Recipe recipe = recipeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Recipe not exist with id :" + id));

        recipe.setName(recipeDetails.getName());
        recipe.setDescription(recipeDetails.getDescription());
        recipe.setPhotos(recipeDetails.getPhotos());
        recipe.setIngredients(recipeDetails.getIngredients());
        recipe.setChef(recipeDetails.getChef());

        recipeRepository.save(recipe);
        return "redirect:/recipes";
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
        return "redirect:/recipes";
    }
}
