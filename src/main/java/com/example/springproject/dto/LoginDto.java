package com.example.springproject.dto;

public record LoginDto(
        String mail,
        String hashedPassword
) {
}