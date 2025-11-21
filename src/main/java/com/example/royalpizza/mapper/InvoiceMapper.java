package com.example.royalpizza.mapper;

import com.example.royalpizza.DTO.InvoiceDTO;
import com.example.royalpizza.entity.Invoice;


public class InvoiceMapper {

    public static InvoiceDTO toDTO(Invoice invoice) {
        InvoiceDTO invoiceDTO = new InvoiceDTO();
        invoiceDTO.setIdInvoice(invoice.getIdInvoice());
        invoiceDTO.setDate(invoice.getDate());
        invoiceDTO.setTotalAmount(invoice.getTotalAmount());
        invoiceDTO.setOrderLineDTOs(
                invoice.getOrderLines()
                .stream()
                .map(OrderLineMapper::toDTO)
                .toList()
        );
        return invoiceDTO;
    }

}
