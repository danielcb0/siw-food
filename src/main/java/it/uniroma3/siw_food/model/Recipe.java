package it.uniroma3.siw_food.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
public class Recipe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;

    @ElementCollection
    private List<String> photos;

    @OneToMany
    private List<Ingredient> ingredients;

    @ManyToOne
    private Chef chef;

    // Constructor sin argumentos (necesario para JPA)
    public Recipe() {}

    // Constructor con argumentos
    public Recipe(String name, String description, List<String> photos, List<Ingredient> ingredients, Chef chef) {
        this.name = name;
        this.description = description;
        this.photos = photos;
        this.ingredients = ingredients;
        this.chef = chef;
    }

    // Getters y Setters
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

    public List<String> getPhotos() {
        return photos;
    }

    public void setPhotos(List<String> photos) {
        this.photos = photos;
    }

    public List<Ingredient> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<Ingredient> ingredients) {
        this.ingredients = ingredients;
    }

    public Chef getChef() {
        return chef;
    }

    public void setChef(Chef chef) {
        this.chef = chef;
    }
}
