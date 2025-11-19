package com.example.royalpizza.controller;

import com.example.royalpizza.DTO.OrderLineDTO;
import com.example.royalpizza.config.JwtTokenManager;
import com.example.royalpizza.entity.Customer;
import com.example.royalpizza.service.CustomerService;
import com.example.royalpizza.service.InvoiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public Map<String, Object> buyPizzas(@RequestBody HashMap<String, List<OrderLineDTO>> orderLineDTO) {

        // Récupération du header Authorization
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long idCustomer = (Long) auth.getPrincipal(); // récupéré depuis ton JwtAuthenticationFilter
        Customer customer = customerService.getCustomer(idCustomer);
        System.out.println("Customer ID from token: " + idCustomer);
        String msg = invoiceService.purchase(orderLineDTO, customer);
        Map<String, Object> json = new HashMap<>();
        json.put("message", msg);
        json.put("token", jwtTokenManager.generateToken(customerService.getCustomer(idCustomer)));
        System.out.println(json);
        return json;
    }
}
