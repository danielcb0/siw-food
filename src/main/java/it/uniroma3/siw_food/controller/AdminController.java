package it.uniroma3.siw_food.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import it.uniroma3.siw_food.model.Chef;
import it.uniroma3.siw_food.model.Recipe;
import it.uniroma3.siw_food.repository.ChefRepository;
import it.uniroma3.siw_food.repository.RecipeRepository;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private ChefRepository chefRepository;

    @Autowired
    private RecipeRepository recipeRepository;

    private static String UPLOADED_FOLDER = "src/main/resources/static/uploads/";


    @GetMapping
    public String adminPanel() {
        return "admin/admin-panel";
    }

    // CRUD operations for Chefs
    @GetMapping("/chefs")
    public String listChefs(Model model) {
        List<Chef> chefs = chefRepository.findAll();
        model.addAttribute("chefs", chefs);
        return "admin/chefs/list";
    }

    @GetMapping("/chefs/new")
    public String createChefForm(Model model) {
        model.addAttribute("chef", new Chef());
        return "admin/chefs/create";
    }

    @PostMapping("/chefs")
    public String saveChef(@ModelAttribute("chef") Chef chef, RedirectAttributes redirectAttributes) {
        chefRepository.save(chef);
        redirectAttributes.addFlashAttribute("message", "Chef created successfully!");
        return "redirect:/admin/chefs";
    }

    @GetMapping("/chefs/edit/{id}")
    public String editChefForm(@PathVariable Long id, Model model) {
        Chef chef = chefRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid chef Id:" + id));
        model.addAttribute("chef", chef);
        return "admin/chefs/edit";
    }

    @PostMapping("/chefs/update/{id}")
    public String updateChef(@PathVariable Long id, @ModelAttribute("chef") Chef chef, RedirectAttributes redirectAttributes) {
        chef.setId(id);
        chefRepository.save(chef);
        redirectAttributes.addFlashAttribute("message", "Chef updated successfully!");
        return "redirect:/admin/chefs";
    }

    @GetMapping("/chefs/delete/{id}")
    public String deleteChefForm(@PathVariable Long id, Model model) {
        Chef chef = chefRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid chef Id:" + id));
        model.addAttribute("chef", chef);
        return "admin/chefs/delete";
    }

    @PostMapping("/chefs/delete/{id}")
    public String deleteChef(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Chef chef = chefRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid chef Id:" + id));
        List<Recipe> recipes = recipeRepository.findByChefId(id);

        // Eliminar todas las recetas asociadas al chef
        recipeRepository.deleteAll(recipes);

        chefRepository.delete(chef);
        redirectAttributes.addFlashAttribute("message", "Chef deleted successfully!");
        return "redirect:/admin/chefs";
    }


    // CRUD operations for Recipes
    @GetMapping("/recipes")
    public String listRecipes(Model model) {
        List<Recipe> recipes = recipeRepository.findAll();
        model.addAttribute("recipes", recipes);
        return "admin/recipes/list";
    }

    @GetMapping("/recipes/new")
    public String createRecipeForm(Model model) {
        model.addAttribute("recipe", new Recipe());
        return "admin/recipes/create";
    }

    @PostMapping("/recipes")
    public String saveRecipe(@ModelAttribute("recipe") Recipe recipe, RedirectAttributes redirectAttributes) {
        recipeRepository.save(recipe);
        redirectAttributes.addFlashAttribute("message", "Recipe created successfully!");
        return "redirect:/admin/recipes";
    }

    @GetMapping("/recipes/edit/{id}")
    public String editRecipeForm(@PathVariable Long id, Model model) {
        Recipe recipe = recipeRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid recipe Id:" + id));
        model.addAttribute("recipe", recipe);
        return "admin/recipes/edit";
    }

    @PostMapping("/recipes/update/{id}")
    public String updateRecipe(@PathVariable Long id, @ModelAttribute("recipe") Recipe recipe, RedirectAttributes redirectAttributes) {
        recipe.setId(id);
        recipeRepository.save(recipe);
        redirectAttributes.addFlashAttribute("message", "Recipe updated successfully!");
        return "redirect:/admin/recipes";
    }

    @GetMapping("/recipes/delete/{id}")
    public String deleteRecipeForm(@PathVariable Long id, Model model) {
        Recipe recipe = recipeRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid recipe Id:" + id));
        model.addAttribute("recipe", recipe);
        return "admin/recipes/delete";
    }

    @PostMapping("/recipes/delete/{id}")
    public String deleteRecipe(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Recipe recipe = recipeRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid recipe Id:" + id));
        recipeRepository.delete(recipe);
        redirectAttributes.addFlashAttribute("message", "Recipe deleted successfully!");
        return "redirect:/admin/recipes";
    }
}