package it.uniroma3.siw_food.controller;

import it.uniroma3.siw_food.model.Chef;
import it.uniroma3.siw_food.service.ChefService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;

/**
 * This controller handles the home page of the application.
 */
@Controller
public class HomeController {

    @Autowired
    private ChefService chefService;

    /**
     * Displays the home page.
     *
     * @param model the model to add attributes to
     * @return the home page view
     */
    @GetMapping("/")
    public String home(Model model) {
        Chef chef = chefService.getAuthenticatedChef();
        if (chef != null) {
            model.addAttribute("chefId", chef.getId());
        }
        return "index";
    }
}
