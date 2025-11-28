package com.example.royalpizza.DTO;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class AdaptedOrderLine {
    private String nameSize;
    private int quantity;
    private BigDecimal price;
}
