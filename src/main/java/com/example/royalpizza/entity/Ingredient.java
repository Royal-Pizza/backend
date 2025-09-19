package com.example.royalpizza.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Ingredient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idIngredient;

    @Column(nullable = false, unique = true)
    private String nameIngredient;
}
