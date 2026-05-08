package com.example.springproject.dto;

public record UserUpdateDto(
        String mail,
        String firstName,
        String lastName,
        String companyName
) {
}