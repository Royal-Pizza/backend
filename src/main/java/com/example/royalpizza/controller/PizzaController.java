package com.example.royalpizza.controller;

import com.example.royalpizza.DTO.PizzaDTO;
import com.example.royalpizza.entity.Pizza;
import com.example.royalpizza.mapper.PizzaMapper;
import com.example.royalpizza.service.PizzaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pizzas")
public class PizzaController {

    @Autowired
    private PizzaService pizzaService;

    @GetMapping
    public List<Pizza> getAllPizzas() {
        return pizzaService.getAllPizzas();
    }

    @GetMapping("/{namePizza}")
    public PizzaDTO getPizzaById(@PathVariable String namePizza) {
        Pizza pizza = pizzaService.getPizza(namePizza);
        PizzaDTO pizzaDTO = PizzaMapper.toDTO(pizza);
        pizzaDTO.setIngredients(pizzaService.getIngredientsFromPizza(pizzaDTO.getIdPizza()));
        pizzaDTO.setPricePizza(pizzaService.getPriceRangeByPizza(pizzaDTO.getIdPizza()));
        return pizzaDTO;
    }


}
