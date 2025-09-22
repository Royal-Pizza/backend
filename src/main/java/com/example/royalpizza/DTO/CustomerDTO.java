package com.example.royalpizza.DTO;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CustomerDTO {
    private Long idCustomer;
    private String firstName;
    private String lastName;
    private String emailAddress;
    private BigDecimal wallet;
    private boolean isAdmin;
}
