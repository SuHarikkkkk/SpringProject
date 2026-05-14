package com.example.springproject.dto;

public record CartItemDto(
        Long id,
        Long productId,
        String productName,
        String productImageUrl,
        Long categoryId,
        String categoryName,
        int quantity,
        Double price
) {
}