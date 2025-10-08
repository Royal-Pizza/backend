package com.example.royalpizza.controller;

import com.example.royalpizza.DTO.LoginDTO;
import com.example.royalpizza.DTO.NewCustomerDTO;
import com.example.royalpizza.entity.Customer;
import com.example.royalpizza.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/customers")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @PostMapping("/login")
    public String login(@RequestBody LoginDTO loginDTO) {
        return customerService.loginCustomer(loginDTO.getEmail(), loginDTO.getPassword());
    }

    @PostMapping("/register")
    public Customer register(@RequestBody NewCustomerDTO newCustomerDTO) {
        return customerService.addCustomer(newCustomerDTO);
    }
}

