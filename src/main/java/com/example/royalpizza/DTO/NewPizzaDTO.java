package com.example.royalpizza.DTO;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class NewPizzaDTO {
    private String namePizza;
    private BigDecimal pricePizza;
    private List<String> ingredients;
    private String image;
}   