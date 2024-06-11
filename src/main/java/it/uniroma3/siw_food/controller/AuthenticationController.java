package it.uniroma3.siw_food.controller;

import it.uniroma3.siw_food.model.Chef;
import it.uniroma3.siw_food.model.Credentials;
import it.uniroma3.siw_food.repository.ChefRepository;
import it.uniroma3.siw_food.repository.CredentialsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

@Controller
public class AuthenticationController {

    @Autowired
    private CredentialsRepository credentialsRepository;

    @Autowired
    private ChefRepository chefRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/login")
    public String showLoginForm() {
        return "login";
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("chef", new Chef());
        model.addAttribute("credentials", new Credentials());
        return "register";
    }

    @PostMapping("/register")
    public String registerChef(Chef chef, Credentials credentials) {
        credentials.setPassword(passwordEncoder.encode(credentials.getPassword()));
        credentials.setRole("USER");
        chefRepository.save(chef);
        credentials.setChef(chef);
        credentialsRepository.save(credentials);
        return "redirect:/login";
    }

    @GetMapping("/loginSuccess")
    public String loginSuccess(Model model) {
        // Obtener el usuario autenticado actual
        User loggedUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // Obtener el nombre de usuario del usuario logueado
        String username = loggedUser.getUsername();

        // Encontrar las credenciales asociadas con este nombre de usuario
        Credentials credentials = credentialsRepository.findByUsername(username);

        // Obtener el chef asociado con estas credenciales
        Chef chef = credentials.getChef();

        // Agregar el ID del chef al modelo
        if (chef != null) {
            model.addAttribute("chefId", chef.getId());
        }

        // Redirigir a la página principal o cualquier otra página según sea necesario
        return "redirect:/";
    }



}
