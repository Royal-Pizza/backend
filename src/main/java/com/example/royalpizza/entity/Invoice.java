package com.example.royalpizza.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "invoice")
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_invoice")
    private Long idInvoice;

    @Column(name = "date_", nullable = false)
    private LocalDateTime date;

    @Column(name = "total_amount", nullable = false)
    private BigDecimal totalAmount;

    @Column(name = "finalized", nullable = false)
    private boolean finalized;

    @ToString.Exclude
    @ManyToOne
    @JoinColumn(name = "id_customer", nullable = false)
    @JsonBackReference
    private Customer customer;

    @ToString.Exclude
    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL, orphanRemoval = true) // cascade permet de propager les opérations et orphanRemoval supprime les lignes orphelines (ligne orphelines = lignes sans facture associée)
    private List<OrderLine> orderLines;
}
