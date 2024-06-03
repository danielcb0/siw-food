package it.uniroma3.siw_food.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import it.uniroma3.siw_food.model.Employee;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long>{

}
