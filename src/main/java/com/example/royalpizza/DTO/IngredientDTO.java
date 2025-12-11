package com.example.royalpizza.DTO;

import lombok.Data;

@Data
public class IngredientDTO {
    private Long idIngredient;
    private String nameIngredient;
    private boolean present;
}
