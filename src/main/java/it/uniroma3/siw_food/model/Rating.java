package it.uniroma3.siw_food.model;

import jakarta.persistence.*;

@Entity
public class Rating {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int score; // Valoración de 1 a 5



    private String comment;
    @ManyToOne
    private Recipe recipe;

    // Constructor sin argumentos (necesario para JPA)
    public Rating() {}

    // Constructor con argumentos
    public Rating(int score, String comment, Recipe recipe) {
        this.score = score;
        this.comment = comment;
        this.recipe = recipe;
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

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}