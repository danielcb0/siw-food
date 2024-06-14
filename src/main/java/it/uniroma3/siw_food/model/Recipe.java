package it.uniroma3.siw_food.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a Recipe entity with details, ratings, ingredients, and associated chef.
 */
@Entity
public class Recipe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;
    private Integer rating; // Rating from 1 to 5

    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Rating> ratings = new ArrayList<>();

    @ElementCollection
    private List<String> photos = new ArrayList<>();

    private String photo;

    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Ingredient> ingredients = new ArrayList<>();

    @ManyToOne
    private Chef chef;

    // No-argument constructor (required by JPA)
    public Recipe() {}

    // Constructor with arguments
    public Recipe(String name, String description, List<String> photos, List<Ingredient> ingredients, Chef chef) {
        this.name = name;
        this.description = description;
        this.photos = photos;
        this.ingredients = ingredients;
        this.chef = chef;
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public List<Rating> getRatings() {
        return ratings;
    }

    public void setRatings(List<Rating> ratings) {
        this.ratings = ratings;
    }

    public List<String> getPhotos() {
        return photos;
    }

    public void setPhotos(List<String> photos) {
        this.photos = photos;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public List<Ingredient> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<Ingredient> ingredients) {
        this.ingredients.clear();
        if (ingredients != null) {
            this.ingredients.addAll(ingredients);
        }
    }

    public Chef getChef() {
        return chef;
    }

    public void setChef(Chef chef) {
        this.chef = chef;
    }
}
