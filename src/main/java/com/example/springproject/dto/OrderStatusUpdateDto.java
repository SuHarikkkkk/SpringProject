package com.example.springproject.dto;

import com.example.springproject.entity.OrderStatus;

public record OrderStatusUpdateDto(
        OrderStatus status
) {
}