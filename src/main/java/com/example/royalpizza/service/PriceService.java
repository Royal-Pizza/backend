package com.example.royalpizza.service;

import com.example.royalpizza.entity.Pizza;
import com.example.royalpizza.entity.Price;
import com.example.royalpizza.repository.PriceRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Slf4j
@Service
public class PriceService {
    private final PriceRepository priceRepository;

    public PriceService(PriceRepository priceRepository) {
        this.priceRepository = priceRepository;
    }

    public Price getStaticPriceByPizza(Pizza pizza) {
        return priceRepository
                .findTopByPizzaAndValidToIsNullOrderByValidFromDesc(pizza)
                .orElse(null);
    }

    public void savePriceOfPizza(Pizza pizza, BigDecimal newPrice) {
        LocalDateTime now = LocalDateTime.now();
        Price price = this.getStaticPriceByPizza(pizza);
        if (price != null) {
            price.setValidTo(now);
            priceRepository.save(price);
        }
        Price newPriceEntry = new Price();
        newPriceEntry.setPizza(pizza);
        newPriceEntry.setValue(newPrice);
        newPriceEntry.setValidFrom(now);
        newPriceEntry.setValidTo(null);
        priceRepository.save(newPriceEntry);
    }


}
