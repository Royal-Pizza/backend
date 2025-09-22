package com.example.royalpizza.service;

import com.example.royalpizza.entity.OrderLine;
import com.example.royalpizza.entity.Size;
import com.example.royalpizza.repository.OrderLineRepository;
import com.example.royalpizza.repository.PizzaRepository;
import com.example.royalpizza.repository.SizeRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SizeService {

    private final SizeRepository sizeRepository;
    private final OrderLineRepository orderLineRepository;

    public SizeService(SizeRepository sizeRepository, OrderLineRepository orderLineRepository) {
        this.sizeRepository = sizeRepository;
        this.orderLineRepository = orderLineRepository;
    }
    public Size getSize(Object object)
    {
        if (object instanceof Long id) {
            Optional<Size> sizeOptional = sizeRepository.findById(id);
            return sizeOptional.orElse(null);
        } else if (object instanceof String name) {
            List<Size> sizes = sizeRepository.findByNameSize(name);
            return sizes.isEmpty() ? null : sizes.get(0);
        }
        return null;
    }

    public void addSize(Size size) {
        sizeRepository.save(size);
    }


    public List<Size> getAllSizes() {
        return sizeRepository.findAll();
    }

    public Size getBestSellingSize() {
        List<OrderLine> orderLines = orderLineRepository.findAll();
        // on recupere la taille la plus vendue, cad, celle qui a le plus d'occurrences dans les lignes de commande
        return orderLines.stream()
                .collect(java.util.stream.Collectors.groupingBy(ol -> ol.getSize(), java.util.stream.Collectors.summingInt(OrderLine::getQuantity)))
                .entrySet()
                .stream()
                .max(java.util.Map.Entry.comparingByValue())
                .map(java.util.Map.Entry::getKey)
                .orElse(null);
    }
}
