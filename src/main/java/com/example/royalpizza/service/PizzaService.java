package com.example.royalpizza.service;

import com.example.royalpizza.entity.Pizza;
import com.example.royalpizza.repository.PizzaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PizzaService {

    private final PizzaRepository pizzaRepository;

    public PizzaService(PizzaRepository pizzaRepository) {
        this.pizzaRepository = pizzaRepository;
    }

    // Récupérer toutes les pizzas
    public List<Pizza> getAllPizzas() {
        return pizzaRepository.findAll();
    }

    // Récupérer une pizza par id
    public Optional<Pizza> getPizzaById(Long id) {
        return pizzaRepository.findById(id);
    }

    // Récupérer une pizza par nom
    public Pizza getPizzasByName(String name) {
        List<Pizza> listPizza = pizzaRepository.findByNamePizza(name);
        return listPizza.isEmpty() ? null : listPizza.get(0);
    }

    // Ajouter ou modifier une pizza
    public Pizza savePizza(Pizza pizza) {
        return pizzaRepository.save(pizza);
    }

    // Supprimer une pizza par id
    public void deletePizzaById(Long id) {
        pizzaRepository.deleteById(id);
    }

    // Supprimer une pizza par nom
    public void deletePizzaByName(String name) {
        pizzaRepository.deleteByNamePizza(name);
    }

    public Pizza updatePizza(Long id, Pizza updatedPizza) {
        // Récupérer la pizza existante
        Pizza pizza = pizzaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pizza non trouvée avec id : " + id));

        // Mettre à jour les champs
        pizza.setNamePizza(updatedPizza.getNamePizza());
        pizza.setPricePizza(updatedPizza.getPricePizza());
        pizza.setImage(updatedPizza.getImage());

        // Sauvegarder les modifications
        return pizzaRepository.save(pizza);
    }

}
