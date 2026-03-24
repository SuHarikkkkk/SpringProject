package com.example.springproject.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Long id;

    private Integer quantity;

    private Double price;

    @ManyToOne
    private Product product;

    @ManyToOne
    private Order order;

}
