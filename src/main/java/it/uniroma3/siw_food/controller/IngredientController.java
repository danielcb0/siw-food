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

@Controller
@RequestMapping("/ingredients")
public class IngredientController {

    @Autowired
    private IngredientRepository ingredientRepository;

    @GetMapping("/add")
    public String showAddIngredientForm(Model model) {
        model.addAttribute("ingredient", new Ingredient());
        return "add-ingredient";
    }

    @PostMapping("/add")
    public String addIngredient(@ModelAttribute Ingredient ingredient) {
        ingredientRepository.save(ingredient);
        return "redirect:/ingredients";
    }

    @GetMapping
    public String listIngredients(Model model) {
        model.addAttribute("ingredients", ingredientRepository.findAll());
        return "list-ingredients";
    }
    @PostMapping("/error")
    public String error() {
        return "redirect:/ingredients";
    }
}
