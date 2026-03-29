package com.example.springproject.service;

import com.example.springproject.entity.Cart;
import com.example.springproject.entity.CartItem;
import com.example.springproject.entity.Customer;
import com.example.springproject.entity.Product;
import com.example.springproject.repository.CartItemRepository;
import com.example.springproject.repository.CartRepository;
import com.example.springproject.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class CartService {
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final CartItemRepository cartItemRepository;

    public CartService (CartRepository cartRepository, ProductRepository productRepository, CartItemRepository cartItemRepository) {
        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
        this.cartItemRepository = cartItemRepository;
    }

    public Cart getOrCreateCart(Customer customer) {
        List<Cart> allCarts = cartRepository.findAll();
        Cart cart = allCarts.stream().filter(c -> c.getCustomer() != null && c.getCustomer().getId().equals(customer.getId())).findFirst().orElse(null);
        if (cart == null) {
            cart = new Cart();
            cart.setCustomer(customer);
            cartRepository.save(cart);
        }
        return cart;
    }

    @Transactional
    public CartItem addItemToCart(Customer customer, Long productId, int quantity) {
        Cart cart = getOrCreateCart(customer);
        Product product = productRepository.findById(productId).orElseThrow(() -> new RuntimeException("Товар не найден"));

        if (product.getStock() < quantity) {
            throw new RuntimeException("Товара недостаточно на складе");
        }

        CartItem existingItem = cart.getCartItems().stream().filter(ci -> ci.getProduct().getId().equals(productId)).findFirst().orElse(null);
        if (existingItem != null) {
            existingItem.setQuantity(existingItem.getQuantity() + quantity);
            existingItem.setPrice(product.getPrice());
            cartItemRepository.save(existingItem);
        } else {
            CartItem newItem = new CartItem();
            newItem.setProduct(product);
            newItem.setQuantity(quantity);
            newItem.setCart(cart);
            newItem.setPrice(product.getPrice());
            cartItemRepository.save(newItem);
            if (cart.getCartItems() == null) {
                cart.setCartItems(new ArrayList<>());
            }
            cart.getCartItems().add(newItem);
        }
        cartRepository.save(cart);
        return existingItem != null ? existingItem : cart.getCartItems().get(cart.getCartItems().size() - 1);
    }

    @Transactional
    public void removeItemFromCart(Long cartItemId) {
        cartItemRepository.deleteById(cartItemId);
    }

    @Transactional
    public CartItem updateCartItemQuantity(Long CartItemId, int quantity) {
        CartItem item = cartItemRepository.findById(CartItemId).orElseThrow(() -> new RuntimeException("Товар в корзине не найден"));
        if (quantity <= 0) {
            cartItemRepository.delete(item);
            return null;
        }
        Product product = item.getProduct();
        if (product.getStock() < quantity) {
            throw new RuntimeException("Not enough stock. Available: " + product.getStock());
        }
        item.setQuantity(quantity);
        return cartItemRepository.save(item);
    }

    @Transactional
    public void clearCart(Customer customer) {
        Cart cart = getOrCreateCart(customer);
        cartItemRepository.deleteAll(cart.getCartItems());
        cart.getCartItems().clear();
        cartRepository.save(cart);
    }

    public Double getCartTotalPrice(Customer customer) {
        Cart cart = getOrCreateCart(customer);
        return cart.getCartItems().stream().mapToDouble(item -> item.getPrice() * item.getQuantity()).sum();
    }

    public List<CartItem> getCartItems(Customer customer) {
        Cart cart = getOrCreateCart(customer);
        return cart.getCartItems();
    }

}

