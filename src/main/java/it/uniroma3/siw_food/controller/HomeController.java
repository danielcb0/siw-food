package it.uniroma3.siw_food.controller;

import it.uniroma3.siw_food.model.Chef;
import it.uniroma3.siw_food.service.ChefService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
@Controller
public class HomeController {


    @Autowired
    private ChefService chefService;

    @GetMapping("/")
    public String home(Model model) {
        Chef chef = chefService.getAuthenticatedChef();
        if (chef != null) {
            model.addAttribute("chefId", chef.getId());
        }
        return "index";
    }
}
