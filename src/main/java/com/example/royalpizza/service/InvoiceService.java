package com.example.royalpizza.service;

import com.example.royalpizza.entity.*;
import com.example.royalpizza.repository.InvoiceRepository;
import com.example.royalpizza.repository.OrderLineRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final OrderLineRepository orderLineRepository;
    private final CustomerService customerService;

    public InvoiceService(InvoiceRepository invoiceRepository, OrderLineRepository orderLineRepository, CustomerService customerService) {
        this.invoiceRepository = invoiceRepository;
        this.orderLineRepository = orderLineRepository;
        this.customerService = customerService;
    }

    public Invoice getInvoiceById(Long id) {
        return invoiceRepository.findById(id).orElse(null);
    }

    public List<OrderLine> getOrderLinesByInvoiceId(Long invoiceId) {
        return orderLineRepository.findByInvoiceIdInvoice(invoiceId);
    }

    public List<Invoice> getAllInvoicesByCustomer(Object customerId) {
        Customer customer = customerService.getCustomer(customerId);
        return invoiceRepository.findByCustomer(customer);
    }

    public Customer getBestCustomer() {
        List<Invoice> invoices = invoiceRepository.findAll();
        // on recupere le meilleur client, cad, celui qui a la somme totale la plus elevee de factures
        return invoices.stream()
                .collect(java.util.stream.Collectors.groupingBy(Invoice::getCustomer, java.util.stream.Collectors.summingDouble(inv -> inv.getTotalAmount().doubleValue())))
                .entrySet()
                .stream()
                .max(java.util.Map.Entry.comparingByValue())
                .map(java.util.Map.Entry::getKey)
                .orElse(null);
    }





}
