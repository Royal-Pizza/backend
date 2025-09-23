package com.example.royalpizza.controller;

import com.example.royalpizza.entity.Pizza;
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

    @GetMapping("/{idPizza}")
    public Pizza getPizzaById(@PathVariable int idPizza) {
        return pizzaService.getPizza(idPizza);
    }


}
