package com.example.royalpizza.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
public class Price {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idPrice;

    @ToString.Exclude
    @ManyToOne
    @JoinColumn(name = "id_pizza", nullable = false)
    private Pizza pizza;

    @Column(nullable = false, columnDefinition = "timestamp default now()")
    private LocalDateTime validFrom;

    @Column(name = "value_", nullable = false)
    private BigDecimal value;

    @Column(nullable = true)
    private LocalDateTime validTo;

}
