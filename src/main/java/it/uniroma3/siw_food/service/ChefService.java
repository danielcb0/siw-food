package it.uniroma3.siw_food.service;

import it.uniroma3.siw_food.model.Chef;
import it.uniroma3.siw_food.model.Recipe;
import it.uniroma3.siw_food.repository.ChefRepository;
import it.uniroma3.siw_food.repository.RecipeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
@Service
public class ChefService {

    @Autowired
    private ChefRepository chefRepository;

    @Autowired
    private RecipeRepository recipeRepository;

    public Chef getChef(Long id) {
        return chefRepository.findById(id).orElse(null);
    }

    public Chef saveChef(Chef chef) {
        return chefRepository.save(chef);
    }



    @Transactional
    public void updateChefRating(Long chefId) {
        Chef chef = chefRepository.findById(chefId).orElse(null);
        if (chef != null) {
            List<Recipe> recipes = recipeRepository.findByChefId(chefId);
            double averageRating = recipes.stream()
                    .mapToInt(Recipe::getRating)
                    .average()
                    .orElse(0.0);
            chef.setRating((int) Math.round(averageRating));
            chefRepository.save(chef);
        }
    }
}
