package com.example.springproject.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Long id;

    @Getter
    @Setter
    private LocalDateTime createdAt;

    @Getter
    @Setter
    private Double totalPrice;

    @OneToMany(mappedBy = "order")
    private List<OrderItem> items;
}
