package it.uniroma3.siw_food.repository;

import it.uniroma3.siw_food.model.Rating;
import it.uniroma3.siw_food.model.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RatingRepository extends JpaRepository<Rating, Long> {
    List<Rating> findByRecipe(Recipe recipe);
}
