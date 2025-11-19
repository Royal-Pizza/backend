package com.example.royalpizza.mapper;

import com.example.royalpizza.DTO.OrderLineDTO;
import com.example.royalpizza.entity.OrderLine;

public class OrderLineMapper {

    // le prix n'est pas mappé ici
    public static OrderLineDTO toDTO(OrderLine orderLine) {
        if(orderLine != null) {;
            OrderLineDTO orderLineDTO = new OrderLineDTO();
            orderLineDTO.setSizeName(orderLine.getSize().getNameSize());
            orderLineDTO.setQuantity(orderLine.getQuantity());
            return orderLineDTO;
        }
        else {
            return null;
        }
    }
}
