package it.uniroma3.siw_food.repository;

import it.uniroma3.siw_food.model.Chef;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChefRepository extends JpaRepository<Chef, Long> {
    Chef findByUsername(String username);

}
