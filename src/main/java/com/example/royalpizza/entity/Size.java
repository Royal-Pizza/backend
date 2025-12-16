package com.example.royalpizza.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class Size {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idSize;

    @Column(nullable = false, unique = true)
    private String nameSize;

    @Column(nullable = false)
    private double coeff;
}

