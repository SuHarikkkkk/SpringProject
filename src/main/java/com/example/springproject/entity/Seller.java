package com.example.springproject.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Setter
@Getter
@Table(name = "sellers")
public class Seller extends User {
    private String companyName;

    @OneToMany(mappedBy = "seller")
    @JsonIgnore
    private List<Product> products;
}
