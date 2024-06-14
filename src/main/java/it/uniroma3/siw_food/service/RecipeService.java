package it.uniroma3.siw_food.service;

import it.uniroma3.siw_food.exception.ResourceNotFoundException;
import it.uniroma3.siw_food.model.Chef;
import it.uniroma3.siw_food.model.Recipe;
import it.uniroma3.siw_food.repository.ChefRepository;
import it.uniroma3.siw_food.repository.RecipeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service class for handling operations related to Recipe.
 */
@Service
public class RecipeService {

    @Autowired
    private RecipeRepository recipeRepository;

    @Autowired
    private ChefRepository chefRepository;

    /**
     * Saves a recipe and associates it with a chef.
     *
     * @param recipe the recipe to be saved
     * @param chefId the ID of the chef to associate with the recipe
     */
    @Transactional
    public void saveRecipe(Recipe recipe, Long chefId) {
        Chef chef = chefRepository.findById(chefId)
                .orElseThrow(() -> new ResourceNotFoundException("Chef not found with id :" + chefId));
        recipe.setChef(chef);
        recipeRepository.save(recipe);
    }
}
