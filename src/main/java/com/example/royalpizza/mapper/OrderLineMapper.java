package com.example.royalpizza.mapper;

import com.example.royalpizza.DTO.AdaptedOrderLine;
import com.example.royalpizza.DTO.OrderLineDTO;
import com.example.royalpizza.entity.OrderLine;

public class OrderLineMapper {

    // le prix n'est pas mappé ici
    public static AdaptedOrderLine toAdapted(OrderLine orderLine) {
        if(orderLine != null) {;
            AdaptedOrderLine adaptedOrderLine = new AdaptedOrderLine();
            adaptedOrderLine.setNameSize(orderLine.getSize().getNameSize());
            adaptedOrderLine.setQuantity(orderLine.getQuantity());
            return adaptedOrderLine;
        }
        else {
            return null;
        }
    }

    // OrderLineDTO without price
    public static OrderLineDTO toDTO(OrderLine orderLine) {
        OrderLineDTO orderLineDTO = new OrderLineDTO();
        orderLineDTO.setNamePizza(orderLine.getPizza().getNamePizza());
        orderLineDTO.setNameSize(orderLine.getSize().getNameSize());
        orderLineDTO.setQuantity(orderLine.getQuantity());
        return orderLineDTO;
    }
}
