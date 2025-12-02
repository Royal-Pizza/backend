package com.example.royalpizza.service;

import com.example.royalpizza.DTO.NewPizzaDTO;
import com.example.royalpizza.DTO.PizzaDTO;
import com.example.royalpizza.DTO.UpdatedPizzaDTO;
import com.example.royalpizza.entity.*;
import com.example.royalpizza.exception.ErrorMessages;
import com.example.royalpizza.exception.PizzaAndIngredientException;
import com.example.royalpizza.mapper.PizzaMapper;
import com.example.royalpizza.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class PizzaService {

    private final PizzaRepository pizzaRepository;
    private final ContainRepository containRepository;
    private final OrderLineRepository orderLineRepository;
    private final SizeRepository sizeRepository;
    private final PriceRepository priceRepository;

    private final PriceService priceService;
    private final IngredientService ingredientService;

    public PizzaService(PizzaRepository pizzaRepository, ContainRepository containRepository, OrderLineRepository orderLineRepository, SizeRepository sizeRepository, PriceService priceService, IngredientRepository ingredientRepository, PriceRepository priceRepository, IngredientService ingredientService) {
        this.pizzaRepository = pizzaRepository;
        this.containRepository = containRepository;
        this.orderLineRepository = orderLineRepository;
        this.sizeRepository = sizeRepository;
        this.priceRepository = priceRepository;
        this.priceService = priceService;
        this.ingredientService = ingredientService;
    }

    public List<PizzaDTO> getAllPizzasDTOAvailable() {
        List<Pizza> pizzas = pizzaRepository.findByAvailableTrueOrderByNamePizzaAsc();
        return pizzas.stream().map(pizza -> {
            return getPizzaDTO(pizza.getIdPizza());
        }).toList();
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

    public PizzaDTO getPizzaDTO(Object object) {
        Pizza pizza = getPizza(object);
        if (pizza != null) {
            PizzaDTO pizzaDTO = PizzaMapper.toDTO(pizza);
            pizzaDTO.setIngredients(getIngredientsFromPizza(pizzaDTO.getIdPizza()));
            pizzaDTO.setPricePizza(getPriceRangeByPizzaAtDate(pizzaDTO.getIdPizza(), LocalDateTime.now()));
            return pizzaDTO;
        }
        return null;
    }

    @Transactional
    public void addPizza(NewPizzaDTO newPizzaDTO) {
        Pizza newPizza = PizzaMapper.toEntity(newPizzaDTO);
        Pizza pizza = this.getPizza(newPizza.getNamePizza());

        // Vérification si le nom de pizza existe déjà
        if (getPizza(pizza.getNamePizza()) != null) {
            if(!pizza.isAvailable()){
                throw new PizzaAndIngredientException(ErrorMessages.PIZZA_UNAVAILABLE + " : " + newPizzaDTO.getNamePizza());
            }
        } else {
            throw new PizzaAndIngredientException(ErrorMessages.PIZZA_ALREADY_EXISTS + " : " + newPizzaDTO.getNamePizza());
        }

        savePizzaWithIngredientsAndPrice(newPizza, newPizzaDTO.getIngredients(), newPizzaDTO.getPricePizza());
    }

    @Transactional
    public void updatePizza(UpdatedPizzaDTO updatedPizzaDTO) {
        Pizza pizza = getPizza(updatedPizzaDTO.getIdPizza());
        if (pizza == null) {
            throw new PizzaAndIngredientException(ErrorMessages.PIZZA_NOT_FOUND);
        }

        // Vérifie qu'aucune autre pizza n'a déjà ce nom
        Optional<Pizza> otherPizzaWithSameName = pizzaRepository.findByNamePizza(updatedPizzaDTO.getNamePizza())
                .stream()
                .filter(p -> !p.getIdPizza().equals(pizza.getIdPizza()))
                .findAny();

        if (otherPizzaWithSameName.isPresent()) {
            throw new PizzaAndIngredientException(ErrorMessages.PIZZA_ALREADY_EXISTS + " : " + updatedPizzaDTO.getNamePizza());
        }

        pizza.setNamePizza(updatedPizzaDTO.getNamePizza());
        pizza.setImage(updatedPizzaDTO.getImage());
        pizza.setAvailable(updatedPizzaDTO.isAvailable());

        savePizzaWithIngredientsAndPrice(pizza, updatedPizzaDTO.getIngredients(), updatedPizzaDTO.getPricePizza());
    }

    public Map<String, BigDecimal> getPriceRangeByPizzaAtDate(Object idPizza, LocalDateTime localDateTime) {
        Pizza pizza = getPizza(idPizza);
        Price pricePizza = this.priceRepository.findTopByPizzaAndValidFromLessThanEqualOrderByValidFromDesc(pizza, localDateTime)
                .orElse(null);
        if(pricePizza != null) {
            List<Size> sizes = sizeRepository.findAll();
            Map<String, BigDecimal> map = new HashMap<>();
            for (Size size : sizes) {
                map.put(size.getNameSize(), pricePizza.getValue().multiply(BigDecimal.valueOf(size.getCoeff())).setScale(2, RoundingMode.HALF_UP));
            }
            return map;
        } else {
            PizzaAndIngredientException exception = new
            PizzaAndIngredientException(ErrorMessages.PRICE_NOT_FOUND + " : " + pizza.getNamePizza() + " à la date " + localDateTime.toString());
            System.err.println(exception.getMessage());
            throw exception;

        }

    }

    // recuperer les ingredients d'une pizza
    private List<String> getIngredientsFromPizza(Object idPizza){
        Pizza pizza = this.getPizza(idPizza);
        if (pizza != null) {
            return containRepository.findByPizzaIdPizza(pizza.getIdPizza())
                    .stream()
                    .map(contain -> contain.getIngredient().getNameIngredient())
                    .toList();
        }
        return null;
    }

    private void savePizzaWithIngredientsAndPrice(Pizza pizza, List<String> ingredientsNames, BigDecimal price) {
        ingredientsNames.sort(String::compareTo);
        if (pizza.getContains() == null) {
            pizza.setContains(new ArrayList<>());
        }
        pizza.getContains().clear();

        List<Contain> contains = new ArrayList<>();
        for (String ingredientName : ingredientsNames) {
            Ingredient ingredient = ingredientService.getIngredient(ingredientName);
            if (ingredient != null) {
                Contain contain = new Contain();
                contain.setPizza(pizza);
                contain.setIngredient(ingredient);
                contains.add(contain);
            } else {
                throw new PizzaAndIngredientException(ErrorMessages.INGREDIENT_NOT_FOUND + " : " + ingredientName);
            }
        }
        pizza.getContains().addAll(contains);

        pizzaRepository.save(pizza);
        // Sauvegarde du prix
        priceService.savePriceOfPizza(pizza, price);
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
