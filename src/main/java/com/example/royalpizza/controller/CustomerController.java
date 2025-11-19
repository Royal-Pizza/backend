package com.example.royalpizza.controller;

import com.example.royalpizza.DTO.LoginDTO;
import com.example.royalpizza.DTO.NewCustomerDTO;
import com.example.royalpizza.DTO.OrderLineDTO;
import com.example.royalpizza.entity.Customer;
import com.example.royalpizza.exception.CustomerException;
import com.example.royalpizza.service.CustomerService;
import com.example.royalpizza.service.InvoiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/customers")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @Autowired
    private InvoiceService invoiceService;

    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody LoginDTO loginDTO) throws CustomerException {
        String token = customerService.loginCustomer(loginDTO.getEmail(), loginDTO.getPassword());
        Map<String, List<OrderLineDTO>> basket = customerService.getBasket(customerService.getCustomer(loginDTO.getEmail()));
        Map<String, Object> map = new HashMap<>();
        map.put("token", token);
        map.put("basket", basket);
        return map;
    }

    @PostMapping("/logout")
    public void logout(@RequestBody Map<String, List<OrderLineDTO>> basket) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long idCustomer = (Long) auth.getPrincipal();
        this.invoiceService.saveBasket(basket, customerService.getCustomer(idCustomer));
    }

    @PostMapping("/register")
    public Customer register(@RequestBody NewCustomerDTO newCustomerDTO) {
        return customerService.addCustomer(newCustomerDTO);
    }
}

