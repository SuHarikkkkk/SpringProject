package com.example.springproject.controller;

import com.example.springproject.entity.Cart;
import com.example.springproject.entity.CartItem;
import com.example.springproject.entity.User;
import com.example.springproject.service.CartService;
import com.example.springproject.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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

    @PreAuthorize("hasRole('CUSTOMER')")
    @GetMapping("/{userId}")
    public ResponseEntity<Cart> getCart(@PathVariable Long userId) {
        try {
            User user = userService.getUserById(userId);
            Cart cart = cartService.getOrCreateCart(user);
            return ResponseEntity.ok(cart);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @GetMapping("/{userId}/items")
    public ResponseEntity<List<CartItem>> getCartItems(@PathVariable Long userId) {
        try {
            User user = userService.getUserById(userId);
            List<CartItem> cartItems = cartService.getCartItems(user);
            return ResponseEntity.ok(cartItems);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @GetMapping("/{userId}/total")
    public ResponseEntity<Double> getCartTotal(@PathVariable Long userId) {
        try {
            User user = userService.getUserById(userId);
            Double total = cartService.getCartTotalPrice(user);
            return ResponseEntity.ok(total);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @PostMapping("/{userId}/items")
    public ResponseEntity<CartItem> addItemToCart(@PathVariable Long userId, @RequestParam Long productId, @RequestParam int quantity) {
        try {
            User user = userService.getUserById(userId);
            CartItem cartItem = cartService.addItemToCart(user, productId, quantity);
            return ResponseEntity.status(HttpStatus.CREATED).body(cartItem);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @PutMapping("/items/{cartItemId}")
    public ResponseEntity<CartItem> updateCartItemQuantity(@PathVariable Long cartItemId, @RequestParam int quantity) {
        try {
            CartItem cartItem = cartService.updateCartItemQuantity(cartItemId, quantity);
            if (cartItem == null) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(cartItem);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @DeleteMapping("/{userId}/clear")
    public ResponseEntity<Void> clearCart(@PathVariable Long userId) {
        try {
            User user = userService.getUserById(userId);
            cartService.clearCart(user);
            return ResponseEntity.noContent().build();
        }
        catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @DeleteMapping("/items/{cartItemId}")
    public ResponseEntity<Void> removeItemFromCart(@PathVariable Long cartItemId) {
        try {
            cartService.removeItemFromCart(cartItemId);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
