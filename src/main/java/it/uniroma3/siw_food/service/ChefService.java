package it.uniroma3.siw_food.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import it.uniroma3.siw_food.model.Chef;
import it.uniroma3.siw_food.repository.ChefRepository;

@Service
public class ChefService {

    @Autowired
    private ChefRepository chefRepository;

    public Chef getChef(Long id) {
        return chefRepository.findById(id).orElse(null);
    }

    public Chef saveChef(Chef chef) {
        return chefRepository.save(chef);
    }
}
