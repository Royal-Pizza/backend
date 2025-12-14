package com.example.royalpizza.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.List;

@Data
@Entity
@Table(name = "customer")
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_customer")
    private Long idCustomer;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "email_address", nullable = false, unique = true)
    private String emailAddress;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "wallet", nullable = false)
    private BigDecimal wallet;

    @Column(name = "is_admin", nullable = false)
    private Boolean isAdmin;

    @Column(name = "available", nullable = false)
    private Boolean available;

    @ToString.Exclude
    @OneToMany(mappedBy = "customer")
    @JsonManagedReference
    private List<Invoice> invoices;
}
