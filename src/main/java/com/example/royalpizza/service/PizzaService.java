package com.example.royalpizza.service;

import com.example.royalpizza.entity.Ingredient;
import com.example.royalpizza.entity.OrderLine;
import com.example.royalpizza.entity.Pizza;
import com.example.royalpizza.entity.Size;
import com.example.royalpizza.repository.*;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class PizzaService {

    private final PizzaRepository pizzaRepository;
    private final ContainRepository containRepository;
    private final OrderLineRepository orderLineRepository;
    private final SizeRepository sizeRepository;

    public PizzaService(PizzaRepository pizzaRepository, ContainRepository containRepository, OrderLineRepository orderLineRepository, SizeRepository sizeRepository) {
        this.pizzaRepository = pizzaRepository;
        this.containRepository = containRepository;
        this.orderLineRepository = orderLineRepository;

        this.sizeRepository = sizeRepository;
    }

    // Récupérer toutes les pizzas
    public List<Pizza> getAllPizzas() {
        return pizzaRepository.findAll();
    }

    public Pizza getPizza(Object object) {
        Optional<Pizza> pizzaOpt;

        if (object instanceof Long) {
            pizzaOpt = pizzaRepository.findById((Long) object);
        } else if (object instanceof String) {
            pizzaOpt = pizzaRepository.findByNamePizza((String) object).stream().findFirst();
        } else {
            throw new IllegalArgumentException("Type d'identifiant non supporté pour la pizza : " + object);
        }

        return pizzaOpt.orElse(null);
    }

    public Map<String, BigDecimal> getPriceRangeByPizza(Object idPizza) {
        Pizza pizza = getPizza(idPizza);
        List<Size> sizes = sizeRepository.findAll();
        Map<String, BigDecimal> map = new HashMap<>();
        for (Size size : sizes) {
            map.put(size.getNameSize(), pizza.getPricePizza().multiply(BigDecimal.valueOf(size.getCoeff())).setScale(2, RoundingMode.HALF_UP));
        }
        return map;
    }

    public void addPizza(Pizza pizza) {
        pizzaRepository.save(pizza);

    }



    public Pizza updatePizza(Long idPizza, Pizza updatedPizza) {

        Pizza pizza = pizzaRepository.findById(idPizza).orElse(null);
        if(pizza == null) {
            throw new RuntimeException("Pizza non trouvée avec l'id : " + idPizza);
        }
        // Mettre à jour les champs
        pizza.setNamePizza(updatedPizza.getNamePizza());
        pizza.setPricePizza(updatedPizza.getPricePizza());
        pizza.setImage(updatedPizza.getImage());

        // Sauvegarder les modifications
        return pizzaRepository.save(pizza);
    }

    // recuperer les ingredients d'une pizza
    public List<String> getIngredientsFromPizza(Object idPizza){
        Pizza pizza = this.getPizza(idPizza);
        if (pizza != null) {
            return containRepository.findByPizzaIdPizza(pizza.getIdPizza())
                    .stream()
                    .map(contain -> contain.getIngredient().getNameIngredient())
                    .toList();
        }
        return null;
    }

    public Pizza getBestSellingPizza() {
        List<OrderLine> orderLines = orderLineRepository.findAll();
        // on recupere la pizza la plus vendue, cad, celle qui a le plus d'occurrences dans les lignes de commande
        return orderLines.stream()
                .collect(java.util.stream.Collectors.groupingBy(OrderLine::getPizza, java.util.stream.Collectors.summingInt(OrderLine::getQuantity)))
                .entrySet()
                .stream()
                .max(java.util.Map.Entry.comparingByValue())
                .map(java.util.Map.Entry::getKey)
                .orElse(null);
    }
}
