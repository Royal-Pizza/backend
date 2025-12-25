package com.example.royalpizza.mapper;

import com.example.royalpizza.DTO.CustomerDTO;
import com.example.royalpizza.DTO.NewCustomerDTO;
import com.example.royalpizza.entity.Customer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CustomerMapper {

    // On transforme l'entité en DTO
    CustomerDTO toDTO(Customer customer);

    // On transforme le DTO en entité
    @Mapping(source = "admin", target = "isAdmin")
    @Mapping(target = "password", ignore = true) // On ignore le mot de passe pour la sécurité
    @Mapping(target = "invoices", ignore = true)
    // On ignore la liste des factures
    Customer toEntity(CustomerDTO dto);

    // On crée une entité à partir d'un nouveau client (inscription)
    @Mapping(target = "idCustomer", ignore = true)
    @Mapping(target = "wallet", constant = "100.00") // On fixe le portefeuille par défaut
    @Mapping(target = "isAdmin", constant = "false") // On définit le rôle admin à faux par défaut
    @Mapping(target = "available", constant = "true") // On active le compte par défaut
    @Mapping(target = "invoices", ignore = true)
    Customer toEntity(NewCustomerDTO dto);
}