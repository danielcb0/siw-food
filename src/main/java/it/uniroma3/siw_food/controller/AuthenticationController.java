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
    public String registerChef(
            @ModelAttribute Chef chef,
            @ModelAttribute Credentials credentials,
            @RequestParam("file") MultipartFile file) {

        // Guardar el archivo y obtener el nombre del archivo
        String filename = uploadFileService.store(file);

        // Asignar el nombre del archivo al campo photo del chef
        chef.setPhoto(filename);

        credentials.setPassword(passwordEncoder.encode(credentials.getPassword()));
        credentials.setRole("USER");
        credentials.setChef(chef);
        chefRepository.save(chef);
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