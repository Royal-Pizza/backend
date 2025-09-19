package com.example.royalpizza.repository;

import com.example.royalpizza.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    //Rechercher un client par son nom
    Optional<Customer> findByLastName(String lastName);

    // Récupérer un client par email
    Optional<Customer> findByEmailAddress(String email);

    // Vérifier si un email existe
    boolean existsByEmailAddress(String email);

    // Recuperer la liste des clients administrateurs
    List<Customer> findByIsAdminTrue();

    // Recuperer la liste des clients non administrateurs
    List<Customer> findByIsAdminFalse();

    // Recuperer la liste des clients par nom
    List<Customer> findByLastNameContainingIgnoreCase(String lastName);

    // Recuperer la liste des clients par prenom
    List<Customer> findByFirstNameContainingIgnoreCase(String firstName);
}
