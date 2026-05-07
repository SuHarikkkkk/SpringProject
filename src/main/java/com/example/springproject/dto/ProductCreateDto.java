package com.example.springproject.dto;

public record ProductCreateDto(
        String name,
        String description,
        String imageUrl,
        Double price,
        int stock,
        Long categoryId,
        Long sellerId
) {
}