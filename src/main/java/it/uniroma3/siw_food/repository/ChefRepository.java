package it.uniroma3.siw_food.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import it.uniroma3.siw_food.model.Chef;

public interface ChefRepository extends JpaRepository<Chef, Long> {
}
