package com.example.royalpizza.service;

import com.example.royalpizza.DTO.AdaptedOrderLine;
import com.example.royalpizza.entity.*;
import com.example.royalpizza.exception.CustomerException;
import com.example.royalpizza.exception.ErrorMessages;
import com.example.royalpizza.repository.InvoiceRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final CustomerService customerService;
    private final PizzaService pizzaService;
    private final SizeService sizeService;

    public InvoiceService(InvoiceRepository invoiceRepository, CustomerService customerService, PizzaService pizzaService, SizeService sizeService) {
        this.invoiceRepository = invoiceRepository;
        this.customerService = customerService;
        this.pizzaService = pizzaService;
        this.sizeService = sizeService;
    }

    public List<Invoice> getAllInvoicesByCustomer(Object customerId) {
        Customer customer = customerService.getCustomer(customerId);
        return invoiceRepository.findByCustomerAndFinalizedTrue(customer);
    }


    @Transactional
    public Invoice saveBasket(Map<String, List<AdaptedOrderLine>> orders, Customer customer) {
        Optional<Invoice> invoiceOpt = invoiceRepository
                .findFirstByCustomerAndFinalizedFalseOrderByDateDesc(customer);
        Invoice invoice = null;
        if (!invoiceOpt.isPresent()) {
            System.out.println("Creating new invoice for customer ID: " + customer.getIdCustomer());
            invoice = new Invoice();
        } else {
            invoice = invoiceOpt.get();
        }
        BigDecimal totalAmount = BigDecimal.ZERO;
        List<OrderLine> listeOrderLine = new ArrayList<>();
        for (String pizzaName : orders.keySet()) {
            Pizza pizza = pizzaService.getPizza(pizzaName);
            List<AdaptedOrderLine> orderLines = orders.get(pizzaName);
            Map<String, BigDecimal> priceByPizza = pizzaService.getPriceRangeByPizzaAtDate(pizzaName, LocalDateTime.now());
            for (AdaptedOrderLine adaptedOrderLineDTO : orderLines) {
                Size size = sizeService.getSize(adaptedOrderLineDTO.getNameSize());
                BigDecimal priceForPizzaSize = priceByPizza.get(size.getNameSize());
                totalAmount = totalAmount.add(priceForPizzaSize.multiply(BigDecimal.valueOf(adaptedOrderLineDTO.getQuantity())));
                OrderLine orderLine = new OrderLine();
                orderLine.setPizza(pizza);
                orderLine.setSize(size);
                orderLine.setQuantity(adaptedOrderLineDTO.getQuantity());
                listeOrderLine.add(orderLine);
            }
        }

        invoice.setTotalAmount(totalAmount);
        invoice.setCustomer(customer);
        invoice.setDate(LocalDateTime.now());
        invoice.setFinalized(false);
        this.saveInvoice(invoice, listeOrderLine);
        return invoice;
    }

    @Transactional
    public String purchase(HashMap<String, List<AdaptedOrderLine>> orders, Customer customer) {
        Invoice invoice = this.saveBasket(orders, customer);
        invoice.setFinalized(true);
        invoiceRepository.save(invoice);
        if (invoice.getTotalAmount().compareTo(customer.getWallet()) >= 0) {
            log.error("Insufficient balance for customer ID: " + customer.getIdCustomer());
            throw new CustomerException(ErrorMessages.INSUFFICIENT_BALANCE);
        }
        customer.setWallet(customer.getWallet().subtract(invoice.getTotalAmount()));
        customerService.updateCustomer(customer);
        // Toujours travailler sur la collection existante
        return "Transaction réussie. Montant total : " + invoice.getTotalAmount() + "€. Votre nouveau solde est de " + customer.getWallet() + "€.";
    }

    private void saveInvoice(Invoice invoice, List<OrderLine> orderLines) {
        for (OrderLine orderLine : orderLines) {
            orderLine.setInvoice(invoice);
        }

        if (invoice.getOrderLines() == null) {
            invoice.setOrderLines(new ArrayList<>());
        }

        // Ne pas faire invoice.setOrderLines(orderLines) car cela créerait une nouvelle collection, car 'orphanRemoval' est à true.
        invoice.getOrderLines().clear();
        invoice.getOrderLines().addAll(orderLines);

        invoiceRepository.save(invoice);
    }

}
