package it.uniroma3.siw_food.controller;

import it.uniroma3.siw_food.model.Credentials;
import it.uniroma3.siw_food.service.ChefService;
import it.uniroma3.siw_food.service.UploadFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
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

/**
 * This controller handles CRUD operations for chefs and related functionalities.
 */
@Controller
@RequestMapping("/chefs")
public class ChefController {

    private static final String UPLOADED_FOLDER = "src/main/resources/static/images/";

    @Autowired
    private ChefRepository chefRepository;

    @Autowired
    private ChefService chefService;

    @Autowired
    private UploadFileService uploadFileService;

    /**
     * Lists all the chefs.
     *
     * @param model the model to add attributes to
     * @return the view to list chefs
     */
    @GetMapping("/list")
    public String getAllChefs(Model model) {
        List<Chef> chefs = chefRepository.findAll();
        model.addAttribute("chefs", chefs);
        return "list-chefs";
    }

    /**
     * Shows the form to create a new chef.
     *
     * @param model the model to add attributes to
     * @return the view to create a chef
     */
    @GetMapping("/new")
    public String createChefForm(Model model) {
        model.addAttribute("chef", new Chef());
        return "create-chef";
    }

    /**
     * Creates a new chef and uploads their photo.
     *
     * @param chef               the chef to be created
     * @param photo              the photo of the chef
     * @param redirectAttributes the attributes for the redirect URL
     * @return the redirect URL to the list of chefs
     */
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

    /**
     * Shows the form to edit an existing chef.
     *
     * @param id    the ID of the chef to be edited
     * @param model the model to add attributes to
     * @return the view to edit a chef
     */
    @GetMapping("/edit/{id}")
    public String editChefForm(@PathVariable Long id, Model model) {
        Chef chef = chefRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Chef not exist with id :" + id));
        model.addAttribute("chef", chef);
        return "edit-chef";
    }

    /**
     * Updates an existing chef.
     *
     * @param id           the ID of the chef to be updated
     * @param chefDetails  the updated chef details
     * @param file         the photo file of the chef
     * @return the redirect URL to the list of chefs
     */
    @PostMapping("/update/{id}")
    public String updateChef(@PathVariable Long id,
                             @ModelAttribute("chef") Chef chefDetails,
                             @RequestParam("file") MultipartFile file) {
        Chef chef = chefRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Chef not exist with id :" + id));

        chef.setFirstName(chefDetails.getFirstName());
        chef.setLastName(chefDetails.getLastName());
        chef.setDateOfBirth(chefDetails.getDateOfBirth());
        chef.setEmail(chefDetails.getEmail());
        chef.setUsername(chefDetails.getUsername());
        chef.setPassword(chefDetails.getPassword());

        // Update the password in Credentials
        Credentials credentials = chef.getCredentials();
        if (credentials == null) {
            credentials = new Credentials();
            credentials.setChef(chef);
            credentials.setUsername(chef.getUsername());
        }

        // Only update the password if a new one is provided.
        if (chefDetails.getPassword() != null && !chefDetails.getPassword().isEmpty()) {
            PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            credentials.setPassword(passwordEncoder.encode(chefDetails.getPassword()));
        }
        chef.setCredentials(credentials);

        // Hash the password before saving
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        chef.setPassword(passwordEncoder.encode(chefDetails.getPassword()));

        // Save the file and get the file name
        String filename = uploadFileService.store(file);

        // Assign the file name to the chef's photo field
        chef.setPhoto(filename);

        chefRepository.save(chef);
        return "redirect:/chefs/list";
    }

    /**
     * Shows the form to delete an existing chef.
     *
     * @param id    the ID of the chef to be deleted
     * @param model the model to add attributes to
     * @return the view to delete a chef
     */
    @GetMapping("/delete/{id}")
    public String deleteChefForm(@PathVariable Long id, Model model) {
        Chef chef = chefRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Chef not exist with id :" + id));
        model.addAttribute("chef", chef);
        return "delete-chef";
    }

    /**
     * Deletes an existing chef from the database.
     *
     * @param id the ID of the chef to be deleted
     * @return the redirect URL to the list of chefs
     */
    @PostMapping("/delete/{id}")
    public String deleteChef(@PathVariable Long id) {
        Chef chef = chefRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Chef not exist with id :" + id));
        chefRepository.delete(chef);
        return "redirect:/chefs/list";
    }

    /**
     * Displays the details of a specific chef.
     *
     * @param id    the ID of the chef
     * @param model the model to add attributes to
     * @return the view to display chef details
     */
    @GetMapping("/{id}")
    public String getChefDetails(@PathVariable Long id, Model model) {
        Chef chef = chefRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Chef not exist with id :" + id));
        model.addAttribute("chef", chef);
        model.addAttribute("recipes", chef.getRecipes());
        return "chef-details";
    }

    /**
     * Displays the details of a specific chef to the owner.
     *
     * @param id    the ID of the chef
     * @param model the model to add attributes to
     * @return the view to display chef details to the owner
     */
    @GetMapping("/owner/{id}")
    public String getChefOwnerDetails(@PathVariable Long id, Model model) {
        Chef chef = chefRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Chef not exist with id :" + id));
        model.addAttribute("chef", chef);
        model.addAttribute("recipes", chef.getRecipes());
        return "chef-details-owner";
    }

    /**
     * Shows the form to edit a chef by the owner.
     *
     * @param id    the ID of the chef to be edited
     * @param model the model to add attributes to
     * @return the view to edit a chef by the owner
     */
    @GetMapping("/{id}/edit")
    public String editChef(@PathVariable Long id, Model model) {
        Chef chef = chefService.findById(id);
        model.addAttribute("chef", chef);
        return "edit-chef-owner";
    }

    /**
     * Updates the rating of a chef based on their recipes.
     *
     * @param chef the chef whose rating is to be updated
     */
    public static void updateChefRating(Chef chef) {
        if (chef != null) {
            List<Recipe> recipes = chef.getRecipes();
            OptionalDouble chefAverageRating = recipes.stream()
                    .filter(r -> r.getRating() > 0)
                    .mapToInt(Recipe::getRating)
                    .average();
            chef.setRating((int) chefAverageRating.orElse(0));
        }
    }

    /**
     * Shows the form to upload a recipe for a specific chef.
     *
     * @param id    the ID of the chef
     * @param model the model to add attributes to
     * @return the view to upload a recipe for a chef
     */
    @GetMapping("/{id}/upload-recipe")
    public String createRecipeForChefForm(@PathVariable Long id, Model model) {
        Chef chef = chefRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Chef not exist with id :" + id));
        Recipe recipe = new Recipe();
        recipe.setChef(chef);  // Associate the recipe with the current chef
        model.addAttribute("recipe", recipe);
        model.addAttribute("chefId", id);  // Pass the chef's ID to the view
        return "upload-recipechef";
    }

    /**
     * Saves a recipe for a specific chef.
     *
     * @param id      the ID of the chef
     * @param recipe  the recipe to be saved
     * @return the redirect URL to the chef's details page
     */
    @PostMapping("/{id}/upload-recipe")
    public String saveRecipeForChef(@PathVariable Long id, @ModelAttribute("recipe") Recipe recipe) {
        Chef chef = chefRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Chef not exist with id :" + id));
        recipe.setChef(chef);  // Associate the recipe with the current chef
        chef.getRecipes().add(recipe);  // Add the recipe to the chef's list of recipes
        chefRepository.save(chef);  // Save the chef with the new recipe
        return "redirect:/chefs/" + id;  // Redirect to the chef's details page
    }
}
