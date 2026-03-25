package com.example.springproject.controller;

import com.example.springproject.entity.Order;
import com.example.springproject.entity.OrderItem;
import com.example.springproject.service.OrderService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    List<Order> getAllOrders() {
        return orderService.getAllOrders();
    }

    @GetMapping("/id")
    public Order getOrderById(@RequestParam("id") Long id) {
        return orderService.getOrderById(id);
    }

    @PostMapping
    public Order createOrder(Order order) {
        return orderService.saveOrder(order);
    }

    @PostMapping("/{orderId}/products/{productId}/{quantity}")
    public OrderItem addProductToOrder(@PathVariable Long orderId, @PathVariable Long productId, @PathVariable int quantity) {
        return orderService.addProductToOrder(orderId, productId, quantity);
    }

    @DeleteMapping("/{id}")
    public void deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(id);
    }
}
