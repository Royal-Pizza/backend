package com.example.royalpizza.repository;

import com.example.royalpizza.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    // Récupérer un client par email
    Optional<Customer> findByEmailAddress(String email);

    // Recuperer la liste des clients administrateurs
    List<Customer> findByIsAdminTrue();
}
