package com.example.royalpizza.repository;

import com.example.royalpizza.entity.Ingredient;
import com.example.royalpizza.entity.Pizza;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PizzaRepository extends JpaRepository<Pizza, Long> {

    // Méthode personnalisée pour récupérer une pizza par son nom
    List<Pizza> findByNamePizza(String name);

    // Méthode personnalisée pour récupérer toutes les pizzas disponibles triées par nom
    List<Pizza> findByAvailableTrueOrderByNamePizzaAsc();

    // recuperer toutes les pizzas triées par disponibilité puis par nom
    List<Pizza> findAllByOrderByAvailableDescNamePizzaAsc();

    // recuperer les pizzas contenant un ingrédient donné
    List<Pizza> findByContains_Ingredient_NameIngredient(String ingredientName);

    // recuperer la liste des ingrédients pour une pizza
    List<Ingredient> findIngredientsByIdPizza(Long idPizza);
}
