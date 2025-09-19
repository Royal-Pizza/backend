package com.example.royalpizza.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "contain")
@IdClass(ContainId.class)
public class Contain {

    @Id
    @ManyToOne
    @JoinColumn(name = "id_pizza", nullable = false)
    private Pizza pizza;

    @Id
    @ManyToOne
    @JoinColumn(name = "id_ingredient", nullable = false)
    private Ingredient ingredient;
}
