package it.uniroma3.siw_food.service;

import it.uniroma3.siw_food.model.Chef;
import it.uniroma3.siw_food.model.Recipe;
import it.uniroma3.siw_food.repository.ChefRepository;
import it.uniroma3.siw_food.repository.RecipeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.List;

/**
 * Service class for handling operations related to Chef.
 */
@Service
public class ChefService {

    @Autowired
    private ChefRepository chefRepository;

    @Autowired
    private RecipeRepository recipeRepository;

    /**
     * Retrieves a chef by their ID.
     *
     * @param id the ID of the chef
     * @return the chef with the specified ID, or null if not found
     */
    public Chef getChef(Long id) {
        return chefRepository.findById(id).orElse(null);
    }

    /**
     * Saves a chef to the repository.
     *
     * @param chef the chef to be saved
     * @return the saved chef
     */
    public Chef saveChef(Chef chef) {
        return chefRepository.save(chef);
    }

    /**
     * Retrieves the currently authenticated chef.
     *
     * @return the authenticated chef, or null if no chef is authenticated
     */
    public Chef getAuthenticatedChef() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof UserDetails) {
                String username = ((UserDetails) principal).getUsername();
                return chefRepository.findByUsername(username);
            }
        }
        return null;
    }

    /**
     * Updates the rating of a chef based on their recipes.
     *
     * @param chefId the ID of the chef
     */
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

    /**
     * Finds a chef by their ID.
     *
     * @param id the ID of the chef
     * @return the chef with the specified ID, or null if not found
     */
    public Chef findById(Long id) {
        return chefRepository.findById(id).orElse(null);
    }

    /**
     * Updates a chef's details.
     *
     * @param id           the ID of the chef to be updated
     * @param updatedChef the updated chef details
     */
    public void updateChef(Long id, Chef updatedChef) {
        Chef chef = chefRepository.findById(id).orElse(null);
        if (chef != null) {
            chef.setFirstName(updatedChef.getFirstName());
            chef.setLastName(updatedChef.getLastName());
            chef.setEmail(updatedChef.getEmail());
            chef.setPhoto(updatedChef.getPhoto());
            chef.setPassword(updatedChef.getPassword());
            chefRepository.save(chef);
        }
    }
}
