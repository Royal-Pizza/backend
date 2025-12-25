package com.example.royalpizza.repository;

import com.example.royalpizza.entity.Contain;
import com.example.royalpizza.entity.ContainId;
import com.example.royalpizza.entity.Pizza;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContainRepository extends JpaRepository<Contain, ContainId> {

    // Récupérer tous les ingrédients d'une pizza
    List<Contain> findByPizzaIdPizza(Long pizzaId);

    List<Contain> findByIngredientIdIngredient(Long ingredientId);

    // Supprimer tous les ingrédients d'une pizza
    @Transactional
    @Modifying
    @Query("DELETE FROM Contain c WHERE c.pizza = :pizza")
    void deleteByPizza(@Param("pizza") Pizza pizza);

}
