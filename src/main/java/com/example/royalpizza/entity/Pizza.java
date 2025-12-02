package com.example.royalpizza.entity;


import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

import java.util.List;


@Data
@Entity
public class Pizza {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idPizza;

    @Column(nullable = false, unique = true)
    private String namePizza;

    @Column(columnDefinition = "BYTEA")
    private byte[] image;

    @Column(nullable = false)
    private boolean available;

    @ToString.Exclude
    @OneToMany(mappedBy = "pizza", cascade = CascadeType.ALL, orphanRemoval = true) // cascade permet de propager les opérations et orphanRemoval supprime les lignes orphelines (ligne orphelines = lignes sans pizza associée)
    private List<Contain> contains;

}
