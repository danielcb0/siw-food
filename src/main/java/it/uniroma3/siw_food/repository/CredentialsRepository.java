package it.uniroma3.siw_food.repository;

import it.uniroma3.siw_food.model.Credentials;
import org.springframework.data.repository.CrudRepository;

public interface CredentialsRepository extends CrudRepository<Credentials, Long> {
    Credentials findByUsername(String username);
}
