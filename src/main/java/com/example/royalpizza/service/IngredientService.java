package com.example.royalpizza.service;

import com.example.royalpizza.entity.Ingredient;
import com.example.royalpizza.repository.IngredientRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class IngredientService {

    private IngredientRepository ingredientRepository;

    public IngredientService(IngredientRepository ingredientRepository) {
        this.ingredientRepository = ingredientRepository;
    }

    public Ingredient getIngredient(Object object)
    {
        if (object instanceof Long id) {
            Optional<Ingredient> ingredientOptional = ingredientRepository.findById(id);
            return ingredientOptional.orElse(null);
        } else if (object instanceof String name) {
            Optional<Ingredient> ingredientOptional = ingredientRepository.findByNameIngredient(name);
            return ingredientOptional.orElse(null);
        }
        return null;
    }

    public List<Ingredient> getAllIngredients() {
        return ingredientRepository.findAll();
    }

    public void addIngredient(Ingredient ingredient) {
        ingredientRepository.save(ingredient);
    }

    public Ingredient updateIngredient(Long idIngredient, Ingredient ingredient) {
        Ingredient existingIngredient = ingredientRepository.findById(idIngredient).orElse(null);
        if (existingIngredient != null) {
            existingIngredient.setNameIngredient(ingredient.getNameIngredient());
            return ingredientRepository.save(existingIngredient);
        }
        else{
            throw new RuntimeException("Ingrédient non trouvé avec l'id : " + idIngredient);
        }
    }
}
