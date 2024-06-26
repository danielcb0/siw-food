package it.uniroma3.siw_food.model;

import jakarta.persistence.*;

/**
 * Represents the credentials for authentication and authorization.
 */
@Entity
public class Credentials {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String password;
    private String role;

    @OneToOne
    @JoinColumn(name = "chef_id", nullable = false)
    private Chef chef;

    // Constructors, getters, and setters

    public Credentials() {}

    public Credentials(String username, String password, String role, Chef chef) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.chef = chef;
    }

    // Getters and setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Chef getChef() {
        return chef;
    }

    public void setChef(Chef chef) {
        this.chef = chef;
    }
}
