package it.uniroma3.siw_food.service;

import it.uniroma3.siw_food.exception.ResourceNotFoundException;
import it.uniroma3.siw_food.model.Chef;
import it.uniroma3.siw_food.model.Recipe;
import it.uniroma3.siw_food.repository.ChefRepository;
import it.uniroma3.siw_food.repository.RecipeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Service
public class RecipeService {

    @Autowired
    private RecipeRepository recipeRepository;

    @Autowired
    private ChefRepository chefRepository;

    @Transactional
    public void saveRecipe(Recipe recipe, Long chefId) {
        Chef chef = chefRepository.findById(chefId)
                .orElseThrow(() -> new ResourceNotFoundException("Chef not found with id :" + chefId));
        recipe.setChef(chef);
        recipeRepository.save(recipe);
    }


}

