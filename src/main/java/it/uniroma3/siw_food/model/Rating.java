package it.uniroma3.siw_food.model;

import jakarta.persistence.*;

@Entity
public class Rating {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int score;

    @ManyToOne
    private Recipe recipe;

    @ManyToOne
    private Chef chef;

    // Constructor sin argumentos (necesario para JPA)
    public Rating() {}

    // Constructor con argumentos
    public Rating(int score, Recipe recipe, Chef chef) {
        this.score = score;
        this.recipe = recipe;
        this.chef = chef;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public Recipe getRecipe() {
        return recipe;
    }

    public void setRecipe(Recipe recipe) {
        this.recipe = recipe;
    }

    public Chef getChef() {
        return chef;
    }

    public void setChef(Chef chef) {
        this.chef = chef;
    }
}
