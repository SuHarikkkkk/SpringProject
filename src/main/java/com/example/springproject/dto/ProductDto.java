package com.example.springproject.dto;

public record ProductDto(
        Long id,
        String name,
        String description,
        String imageUrl,
        Double price,
        int stock,
        Long categoryId,
        String categoryName,
        Long sellerId
) {
}