package it.uniroma3.siw_food.repository;

import it.uniroma3.siw_food.model.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RecipeRepository extends JpaRepository<Recipe, Long> {
    List<Recipe> findByChefId(Long chefId);
}
