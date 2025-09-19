package com.example.royalpizza.repository;

import com.example.royalpizza.entity.OrderLine;
import com.example.royalpizza.entity.OrderLineId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderLineRepository extends JpaRepository<OrderLine, OrderLineId> {

    // Récupérer toutes les lignes d'une facture
    List<OrderLine> findByInvoiceIdInvoice(Long invoiceId);
}
