package com.example.royalpizza.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class OrderLineId implements Serializable {
    private Long pizza;
    private Long size;
    private Long invoice;
}