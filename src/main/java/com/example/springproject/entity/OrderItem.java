package com.example.springproject.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Long id;

    @Getter
    @Setter
    private Integer quantity;

    @Getter
    @Setter
    private Double price;

    @Getter
    @Setter
    @ManyToOne
    private Product product;

    @ManyToOne
    @Getter
    @Setter
    private Order order;

}
