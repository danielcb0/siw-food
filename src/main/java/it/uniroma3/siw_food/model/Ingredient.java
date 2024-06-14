package it.uniroma3.siw_food.model;

import jakarta.persistence.*;

/**
 * Represents an Ingredient entity associated with a Recipe.
 */
@Entity
public class Ingredient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String quantity;

    @ManyToOne
    private Recipe recipe;

    // No-argument constructor (required by JPA)
    public Ingredient() {}

    // Constructor with arguments
    public Ingredient(String name, String quantity, Recipe recipe) {
        this.name = name;
        this.quantity = quantity;
        this.recipe = recipe;
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

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public Recipe getRecipe() {
        return recipe;
    }

    public void setRecipe(Recipe recipe) {
        this.recipe = recipe;
    }
}
