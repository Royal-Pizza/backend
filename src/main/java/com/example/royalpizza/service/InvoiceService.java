package com.example.royalpizza.service;

import com.example.royalpizza.DTO.OrderLineDTO;
import com.example.royalpizza.entity.*;
import com.example.royalpizza.exception.CustomerException;
import com.example.royalpizza.repository.InvoiceRepository;
import com.example.royalpizza.repository.OrderLineRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final OrderLineRepository orderLineRepository;
    private final CustomerService customerService;
    private final PizzaService pizzaService;
    private final SizeService sizeService;

    public InvoiceService(InvoiceRepository invoiceRepository, OrderLineRepository orderLineRepository, CustomerService customerService, PizzaService pizzaService, SizeService sizeService) {
        this.invoiceRepository = invoiceRepository;
        this.orderLineRepository = orderLineRepository;
        this.customerService = customerService;
        this.pizzaService = pizzaService;
        this.sizeService = sizeService;
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

    @Transactional
    public String purchase(HashMap<String, ArrayList<OrderLineDTO>> orders, Customer customer) {
        BigDecimal totalAmount = BigDecimal.ZERO;
        List<OrderLine> listeOrderLine = new ArrayList<OrderLine>();
        for (String pizzaName : orders.keySet()) {
            Pizza pizza = pizzaService.getPizza(pizzaName);
            ArrayList<OrderLineDTO> orderLines = orders.get(pizzaName);
            Map<String, BigDecimal> priceByPizza = pizzaService.getPriceRangeByPizza(pizzaName);
            for (OrderLineDTO orderLineDTO : orderLines) {
                Size size = sizeService.getSize(orderLineDTO.getSizeName());
                BigDecimal priceForPizzaSize = priceByPizza.get(size.getNameSize());
                totalAmount = totalAmount.add(priceForPizzaSize.multiply(BigDecimal.valueOf(orderLineDTO.getQuantity())));
                OrderLine orderLine = new OrderLine();
                orderLine.setPizza(pizza);
                orderLine.setSize(size);
                orderLine.setQuantity(orderLineDTO.getQuantity());
                listeOrderLine.add(orderLine);
            }
        }
        if (totalAmount.compareTo(customer.getWallet()) > 0) {
            throw new CustomerException("Fonds insuffisants pour effectuer cet achat.");
        }
        customer.setWallet(customer.getWallet().subtract(totalAmount));
        customerService.updateCustomer(customer, false);
        Invoice invoice = new Invoice();
        invoice.setTotalAmount(totalAmount);
        invoice.setCustomer(customer);
        invoice.setDate(LocalDateTime.now());
        this.saveInvoice(invoice, listeOrderLine);
        return "Transaction réussie. Montant total : " + totalAmount + "€. Votre nouveau solde est de " + customer.getWallet() + "€.";
    }

    private void saveInvoice(Invoice invoice, List<OrderLine> orderLines) {
        for( OrderLine orderLine : orderLines) {
            orderLine.setInvoice(invoice);
        }
        invoice.setOrderLines(orderLines);
        invoiceRepository.save(invoice);
    }

}
