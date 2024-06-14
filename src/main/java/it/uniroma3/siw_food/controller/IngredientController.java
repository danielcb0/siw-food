package it.uniroma3.siw_food.controller;

import it.uniroma3.siw_food.model.Ingredient;
import it.uniroma3.siw_food.repository.IngredientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * This controller handles CRUD operations for ingredients.
 */
@Controller
@RequestMapping("/ingredients")
public class IngredientController {

    @Autowired
    private IngredientRepository ingredientRepository;

    /**
     * Displays the form to add a new ingredient.
     *
     * @param model the model to add attributes to
     * @return the view to add a new ingredient
     */
    @GetMapping("/add")
    public String showAddIngredientForm(Model model) {
        model.addAttribute("ingredient", new Ingredient());
        return "add-ingredient";
    }

    /**
     * Adds a new ingredient to the database.
     *
     * @param ingredient the ingredient to be added
     * @return the redirect URL to the list of ingredients
     */
    @PostMapping("/add")
    public String addIngredient(@ModelAttribute Ingredient ingredient) {
        ingredientRepository.save(ingredient);
        return "redirect:/ingredients";
    }

    /**
     * Lists all ingredients.
     *
     * @param model the model to add attributes to
     * @return the view to list all ingredients
     */
    @GetMapping
    public String listIngredients(Model model) {
        model.addAttribute("ingredients", ingredientRepository.findAll());
        return "list-ingredients";
    }

    /**
     * Handles errors by redirecting to the list of ingredients.
     *
     * @return the redirect URL to the list of ingredients
     */
    @PostMapping("/error")
    public String error() {
        return "redirect:/ingredients";
    }
}
