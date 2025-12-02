package com.example.royalpizza.repository;

import com.example.royalpizza.entity.Pizza;
import com.example.royalpizza.entity.Price;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface PriceRepository extends JpaRepository<Price, Long> {

    // Récupérer le prix actuel d'une pizza (le plus récent avec validTo null)
    Optional<Price> findTopByPizzaAndValidToIsNullOrderByValidFromDesc(Pizza pizza);

    // Cette méthode permet de trouver le prix valide d'une pizza à une date donnée
    Optional<Price> findTopByPizzaAndValidFromLessThanEqualOrderByValidFromDesc(Pizza pizza, LocalDateTime localDateTime);
}
