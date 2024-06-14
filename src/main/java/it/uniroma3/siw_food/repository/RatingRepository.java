package it.uniroma3.siw_food.repository;

import it.uniroma3.siw_food.model.Rating;
import it.uniroma3.siw_food.model.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

/**
 * Repository interface for Rating entity.
 * Provides methods for performing CRUD operations and custom queries.
 */
public interface RatingRepository extends JpaRepository<Rating, Long> {

    /**
     * Finds ratings by the associated recipe.
     *
     * @param recipe the recipe associated with the ratings
     * @return a list of ratings for the specified recipe
     */
    List<Rating> findByRecipe(Recipe recipe);
}
