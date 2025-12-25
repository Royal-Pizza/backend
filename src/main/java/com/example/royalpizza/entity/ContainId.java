package com.example.royalpizza.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class ContainId implements Serializable {
    private Long pizza;
    private Long ingredient;
}
