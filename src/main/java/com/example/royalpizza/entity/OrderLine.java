package com.example.royalpizza.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "order_line")
@IdClass(OrderLineId.class) // Clé primaire composite
public class OrderLine {

    @Id
    @ManyToOne
    @JoinColumn(name = "id_pizza", nullable = false)
    private Pizza pizza;

    @Id
    @ManyToOne
    @JoinColumn(name = "id_size", nullable = false)
    private Size size;

    @Id
    @ManyToOne
    @JoinColumn(name = "id_invoice", nullable = false)
    private Invoice invoice;

    @Column(nullable = false)
    private Integer quantity;
}
