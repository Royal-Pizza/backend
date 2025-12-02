package com.example.royalpizza.DTO;

import lombok.Data;

@Data
public class UpdatedPizzaDTO extends NewPizzaDTO {
    private Long idPizza;
    private boolean available;
}
