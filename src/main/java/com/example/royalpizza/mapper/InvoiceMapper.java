package com.example.royalpizza.mapper;

import com.example.royalpizza.DTO.InvoiceDTO;
import com.example.royalpizza.entity.Invoice;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring", uses = {OrderLineMapper.class})
public interface InvoiceMapper {

    @Mapping(source = "orderLines", target = "orderLineDTOs")
    InvoiceDTO toDTO(Invoice invoice);

    @Mapping(source = "orderLineDTOs", target = "orderLines")
    @Mapping(target = "customer", ignore = true)
    @Mapping(target = "finalized", ignore = true)
    Invoice toEntity(InvoiceDTO invoiceDTO);
}