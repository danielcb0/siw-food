package it.uniroma3.siw_food.repository;

import it.uniroma3.siw_food.model.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

/**
 * Repository interface for Recipe entity.
 * Provides methods for performing CRUD operations and custom queries.
 */
public interface RecipeRepository extends JpaRepository<Recipe, Long> {

    /**
     * Finds recipes by the associated chef's ID.
     *
     * @param chefId the ID of the chef
     * @return a list of recipes associated with the specified chef
     */
    List<Recipe> findByChefId(Long chefId);
}
