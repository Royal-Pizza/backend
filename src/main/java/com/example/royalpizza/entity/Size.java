package com.example.royalpizza.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class Size {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idSize;

    @Column(nullable = false)
    private String nameSize;

    @Column(nullable = false)
    private Double coeff;
}

