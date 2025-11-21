package com.example.royalpizza.DTO;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderLineDTO {
    String namePizza;
    String nameSize;
    BigDecimal price;
    int quantity;
}
