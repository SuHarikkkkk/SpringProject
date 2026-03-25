package com.example.springproject.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Setter
@Getter
public class Seller extends User {
    private String companyName;

    @OneToMany(mappedBy = "seller")
    private List<Product> products;
}
