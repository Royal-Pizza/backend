package com.example.royalpizza.DTO;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderLineDTO {
    private String sizeName;
    private int quantity;
    private BigDecimal price;
}
