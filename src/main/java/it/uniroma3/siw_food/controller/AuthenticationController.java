package it.uniroma3.siw_food.controller;

import it.uniroma3.siw_food.model.Chef;
import it.uniroma3.siw_food.model.Credentials;
import it.uniroma3.siw_food.repository.ChefRepository;
import it.uniroma3.siw_food.repository.CredentialsRepository;
import it.uniroma3.siw_food.service.UploadFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * This controller handles authentication-related operations, including login and registration.
 */
@Controller
public class AuthenticationController {

    @Autowired
    private CredentialsRepository credentialsRepository;

    @Autowired
    private ChefRepository chefRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UploadFileService uploadFileService;

    /**
     * Displays the login form.
     *
     * @return the login view
     */
    @GetMapping("/login")
    public String showLoginForm() {
        return "login";
    }

    /**
     * Displays the registration form.
     *
     * @param model the model to add attributes to
     * @return the registration view
     */
    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("chef", new Chef());
        model.addAttribute("credentials", new Credentials());
        return "register";
    }

    /**
     * Registers a new chef.
     *
     * @param chef        the chef to be registered
     * @param credentials the credentials of the chef
     * @param file        the photo file of the chef
     * @return the redirect URL to the login page
     */
    @PostMapping("/register")
    public String registerChef(
            @ModelAttribute Chef chef,
            @ModelAttribute Credentials credentials,
            @RequestParam("file") MultipartFile file) {

        // Save the file and get the file name
        String filename = uploadFileService.store(file);

        // Assign the file name to the chef's photo field
        chef.setPhoto(filename);

        credentials.setPassword(passwordEncoder.encode(credentials.getPassword()));
        credentials.setRole("USER");
        credentials.setChef(chef);
        chefRepository.save(chef);
        credentialsRepository.save(credentials);

        return "redirect:/login";
    }

    /**
     * Handles successful login.
     *
     * @param model the model to add attributes to
     * @return the redirect URL to the home page
     */
    @GetMapping("/loginSuccess")
    public String loginSuccess(Model model) {
        // Get the current authenticated user
        User loggedUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // Get the username of the logged user
        String username = loggedUser.getUsername();

        // Find the credentials associated with this username
        Credentials credentials = credentialsRepository.findByUsername(username);

        // Get the chef associated with these credentials
        Chef chef = credentials.getChef();

        // Add the chef's ID to the model
        if (chef != null) {
            model.addAttribute("chefId", chef.getId());
        }

        // Redirect to the home page or any other page as necessary
        return "redirect:/";
    }
}
