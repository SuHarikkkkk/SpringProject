package com.example.springproject.dto;

import com.example.springproject.entity.OrderStatus;

import java.time.LocalDateTime;
import java.util.List;

public record OrderDto(
        Long id,
        Long customerId,
        String customerMail,
        String shippingAddress,
        String paymentMethod,
        OrderStatus status,
        Double totalPrice,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        List<OrderItemDto> items
) {
}