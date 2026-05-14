package com.example.springproject.controller;

import com.example.springproject.dto.CartDto;
import com.example.springproject.dto.CartItemCreateDto;
import com.example.springproject.dto.CartItemDto;
import com.example.springproject.dto.CartItemUpdateDto;
import com.example.springproject.entity.User;
import com.example.springproject.service.CartService;
import com.example.springproject.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/carts")
public class CartController {

    private final CartService cartService;
    private final UserService userService;

    public CartController(CartService cartService, UserService userService) {
        this.cartService = cartService;
        this.userService = userService;
    }

    @GetMapping("/{userId}")
    public CartDto getCart(@PathVariable Long userId) {
        User user = userService.getUserById(userId);
        return cartService.getOrCreateCart(user);
    }

    @GetMapping("/{userId}/items")
    public List<CartItemDto> getCartItems(@PathVariable Long userId) {
        User user = userService.getUserById(userId);
        return cartService.getCartItems(user);
    }

    @GetMapping("/{userId}/total")
    public Double getCartTotal(@PathVariable Long userId) {
        User user = userService.getUserById(userId);
        return cartService.getCartTotalPrice(user);
    }

    @PostMapping("/{userId}/items")
    @ResponseStatus(HttpStatus.CREATED)
    public CartItemDto addItemToCart(
            @PathVariable Long userId,
            @RequestBody CartItemCreateDto dto
    ) {
        User user = userService.getUserById(userId);
        return cartService.addItemToCart(user, dto);
    }

    @PutMapping("/items/{cartItemId}")
    public CartItemDto updateCartItemQuantity(
            @PathVariable Long cartItemId,
            @RequestBody CartItemUpdateDto dto
    ) {
        return cartService.updateCartItemQuantity(cartItemId, dto);
    }

    @DeleteMapping("/{userId}/clear")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void clearCart(@PathVariable Long userId) {
        User user = userService.getUserById(userId);
        cartService.clearCart(user);
    }

    @DeleteMapping("/items/{cartItemId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeItemFromCart(@PathVariable Long cartItemId) {
        cartService.removeItemFromCart(cartItemId);
    }
}