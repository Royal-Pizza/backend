package com.example.royalpizza.controller;

import com.example.royalpizza.entity.Ingredient;
import com.example.royalpizza.service.IngredientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ingredients")
public class IngredientController {

    @Autowired
    private IngredientService ingredientService;

    @GetMapping
    public List<Ingredient> getAllIngredients() {
        return ingredientService.getAllIngredients();
    }

    @PostMapping("/add")
    public void addIngredient(@RequestBody String nameIngredient) {
        ingredientService.addIngredient(nameIngredient);
    }

    @PostMapping("/delete")
    public void deleteIngredient(@RequestBody Long idIngredient) {
        ingredientService.deleteIngredient(idIngredient);
    }

    @PostMapping("/update")
    public void updateIngredient(@RequestBody Ingredient ingredient) {
        ingredientService.updateIngredient(ingredient);
    }

}
