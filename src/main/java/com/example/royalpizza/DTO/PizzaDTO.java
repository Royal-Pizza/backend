package com.example.royalpizza.DTO;

import com.example.royalpizza.entity.Ingredient;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
public class PizzaDTO {
    private Long idPizza;
    private String namePizza;
    private Map<String, BigDecimal> pricePizza;
    private List<String> ingredients;
    private byte[] image;
}
