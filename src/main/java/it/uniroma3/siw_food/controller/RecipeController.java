package it.uniroma3.siw_food.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import it.uniroma3.siw_food.exception.ResourceNotFoundException;
import it.uniroma3.siw_food.model.Recipe;
import it.uniroma3.siw_food.repository.RecipeRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/v1")
public class RecipeController {

    @Autowired
    private RecipeRepository recipeRepository;

    // get all recipes
    @GetMapping("/recipes")
    public List<Recipe> getAllRecipes() {
        return recipeRepository.findAll();
    }

    // create recipe rest api
    @PostMapping("/recipes")
    public Recipe createRecipe(@RequestBody Recipe recipe) {
        return recipeRepository.save(recipe);
    }

    // get recipe by id rest api
    @GetMapping("/recipes/{id}")
    public ResponseEntity<Recipe> getRecipeById(@PathVariable Long id) {
        Recipe recipe = recipeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Recipe not exist with id :" + id));
        return ResponseEntity.ok(recipe);
    }

    // update recipe rest api
    @PutMapping("/recipes/{id}")
    public ResponseEntity<Recipe> updateRecipe(@PathVariable Long id, @RequestBody Recipe recipeDetails) {
        Recipe recipe = recipeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Recipe not exist with id :" + id));

        recipe.setName(recipeDetails.getName());
        recipe.setDescription(recipeDetails.getDescription());
        recipe.setPhotos(recipeDetails.getPhotos());
        recipe.setIngredients(recipeDetails.getIngredients());
        recipe.setChef(recipeDetails.getChef());

        Recipe updatedRecipe = recipeRepository.save(recipe);
        return ResponseEntity.ok(updatedRecipe);
    }

    // delete recipe rest api
    @DeleteMapping("/recipes/{id}")
    public ResponseEntity<Map<String, Boolean>> deleteRecipe(@PathVariable Long id) {
        Recipe recipe = recipeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Recipe not exist with id :" + id));

        recipeRepository.delete(recipe);
        Map<String, Boolean> response = new HashMap<>();
        response.put("deleted", Boolean.TRUE);
        return ResponseEntity.ok(response);
    }
}
