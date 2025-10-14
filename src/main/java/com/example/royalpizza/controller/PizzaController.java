package com.example.royalpizza.controller;

import com.example.royalpizza.DTO.PizzaDTO;
import com.example.royalpizza.entity.Pizza;
import com.example.royalpizza.mapper.PizzaMapper;
import com.example.royalpizza.service.PizzaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/pizzas")
public class PizzaController {

    @Autowired
    private PizzaService pizzaService;

    @GetMapping
    public List<PizzaDTO> getAllPizzas() {
        List<PizzaDTO> pizzasDto = pizzaService.getAllPizzasDTO();
        return pizzasDto;
    }

    @GetMapping("/{namePizza}")
    public PizzaDTO getPizzaById(@PathVariable String namePizza) {
        return pizzaService.getPizzaDTO(namePizza);
    }

}
