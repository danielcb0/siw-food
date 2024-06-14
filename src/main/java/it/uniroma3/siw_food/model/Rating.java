package it.uniroma3.siw_food.model;

import jakarta.persistence.*;

/**
 * Represents a Rating entity associated with a Recipe.
 */
@Entity
public class Rating {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int score; // Rating score from 1 to 5
    private String comment;

    @ManyToOne
    private Recipe recipe;

    // No-argument constructor (required by JPA)
    public Rating() {}

    // Constructor with arguments
    public Rating(int score, String comment, Recipe recipe) {
        this.score = score;
        this.comment = comment;
        this.recipe = recipe;
    }

    // Getters and Setters

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
