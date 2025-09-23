package com.example.royalpizza.entity;


import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Entity
public class Pizza {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idPizza;

    @Column(nullable = false, unique = true)
    private String namePizza;

    @Column(nullable = false)
    private BigDecimal pricePizza;

    @Lob
    @Column(columnDefinition = "BYTEA")
    private byte[] image;


}
