package com.example.royalpizza.service;

import com.example.royalpizza.DTO.AdaptedOrderLine;
import com.example.royalpizza.entity.*;
import com.example.royalpizza.exception.CustomerException;
import com.example.royalpizza.exception.ErrorMessages;
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
        return orderLineRepository.findByInvoice_IdInvoice(invoiceId);
    }

    public List<Invoice> getAllInvoicesByCustomer(Object customerId) {
        Customer customer = customerService.getCustomer(customerId);
        return invoiceRepository.findByCustomerAndFinalizedTrue(customer);
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
    public Invoice saveBasket(Map<String, List<AdaptedOrderLine>> orders, Customer customer){
        Invoice invoice = invoiceRepository
                .findFirstByCustomerAndFinalizedFalseOrderByDateDesc(customer)
                .orElseGet(Invoice::new);
        BigDecimal totalAmount = BigDecimal.ZERO;
        List<OrderLine> listeOrderLine = new ArrayList<>();
        for (String pizzaName : orders.keySet()) {
            Pizza pizza = pizzaService.getPizza(pizzaName);
            List<AdaptedOrderLine> orderLines = orders.get(pizzaName);
            Map<String, BigDecimal> priceByPizza = pizzaService.getPriceRangeByPizza(pizzaName);
            for (AdaptedOrderLine adaptedOrderLineDTO : orderLines) {
                Size size = sizeService.getSize(adaptedOrderLineDTO.getSizeName());
                BigDecimal priceForPizzaSize = priceByPizza.get(size.getNameSize());
                totalAmount = totalAmount.add(priceForPizzaSize.multiply(BigDecimal.valueOf(adaptedOrderLineDTO.getQuantity())));
                OrderLine orderLine = new OrderLine();
                orderLine.setPizza(pizza);
                orderLine.setSize(size);
                orderLine.setQuantity(adaptedOrderLineDTO.getQuantity());
                listeOrderLine.add(orderLine);
            }
        }
        if (totalAmount.compareTo(customer.getWallet()) > 0) {
            throw new CustomerException(ErrorMessages.INSUFFICIENT_BALANCE);
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
        customer.setWallet(customer.getWallet().subtract(invoice.getTotalAmount()));
        customerService.updateCustomer(customer, false);
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

        // Ne pas faire invoice.setOrderLines(orderLines) car cela créerait une nouvelle collection vue que orphanRemoval est à true
        invoice.getOrderLines().clear();
        invoice.getOrderLines().addAll(orderLines);

        invoiceRepository.save(invoice);
    }

}
