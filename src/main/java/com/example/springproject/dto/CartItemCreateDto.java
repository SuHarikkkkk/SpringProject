package com.example.springproject.dto;

public record CartItemCreateDto(
        Long productId,
        int quantity
) {
}