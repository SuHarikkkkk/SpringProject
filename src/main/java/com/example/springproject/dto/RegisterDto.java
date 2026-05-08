package com.example.springproject.dto;

import com.example.springproject.entity.Role;

public record RegisterDto(
        String mail,
        String hashedPassword,
        String firstName,
        String lastName,
        Role role,
        String companyName
) {
}