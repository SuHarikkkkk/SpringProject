package com.example.springproject.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "orders", schema = "public")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Long id;

    private LocalDateTime createdAt;

    private Double totalPrice;

    @OneToMany(mappedBy = "order")
    private List<OrderItem> items;
}
