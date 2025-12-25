package com.example.royalpizza.mapper;

import com.example.royalpizza.DTO.NewPizzaDTO;
import com.example.royalpizza.DTO.PizzaDTO;
import com.example.royalpizza.DTO.UpdatedPizzaDTO;
import com.example.royalpizza.entity.Pizza;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Base64;

@Mapper(componentModel = "spring")
public interface PizzaMapper {

    // On transforme l'entité en PizzaDTO
    PizzaDTO toDTO(Pizza pizza);

    // On transforme un NewPizzaDTO en entité
    @Mapping(target = "idPizza", ignore = true)
    // On ignore "contains" car la liste des ingrédients (entité) ne correspond pas à List<String> (DTO)
    @Mapping(target = "contains", ignore = true)
    @Mapping(target = "available", constant = "true")
    @Mapping(source = "image", target = "image", qualifiedByName = "base64ToBytes")
    Pizza toEntity(NewPizzaDTO newPizzaDTO);

    // On gère la mise à jour (UpdatedPizzaDTO)
    @Mapping(target = "contains", ignore = true)
    @Mapping(source = "image", target = "image", qualifiedByName = "base64ToBytes")
    Pizza toEntity(UpdatedPizzaDTO updatedPizzaDTO);

    // On définit la logique personnalisée pour le décodage de l'image
    @Named("base64ToBytes")
    default byte[] base64ToBytes(String base64) {
        if (base64 == null || base64.isEmpty()) {
            return null;
        }

        // Si l'image contient un header (data:image/...;base64,), on le nettoie
        String cleanBase64 = base64;
        if (cleanBase64.contains(",")) {
            cleanBase64 = cleanBase64.substring(cleanBase64.indexOf(",") + 1);
        }

        try {
            return Base64.getDecoder().decode(cleanBase64);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}