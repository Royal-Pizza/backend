package com.example.royalpizza.DTO;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class InvoiceDTO {
    private Long idInvoice;
    private LocalDateTime date;
    private BigDecimal totalAmount;
    private List<OrderLineDTO> orderLineDTOs;
}
