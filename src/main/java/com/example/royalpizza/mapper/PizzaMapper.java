package com.example.royalpizza.mapper;

import com.example.royalpizza.DTO.NewPizzaDTO;
import com.example.royalpizza.DTO.PizzaDTO;
import com.example.royalpizza.DTO.UpdatedPizzaDTO;
import com.example.royalpizza.entity.Pizza;

import java.util.Base64;

public class PizzaMapper {

    // le prix et la liste des ingrédients ne sont pas mappés ici
    public static Pizza toEntity(NewPizzaDTO newPizzaDTO) {
        if (newPizzaDTO == null)
            return null;
        Pizza pizza = new Pizza();
        pizza.setNamePizza(newPizzaDTO.getNamePizza());
        if (newPizzaDTO.getImage() != null && !newPizzaDTO.getImage().isEmpty()) {
            String base64 = newPizzaDTO.getImage();

            // Si l'image contient un header data:image/...;base64,
            // on supprime tout avant la virgule
            if (base64.contains(",")) {
                base64 = base64.substring(base64.indexOf(",") + 1);
            }

            pizza.setImage(Base64.getDecoder().decode(base64));
        }
        pizza.setAvailable(true);
        if(newPizzaDTO instanceof UpdatedPizzaDTO updatedPizzaDTO) {
            pizza.setIdPizza(updatedPizzaDTO.getIdPizza());
            pizza.setAvailable(updatedPizzaDTO.isAvailable());
        }
        return pizza;
    }

    public static PizzaDTO toDTO(Pizza pizza) {
        if (pizza == null)
            return null;
        PizzaDTO pizzaDTO = new PizzaDTO();
        pizzaDTO.setIdPizza(pizza.getIdPizza());
        pizzaDTO.setNamePizza(pizza.getNamePizza());
        pizzaDTO.setImage(pizza.getImage());
        pizzaDTO.setAvailable(pizza.isAvailable());
        return pizzaDTO;
    }
}
