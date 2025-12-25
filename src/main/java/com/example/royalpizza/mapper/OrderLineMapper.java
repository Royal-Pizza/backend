package com.example.royalpizza.mapper;

import com.example.royalpizza.DTO.AdaptedOrderLine;
import com.example.royalpizza.DTO.OrderLineDTO;
import com.example.royalpizza.entity.OrderLine;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderLineMapper {

    // On transforme l'entité en AdaptedOrderLine
    // On va chercher le nom de la taille dans l'objet Size lié
    @Mapping(source = "size.nameSize", target = "nameSize")
    @Mapping(target = "price", ignore = true)
    // On précise que le prix n'est pas mappé ici
    AdaptedOrderLine toAdapted(OrderLine orderLine);

    // On transforme l'entité en OrderLineDTO
    // On récupère le nom de la pizza et le nom de la taille depuis les entités liées
    @Mapping(source = "pizza.namePizza", target = "namePizza")
    @Mapping(source = "size.nameSize", target = "nameSize")
    @Mapping(target = "price", ignore = true)
    // Le DTO n'inclut pas le prix selon la logique actuelle
    OrderLineDTO toDTO(OrderLine orderLine);

    // On peut aussi définir la transformation inverse si nécessaire
    @Mapping(target = "pizza", ignore = true)
    @Mapping(target = "size", ignore = true)
    @Mapping(target = "invoice", ignore = true)
    OrderLine toEntity(OrderLineDTO orderLineDTO);
}