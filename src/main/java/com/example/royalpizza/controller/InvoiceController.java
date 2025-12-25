package com.example.royalpizza.controller;

import com.example.royalpizza.DTO.AdaptedOrderLine;
import com.example.royalpizza.DTO.InvoiceDTO;
import com.example.royalpizza.DTO.OrderLineDTO;
import com.example.royalpizza.config.JwtTokenManager;
import com.example.royalpizza.entity.Customer;
import com.example.royalpizza.entity.Invoice;
import com.example.royalpizza.mapper.InvoiceMapper;
import com.example.royalpizza.service.CustomerService;
import com.example.royalpizza.service.InvoiceService;
import com.example.royalpizza.service.PizzaService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// logger
@Slf4j
@RestController
@RequestMapping("/purchases")
public class InvoiceController {

    @Autowired
    private InvoiceService invoiceService;

    @Autowired
    private JwtTokenManager jwtTokenManager;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private PizzaService pizzaService;

    @Autowired
    private InvoiceMapper invoiceMapper;

    @PostMapping("/buy")
    public Map<String, Object> buyPizzas(@RequestBody HashMap<String, List<AdaptedOrderLine>> orderLineDTO) {

        // Récupération du header Authorization
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long idCustomer = (Long) auth.getPrincipal(); // récupéré depuis ton JwtAuthenticationFilter
        Customer customer = customerService.getCustomer(idCustomer);
        log.info("Customer ID from token: " + idCustomer);
        String msg = invoiceService.purchase(orderLineDTO, customer);
        Map<String, Object> json = new HashMap<>();
        json.put("message", msg);
        json.put("token", jwtTokenManager.generateToken(customerService.getCustomer(idCustomer)));
        log.info(json.toString());
        return json;
    }

    @GetMapping("/invoices/customer/{idCustomer}")
    public List<InvoiceDTO> getAllInvoicesByCustomer(@PathVariable Long idCustomer) {
        List<Invoice> listeInvoices = invoiceService.getAllInvoicesByCustomer(idCustomer);
        List<InvoiceDTO> invoiceDTOs = new ArrayList<>();
        for (Invoice invoice : listeInvoices) {
            InvoiceDTO invoiceDTO = this.invoiceMapper.toDTO(invoice);
            for (OrderLineDTO orderLineDTO : invoiceDTO.getOrderLineDTOs()) {
                BigDecimal price = pizzaService.getPriceRangeByPizzaAtDate(orderLineDTO.getNamePizza(), invoice.getDate()).get(orderLineDTO.getNameSize());
                orderLineDTO.setPrice(price
                );
            }
            invoiceDTOs.add(invoiceDTO);
        }
        return invoiceDTOs;
    }
}
