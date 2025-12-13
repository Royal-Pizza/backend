package com.example.royalpizza.service;

import com.example.royalpizza.entity.Contain;
import com.example.royalpizza.entity.Ingredient;
import com.example.royalpizza.exception.ErrorMessages;
import com.example.royalpizza.exception.PizzaAndIngredientException;
import com.example.royalpizza.repository.ContainRepository;
import com.example.royalpizza.repository.IngredientRepository;
import com.example.royalpizza.repository.PizzaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class IngredientService {

    private IngredientRepository ingredientRepository;
    private ContainRepository containRepository;

    public IngredientService(IngredientRepository ingredientRepository, ContainRepository containRepository) {
        this.containRepository = containRepository;
        this.ingredientRepository = ingredientRepository;
    }

    public Ingredient getIngredient(Object object)
    {
        if (object instanceof Long id) {
            Optional<Ingredient> ingredientOptional = ingredientRepository.findById(id);
            return ingredientOptional.orElse(null);
        } else if (object instanceof String name) {
            return ingredientRepository.findTopByNameIngredient(name);
        }
        return null;
    }

    public List<Ingredient> findByNameIngredientContaining(String namePart) {
        return ingredientRepository.findByNameIngredientContainingOrderByNameIngredientAsc(namePart);
    }

    public List<Ingredient> getAllIngredients() {
        return ingredientRepository.findAllByOrderByNameIngredientAsc();
    }

    public void addIngredient(String ingredientName) {
        Ingredient existingIngredient = ingredientRepository.findTopByNameIngredient(ingredientName);
        if(existingIngredient != null) {
            throw new PizzaAndIngredientException(ErrorMessages.INGREDIENT_ALREADY_EXISTS + " : " + ingredientName);
        } else {
            Ingredient newIngredient = new Ingredient();
            newIngredient.setNameIngredient(ingredientName);
            ingredientRepository.save(newIngredient);
        }
    }

    public Ingredient updateIngredient(Ingredient ingredient) {
        // Chercher par ID
        Ingredient existingIngredient = ingredientRepository.findById(ingredient.getIdIngredient())
                .orElseThrow(() -> new PizzaAndIngredientException(
                        ErrorMessages.INGREDIENT_NOT_SAVED + " : " + ingredient.getNameIngredient()
                ));

        // Vérifier si un autre ingrédient a le même nom
        Ingredient sameNameIngredient = ingredientRepository.findTopByNameIngredient(ingredient.getNameIngredient());
        if (sameNameIngredient != null && !sameNameIngredient.getIdIngredient().equals(ingredient.getIdIngredient())) {
            throw new PizzaAndIngredientException(
                    ErrorMessages.INGREDIENT_ALREADY_EXISTS + " : " + ingredient.getNameIngredient()
            );
        }

        // Mettre à jour
        existingIngredient.setNameIngredient(ingredient.getNameIngredient());
        return ingredientRepository.save(existingIngredient);
    }


    public void deleteIngredient(Long idIngredient) {
        Ingredient ingredient = getIngredient(idIngredient);
        List<Contain> contains = containRepository.findByIngredientIdIngredient(ingredient.getIdIngredient());
        // duplication de la liste pour éviter ConcurrentModificationException
        for (Contain contain : List.copyOf(contains)) {
            containRepository.delete(contain);
        }
        ingredientRepository.delete(ingredient);
    }


}
