package it.uniroma3.siw_food.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import it.uniroma3.siw_food.exception.ResourceNotFoundException;
import it.uniroma3.siw_food.model.Chef;
import it.uniroma3.siw_food.model.Recipe;
import it.uniroma3.siw_food.repository.ChefRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.OptionalDouble;

@Controller
@RequestMapping("/chefs")
public class ChefController {

    private static final String UPLOADED_FOLDER = "src/main/resources/static/images/";

    @Autowired
    private ChefRepository chefRepository;

    // Nueva ruta para listar chefs
    @GetMapping("/list")
    public String getAllChefs(Model model) {
        List<Chef> chefs = chefRepository.findAll();
        model.addAttribute("chefs", chefs);
        return "list-chefs";
    }

    @GetMapping("/new")
    public String createChefForm(Model model) {
        model.addAttribute("chef", new Chef());
        return "create-chef";
    }

    @PostMapping
    public String createChef(@ModelAttribute("chef") Chef chef, @RequestParam("photo") MultipartFile photo, RedirectAttributes redirectAttributes) {
        if (photo.isEmpty()) {
            redirectAttributes.addFlashAttribute("message", "Please select a file to upload");
            return "redirect:/chefs/new";
        }

        try {
            byte[] bytes = photo.getBytes();
            Path path = Paths.get(UPLOADED_FOLDER + photo.getOriginalFilename());
            Files.write(path, bytes);
            chef.setPhoto("/images/" + photo.getOriginalFilename());
            chefRepository.save(chef);
            redirectAttributes.addFlashAttribute("message", "You successfully uploaded '" + photo.getOriginalFilename() + "'");
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "redirect:/chefs/list";
    }

    @GetMapping("/edit/{id}")
    public String editChefForm(@PathVariable Long id, Model model) {
        Chef chef = chefRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Chef not exist with id :" + id));
        model.addAttribute("chef", chef);
        return "edit-chef";
    }

    @PostMapping("/update/{id}")
    public String updateChef(@PathVariable Long id, @ModelAttribute("chef") Chef chefDetails, @RequestParam("photo") MultipartFile photo) {
        Chef chef = chefRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Chef not exist with id :" + id));

        chef.setFirstName(chefDetails.getFirstName());
        chef.setLastName(chefDetails.getLastName());
        chef.setDateOfBirth(chefDetails.getDateOfBirth());
        chef.setEmail(chefDetails.getEmail());
        chef.setUsername(chefDetails.getUsername());
        chef.setPassword(chefDetails.getPassword());

        if (!photo.isEmpty()) {
            try {
                byte[] bytes = photo.getBytes();
                Path path = Paths.get(UPLOADED_FOLDER + photo.getOriginalFilename());
                Files.write(path, bytes);
                chef.setPhoto("/images/" + photo.getOriginalFilename());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        chefRepository.save(chef);
        return "redirect:/chefs/list";
    }

    @GetMapping("/delete/{id}")
    public String deleteChefForm(@PathVariable Long id, Model model) {
        Chef chef = chefRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Chef not exist with id :" + id));
        model.addAttribute("chef", chef);
        return "delete-chef";
    }

    @PostMapping("/delete/{id}")
    public String deleteChef(@PathVariable Long id) {
        Chef chef = chefRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Chef not exist with id :" + id));
        chefRepository.delete(chef);
        return "redirect:/chefs/list";
    }

    @GetMapping("/{id}")
    public String getChefDetails(@PathVariable Long id, Model model) {
        Chef chef = chefRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Chef not exist with id :" + id));
        model.addAttribute("chef", chef);
        model.addAttribute("recipes", chef.getRecipes());
        return "chef-details";
    }


    public static void updateChefRating(Chef chef) {
        if (chef != null) {
            List<Recipe> recipes = chef.getRecipes();
            OptionalDouble chefAverageRating = recipes.stream()
                    .filter(r -> r.getRating() > 0) // Filtrar recetas que tengan valoraciones
                    .mapToInt(Recipe::getRating)
                    .average();
            chef.setRating((int) chefAverageRating.orElse(0));
        }
    }

}
