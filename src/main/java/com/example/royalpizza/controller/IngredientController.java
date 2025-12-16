package com.example.royalpizza.controller;

import com.example.royalpizza.entity.Ingredient;
import com.example.royalpizza.exception.CustomerException;
import com.example.royalpizza.exception.ErrorMessages;
import com.example.royalpizza.service.IngredientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ingredients")
public class IngredientController {

    @Autowired
    private IngredientService ingredientService;

    @GetMapping
    public List<Ingredient> getAllIngredients() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        if(!isAdmin) {
            System.err.println("Unauthorized access attempt to /ingredients");
            throw new CustomerException(ErrorMessages.NOT_AUTHORIZED);
        }
        return ingredientService.getAllIngredients();
    }

    @GetMapping("{namePart}")
    public List<Ingredient> findByNameIngredientContaining(@PathVariable String namePart) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        if(!isAdmin) {
            System.err.println("Unauthorized access attempt to /ingredients/" + namePart);
            throw new CustomerException(ErrorMessages.NOT_AUTHORIZED);
        }
        return ingredientService.findByNameIngredientContaining(namePart);
    }

    @PostMapping("/add")
    public void addIngredient(@RequestBody String nameIngredient) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        if(!isAdmin) {
            System.err.println("Unauthorized access attempt to /ingredients/add");
            throw new CustomerException(ErrorMessages.NOT_AUTHORIZED);
        }
        ingredientService.addIngredient(nameIngredient);
    }

    @PostMapping("/delete")
    public void deleteIngredient(@RequestBody Long idIngredient) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        if(!isAdmin) {
            System.err.println("Unauthorized access attempt to /ingredients/delete");
            throw new CustomerException(ErrorMessages.NOT_AUTHORIZED);
        }
        ingredientService.deleteIngredient(idIngredient);
    }

    @PostMapping("/update")
    public void updateIngredient(@RequestBody Ingredient ingredient) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        if(!isAdmin) {
            System.err.println("Unauthorized access attempt to /ingredients/update");
            throw new CustomerException(ErrorMessages.NOT_AUTHORIZED);
        }
        ingredientService.updateIngredient(ingredient);
    }

}
