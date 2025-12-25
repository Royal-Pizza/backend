package com.example.royalpizza.repository;

import com.example.royalpizza.entity.Ingredient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IngredientRepository extends JpaRepository<Ingredient, Long> {

    List<Ingredient> findAllByOrderByNameIngredientAsc();

    // Récupérer un ingrédient par son nom
    Ingredient findTopByNameIngredient(String name);

    List<Ingredient> findByNameIngredientContainingOrderByNameIngredientAsc(String namePart);

}
