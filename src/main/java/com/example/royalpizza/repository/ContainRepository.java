package com.example.royalpizza.repository;

import com.example.royalpizza.entity.Contain;
import com.example.royalpizza.entity.ContainId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContainRepository extends JpaRepository<Contain, ContainId> {

    // Récupérer tous les ingrédients d'une pizza
    List<Contain> findByPizzaIdPizza(Long pizzaId);
}
