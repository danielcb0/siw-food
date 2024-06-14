package it.uniroma3.siw_food.repository;

import it.uniroma3.siw_food.model.Chef;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository interface for Chef entity.
 * Provides methods for performing CRUD operations and custom queries.
 */
public interface ChefRepository extends JpaRepository<Chef, Long> {

    /**
     * Finds a chef by their username.
     *
     * @param username the username of the chef
     * @return the chef with the specified username
     */
    Chef findByUsername(String username);
}
