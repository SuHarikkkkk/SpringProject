package com.example.springproject.dto;

public record OrderCreateDto(
        String shippingAddress,
        String paymentMethod
) {
}