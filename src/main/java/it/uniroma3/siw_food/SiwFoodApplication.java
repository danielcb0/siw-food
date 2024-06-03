package it.uniroma3.siw_food;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "it.uniroma3.siw_food.repository")
@EntityScan(basePackages = "it.uniroma3.siw_food.model")
public class SiwFoodApplication {

	public static void main(String[] args) {
		SpringApplication.run(SiwFoodApplication.class, args);
	}
}
