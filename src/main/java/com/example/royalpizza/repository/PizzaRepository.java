package com.example.royalpizza.repository;

import com.example.royalpizza.entity.Pizza;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PizzaRepository extends JpaRepository<Pizza, Long> {

    // Méthode personnalisée pour récupérer une pizza par son nom
    List<Pizza> findByNamePizza(String name);

    // Méthode personnalisée pour supprimer une pizza par son nom
    void deleteByNamePizza(String name);
}
