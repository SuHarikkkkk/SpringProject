package com.example.springproject.dto;

public record OrderItemDto(
        Long id,
        Long productId,
        String productName,
        int quantity,
        Double price
) {
}