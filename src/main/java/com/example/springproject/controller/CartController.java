package com.example.springproject.controller;

import com.example.springproject.entity.Cart;
import com.example.springproject.entity.CartItem;
import com.example.springproject.entity.Customer;
import com.example.springproject.service.CartService;
import com.example.springproject.service.CustomerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/carts")
public class CartController {

    private final CartService cartService;
    private final CustomerService customerService;

    public CartController(CartService cartService, CustomerService customerService) {
        this.cartService = cartService;
        this.customerService = customerService;
    }

    @GetMapping("/items")
    public ResponseEntity<List<CartItem>> getCartItems(@RequestParam Long id) {
        Customer customer = customerService.getCustomerById(id);
        List<CartItem> cartItems = cartService.getCartItems(customer);
        return ResponseEntity.ok(cartItems);
    }

    @GetMapping("/total")
    public ResponseEntity<Double> getCartTotal(@RequestParam Long id) {
        Customer customer = customerService.getCustomerById(id);
        Double total = cartService.getCartTotalPrice(customer);
        return ResponseEntity.ok(total);
    }

    @GetMapping
    public ResponseEntity<Cart> getOrCreateCart(@RequestParam Long id) {
        Customer customer = customerService.getCustomerById(id);
        Cart cart = cartService.getOrCreateCart(customer);
        return ResponseEntity.ok().body(cart);
    }

    @PostMapping("/add")
    public ResponseEntity<CartItem> addItemToCart(@RequestParam Long customerId, @RequestParam Long productId, @RequestParam int quantity) {
        try {
            Customer customer = customerService.getCustomerById(customerId);
            CartItem cartItem = cartService.addItemToCart(customer, productId, quantity);
            return ResponseEntity.status(HttpStatus.CREATED).body(cartItem);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/item")
    public ResponseEntity<CartItem> updateCartItemQuantity(@RequestParam Long id, @RequestParam int quantity) {
        try {
            CartItem cartItem = cartService.updateCartItemQuantity(id, quantity);
            if (cartItem == null) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(cartItem);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/clear")
    public ResponseEntity<Void> clearCart(@RequestParam Long id) {
        Customer customer = customerService.getCustomerById(id);
        cartService.clearCart(customer);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/item")
    public ResponseEntity<Void> removeItemFromCart(@RequestParam Long id) {
        try {
            cartService.removeItemFromCart(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

}
