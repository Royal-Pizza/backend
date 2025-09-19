package com.example.royalpizza.entity;

import java.io.Serializable;
import lombok.Data;

@Data
public class ContainId implements Serializable {
    private Long pizza;
    private Long ingredient;
}
