package com.example.royalpizza.repository;

import com.example.royalpizza.entity.Customer;
import com.example.royalpizza.entity.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    // Récupérer toutes les factures d'un client
    List<Invoice> findByCustomer(Customer customer);

    // Récupérer toutes les factures par Date
    List<Invoice> findByDate(LocalDateTime dateInvoice);

    // Récupérer toutes les factures par Montant
    List<Invoice> findByTotalAmount(Double totalAmount);
}