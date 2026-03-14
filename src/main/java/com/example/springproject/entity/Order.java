package com.example.springproject.entity;


import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Long id;

    private LocalDateTime createdAt;

    private Double totalPrice;

    @OneToMany(mappedBy = "order")
    private List<OrderItem> items;
}
