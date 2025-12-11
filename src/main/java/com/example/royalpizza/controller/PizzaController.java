package com.example.royalpizza.controller;

import com.example.royalpizza.DTO.NewPizzaDTO;
import com.example.royalpizza.DTO.PizzaDTO;
import com.example.royalpizza.DTO.UpdatedPizzaDTO;
import com.example.royalpizza.exception.CustomerException;
import com.example.royalpizza.exception.ErrorMessages;
import com.example.royalpizza.service.PizzaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pizzas")
public class PizzaController {

    @Autowired
    private PizzaService pizzaService;

    @GetMapping
    public List<PizzaDTO> getAllPizzasAvailable() {
        List<PizzaDTO> pizzasDto = pizzaService.getAllPizzasDTOAvailable();
        return pizzasDto;
    }

    @GetMapping("/{namePizza}")
    public PizzaDTO getPizzaById(@PathVariable String namePizza) {
        return pizzaService.getPizzaDTOAvailable(namePizza);
    }

    @GetMapping("/all")
    public List<PizzaDTO> getAllPizzas() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if(auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            throw new CustomerException(ErrorMessages.NOT_AUTHORIZED);
        }
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        if(!isAdmin) {
            throw new CustomerException(ErrorMessages.NOT_AUTHORIZED);
        }
        List<PizzaDTO> pizzasDto = pizzaService.getAllPizzasDTO();
        return pizzasDto;
    }

    @PostMapping("/add")
    public void addPizza(@RequestBody NewPizzaDTO newPizzaDTO) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        if(!isAdmin) {
            throw new CustomerException(ErrorMessages.NOT_AUTHORIZED);
        }
        pizzaService.addPizza(newPizzaDTO);
    }

    @PostMapping("/update")
        public void updatePizza(@RequestBody UpdatedPizzaDTO updatedPizzaDTO) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if(auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            throw new CustomerException(ErrorMessages.NOT_AUTHORIZED);
        }
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        if(!isAdmin) {
            throw new CustomerException(ErrorMessages.NOT_AUTHORIZED);
        }
        pizzaService.updatePizza(updatedPizzaDTO);
    }
}
