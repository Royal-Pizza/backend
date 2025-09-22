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

    // Recuperer la liste des clients non administrateurs
    List<Customer> findByIsAdminFalse();

    // Recuperer la liste des clients par nom
    List<Customer> findByLastNameContainingIgnoreCase(String lastName);

    // Recuperer la liste des clients par prenom
    List<Customer> findByFirstNameContainingIgnoreCase(String firstName);

    // Recuperer un client par son adresse email
    Customer findCustomerByEmailAddress(String email);

    // Supprimer un client par son email
    void deleteByEmailAddress(String email);
}
