package com.example.royalpizza.controller;

import com.example.royalpizza.DTO.OrderLineDTO;
import com.example.royalpizza.config.JwtTokenManager;
import com.example.royalpizza.entity.Customer;
import com.example.royalpizza.exception.CustomerException;
import com.example.royalpizza.service.CustomerService;
import com.example.royalpizza.service.InvoiceService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;

@RestController
@RequestMapping("/purchases")
public class InvoiceController {

    @Autowired
    private InvoiceService invoiceService;

    @Autowired
    private JwtTokenManager jwtTokenManager;

    @Autowired
    private CustomerService customerService;

    @PostMapping("/buy")
    public String buyPizzas(@RequestBody HashMap<String, ArrayList<OrderLineDTO>> orderLineDTO, HttpServletRequest request) {

        // Récupération du header Authorization
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new CustomerException("Aucun token JWT fourni.");
        }

        String token = authHeader.substring(7);

        // Extraction du client depuis le token
        Long idCustomer = jwtTokenManager.parseToken(token);
        Customer customer = customerService.getCustomer(idCustomer);
        System.out.println("Customer ID from token: " + idCustomer);
        return invoiceService.purchase(orderLineDTO, customer);
    }
}
