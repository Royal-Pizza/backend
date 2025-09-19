package com.example.royalpizza.entity;

import java.io.Serializable;
import lombok.Data;

@Data
public class OrderLineId implements Serializable {
    private Long pizza;
    private Long size;
    private Long invoice;
}
