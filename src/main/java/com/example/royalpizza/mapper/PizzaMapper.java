package com.example.royalpizza.mapper;

import com.example.royalpizza.DTO.NewPizzaDTO;
import com.example.royalpizza.DTO.PizzaDTO;
import com.example.royalpizza.entity.Pizza;

public class PizzaMapper {

    // le prix et la liste des ingrédients ne sont pas mappés ici
    public static Pizza toEntity(NewPizzaDTO newPizzaDTO) {
        if (newPizzaDTO == null)
            return null;
        Pizza pizza = new Pizza();
        pizza.setNamePizza(newPizzaDTO.getNamePizza());
        pizza.setImage(pizza.getImage());
        return pizza;
    }

    public static PizzaDTO toDTO(Pizza pizza) {
        if (pizza == null)
            return null;
        PizzaDTO pizzaDTO = new PizzaDTO();
        pizzaDTO.setIdPizza(pizza.getIdPizza());
        pizzaDTO.setNamePizza(pizza.getNamePizza());
        pizzaDTO.setImage(pizza.getImage());
        pizzaDTO.setAvailable(pizzaDTO.isAvailable());
        return pizzaDTO;
    }
}
