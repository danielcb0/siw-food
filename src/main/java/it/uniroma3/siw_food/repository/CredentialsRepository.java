package it.uniroma3.siw_food.repository;

import it.uniroma3.siw_food.model.Credentials;
import org.springframework.data.repository.CrudRepository;

/**
 * Repository interface for Credentials entity.
 * Provides methods for performing CRUD operations and custom queries.
 */
public interface CredentialsRepository extends CrudRepository<Credentials, Long> {

    /**
     * Finds credentials by their username.
     *
     * @param username the username associated with the credentials
     * @return the credentials with the specified username
     */
    Credentials findByUsername(String username);
}
