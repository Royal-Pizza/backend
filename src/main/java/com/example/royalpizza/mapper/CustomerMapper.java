package com.example.royalpizza.mapper;

import com.example.royalpizza.DTO.CustomerDTO;
import com.example.royalpizza.DTO.NewCustomerDTO;
import com.example.royalpizza.entity.Customer;

import java.math.BigDecimal;

public class CustomerMapper {

    // ========== Customer -> CustomerDTO ==========
    public static CustomerDTO toDto(Customer customer) {
        if (customer == null) return null;
        CustomerDTO dto = new CustomerDTO();
        dto.setEmailAddress(customer.getEmailAddress());
        dto.setFirstName(customer.getFirstName());
        dto.setLastName(customer.getLastName());
        dto.setWallet(customer.getWallet());
        dto.setAdmin(customer.getIsAdmin());
        return dto;
    }

    public static Customer toEntity(CustomerDTO dto) {
        if (dto == null) return null;
        Customer customer = new Customer();
        customer.setFirstName(dto.getFirstName());
        customer.setLastName(dto.getLastName());
        customer.setEmailAddress(dto.getEmailAddress());
        customer.setWallet(dto.getWallet());
        customer.setIsAdmin(dto.isAdmin());
        // le mot de passe n'est pas dans le DTO, à changer dans le service
        return customer;
    }

    public static Customer toEntity(NewCustomerDTO dto) {
        if (dto == null) return null;
        Customer customer = new Customer();
        customer.setFirstName(dto.getFirstName());
        customer.setLastName(dto.getLastName());
        customer.setEmailAddress(dto.getEmailAddress());
        customer.setPassword(dto.getPassword());
        customer.setWallet(BigDecimal.valueOf(100.00));
        customer.setIsAdmin(false);
        return customer;
    }
}
