package it.uniroma3.siw_food.service;

import it.uniroma3.siw_food.model.Chef;
import it.uniroma3.siw_food.model.Recipe;
import it.uniroma3.siw_food.repository.ChefRepository;
import it.uniroma3.siw_food.repository.RecipeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RecipeService {

    @Autowired
    private RecipeRepository recipeRepository;

    @Autowired
    private ChefRepository chefRepository;

    public void saveRecipe(Recipe recipe, Long chefId) {
        Chef chef = chefRepository.findById(chefId).orElse(null);
        if (chef != null) {
            recipe.setChef(chef);
            recipeRepository.save(recipe);
        }
    }
}

