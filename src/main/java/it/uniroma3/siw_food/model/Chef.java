package it.uniroma3.siw_food.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.List;

/**
 * Represents a Chef entity with personal details, credentials, recipes, and ratings.
 */
@Entity
public class Chef {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;
    private String lastName;
    private LocalDate dateOfBirth;
    private String photo; // This field is a simple String
    private String email;
    private String username;
    private String password;

    @OneToMany(mappedBy = "chef")
    private List<Recipe> recipes;

    private Integer rating; // Changed from int to Integer

    @OneToOne(mappedBy = "chef", cascade = CascadeType.ALL, orphanRemoval = true)
    private Credentials credentials;

    // Constructors, getters, and setters

    public Chef() {}

    public Chef(String firstName, String lastName, LocalDate dateOfBirth, String photo, String email, String username, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;
        this.photo = photo;
        this.email = email;
        this.username = username;
        this.password = password;
        this.rating = 0; // Initial value if needed
    }

    // Getters and setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public List<Recipe> getRecipes() {
        return recipes;
    }

    public void setRecipes(List<Recipe> recipes) {
        this.recipes = recipes;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public Credentials getCredentials() {
        return credentials;
    }

    public void setCredentials(Credentials credentials) {
        this.credentials = credentials;
    }
}
