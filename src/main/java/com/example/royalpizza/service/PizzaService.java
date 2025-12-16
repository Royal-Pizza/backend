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
import org.aspectj.weaver.ast.Or;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PizzaService {

    private final PizzaRepository pizzaRepository;
    private final ContainRepository containRepository;
    private final OrderLineRepository orderLineRepository;
    private final PriceRepository priceRepository;

    private final PriceService priceService;
    private final SizeService sizeService;
    private final IngredientService ingredientService;

    public PizzaService(PizzaRepository pizzaRepository, ContainRepository containRepository,
                        OrderLineRepository orderLineRepository, PriceService priceService,
                        PriceRepository priceRepository, SizeService sizeService,
                        IngredientService ingredientService) {
        this.pizzaRepository = pizzaRepository;
        this.containRepository = containRepository;
        this.orderLineRepository = orderLineRepository;
        this.priceRepository = priceRepository;
        this.priceService = priceService;
        this.sizeService = sizeService;
        this.ingredientService = ingredientService;
    }

    public List<PizzaDTO> getAllPizzasDTOAvailable() {
        List<Pizza> pizzas = pizzaRepository.findByAvailableTrueOrderByNamePizzaAsc();
        return pizzas.stream()
                .map(pizza -> getPizzaDTO(pizza.getIdPizza()))
                .collect(Collectors.toList());
    }

    public List<PizzaDTO> getAllPizzasDTO(){
        List<Pizza> pizzas = pizzaRepository.findAllByOrderByAvailableDescNamePizzaAsc();
        return pizzas.stream()
                .map(pizza -> getPizzaDTO(pizza.getIdPizza()))
                .collect(Collectors.toList());
    }

    public Pizza getPizza(Object object) {
        Optional<Pizza> pizzaOpt;
        if (object instanceof Long) {
            pizzaOpt = pizzaRepository.findById((Long) object);
        } else if (object instanceof String) {
            pizzaOpt = pizzaRepository.findByNamePizza((String) object).stream().findFirst();
        } else {
            System.err.println("getPizza: unsupported identifier type for pizza: " + object);
            throw new IllegalArgumentException("Type d'identifiant non supporté pour la pizza : " + object);
        }
        return pizzaOpt.orElse(null);
    }

    public PizzaDTO getPizzaDTOAvailable(Object object) {
        PizzaDTO pizzaDTO = getPizzaDTO(object);
        if(pizzaDTO != null) {
            if(pizzaDTO.isAvailable()) {
                return pizzaDTO;
            } else {
                System.err.println("Attempt to access an unavailable pizza with getPizzaDTOAvailable: " + pizzaDTO.getNamePizza());
                throw new PizzaAndIngredientException(ErrorMessages.PIZZA_UNAVAILABLE + " : " + pizzaDTO.getNamePizza());
            }
        }
        return null;
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

        Pizza existingPizza = this.getPizza(newPizza.getNamePizza());
        if (existingPizza != null) {
            if (!existingPizza.isAvailable()) {
                System.err.println("Attempt to add a pizza that exists but is unavailable: " + newPizzaDTO.getNamePizza());
                throw new PizzaAndIngredientException(ErrorMessages.PIZZA_UNAVAILABLE + " : " + newPizzaDTO.getNamePizza());
            } else {
                System.err.println("Attempt to add a pizza that already exists: " + newPizzaDTO.getNamePizza());
                throw new PizzaAndIngredientException(ErrorMessages.PIZZA_ALREADY_EXISTS + " : " + newPizzaDTO.getNamePizza());
            }
        }

        savePizzaWithIngredientsAndPrice(newPizza, newPizzaDTO.getIngredients(), newPizzaDTO.getPricePizza());
    }


    @Transactional
    public void updatePizza(UpdatedPizzaDTO updatedPizzaDTO) {
        Pizza pizza = getPizza(updatedPizzaDTO.getIdPizza());
        if (pizza == null) {
            System.err.println("Attempt to update a pizza that does not exist: " + updatedPizzaDTO.getIdPizza());
            throw new PizzaAndIngredientException(ErrorMessages.PIZZA_NOT_FOUND);
        }
        Long idPizza = pizza.getIdPizza();
        // Vérifie qu'aucune autre pizza n'a déjà ce nom
        Optional<Pizza> otherPizzaWithSameName = pizzaRepository.findByNamePizza(updatedPizzaDTO.getNamePizza())
                .stream()
                .filter(p -> !p.getIdPizza().equals(idPizza))
                .findAny();

        if (otherPizzaWithSameName.isPresent()) {
            System.err.println("Attempt to update a pizza to a name that already exists: " + updatedPizzaDTO.getNamePizza());
            throw new PizzaAndIngredientException(ErrorMessages.PIZZA_ALREADY_EXISTS + " : " + updatedPizzaDTO.getNamePizza());
        }

        if(!updatedPizzaDTO.isAvailable()) {
            List<OrderLine> orderLinesWithInvoicesNotFinalized = orderLineRepository
                    .findByPizzaAndInvoice_FinalizedFalse(this.getPizza(updatedPizzaDTO.getIdPizza()));
            this.orderLineRepository.deleteAll(orderLinesWithInvoicesNotFinalized);
        }
        pizza = PizzaMapper.toEntity(updatedPizzaDTO);

        savePizzaWithIngredientsAndPrice(pizza, updatedPizzaDTO.getIngredients(), updatedPizzaDTO.getPricePizza());
    }

    public Map<String, BigDecimal> getPriceRangeByPizzaAtDate(Object idPizza, LocalDateTime localDateTime) {
        Pizza pizza = getPizza(idPizza);
        Price pricePizza = this.priceRepository.findTopByPizzaAndValidFromLessThanEqualOrderByValidFromDesc(pizza, localDateTime)
                .orElse(null);
        if(pricePizza != null) {
            List<Size> sizes = sizeService.getAllSizes();
            Map<String, BigDecimal> map = new HashMap<>();
            for (Size size : sizes) {
                map.put(size.getNameSize(),
                        pricePizza.getValue().multiply(BigDecimal.valueOf(size.getCoeff()))
                                .setScale(2, RoundingMode.HALF_UP));
            }
            // tri par clé (nom de taille)
            map = map.entrySet().stream()
                    .sorted(Map.Entry.comparingByKey())
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            Map.Entry::getValue,
                            (e1, e2) -> e1,
                            LinkedHashMap::new
                    ));
            return map;
        } else {
            System.err.println("Price not found for pizza " + pizza.getNamePizza() + " at date " + localDateTime.toString());
            throw new PizzaAndIngredientException(
                    ErrorMessages.PRICE_NOT_FOUND + " : " + pizza.getNamePizza() + " à la date " + localDateTime.toString()
            );
        }
    }

    private List<String> getIngredientsFromPizza(Object idPizza){
        Pizza pizza = this.getPizza(idPizza);
        if (pizza != null) {
            List<String> retour = containRepository.findByPizzaIdPizza(pizza.getIdPizza())
                    .stream()
                    .map(contain -> contain.getIngredient().getNameIngredient())
                    .collect(Collectors.toList());
            retour.sort(String::compareTo);
            return retour;
        }
        return null;
    }


    @Transactional
    public void savePizzaWithIngredientsAndPrice(Pizza pizza, List<String> ingredientsNames, BigDecimal price) {
        ingredientsNames.sort(String::compareTo);

        // Supprimer les Contain existants pour éviter les conflits de clé composite
        if (pizza.getIdPizza() != null) {
            containRepository.deleteByPizza(pizza); // DELETE direct en base
            containRepository.flush();              // flush immédiat pour que Hibernate exécute le DELETE
        }

        // Créer les nouvelles Contain
        List<Contain> newContains = new ArrayList<>();
        for (String ingredientName : ingredientsNames) {
            Ingredient ingredient = ingredientService.getIngredient(ingredientName);
            if (ingredient == null) {
                System.err.println("Ingredient not found when saving pizza: " + ingredientName);
                throw new PizzaAndIngredientException(ErrorMessages.INGREDIENT_NOT_FOUND + " : " + ingredientName);
            }
            Contain contain = new Contain();
            contain.setPizza(pizza);
            contain.setIngredient(ingredient);
            newContains.add(contain);
        }

        // Ajouter à la collection existante
        if (pizza.getContains() == null) {
            pizza.setContains(new ArrayList<>());
        }
        pizza.getContains().clear();
        pizza.getContains().addAll(newContains);

        // Sauvegarder la pizza et son prix
        pizzaRepository.save(pizza);
        priceService.savePriceOfPizza(pizza, price);
    }

    public Pizza getBestSellingPizza() {
        List<OrderLine> orderLines = orderLineRepository.findAll();
        return orderLines.stream()
                .collect(Collectors.groupingBy(OrderLine::getPizza, Collectors.summingInt(OrderLine::getQuantity)))
                .entrySet()
                .stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);
    }
}
