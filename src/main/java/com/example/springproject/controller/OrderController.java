package com.example.springproject.controller;

import com.example.springproject.dto.OrderCreateDto;
import com.example.springproject.dto.OrderDto;
import com.example.springproject.dto.OrderStatusUpdateDto;
import com.example.springproject.entity.User;
import com.example.springproject.service.OrderService;
import com.example.springproject.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;
    private final UserService userService;

    public OrderController(OrderService orderService, UserService userService) {
        this.orderService = orderService;
        this.userService = userService;
    }

    @GetMapping("/{orderId}")
    public OrderDto getOrderById(@PathVariable Long orderId) {
        return orderService.getOrderById(orderId);
    }

    @GetMapping("/user/{userId}")
    public Page<OrderDto> getOrdersByUser(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        User user = userService.getUserById(userId);
        return orderService.getOrdersByCustomer(user, page, size);
    }

    @PostMapping("/{userId}/create")
    @ResponseStatus(HttpStatus.CREATED)
    public OrderDto createOrderFromCart(
            @PathVariable Long userId,
            @RequestBody OrderCreateDto dto
    ) {
        User user = userService.getUserById(userId);
        return orderService.createOrderFromCart(user, dto);
    }

    @GetMapping
    public Page<OrderDto> getAllOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return orderService.getAllOrders(page, size);
    }

    @PutMapping("/{orderId}/status")
    public OrderDto updateOrderStatus(
            @PathVariable Long orderId,
            @RequestBody OrderStatusUpdateDto dto
    ) {
        return orderService.updateOrderStatus(orderId, dto);
    }

    @PostMapping("/{orderId}/pay")
    public OrderDto payOrder(@PathVariable Long orderId) {
        return orderService.payOrder(orderId);
    }

    @PostMapping("/{orderId}/cancel")
    public OrderDto cancelOrder(@PathVariable Long orderId) {
        return orderService.cancelOrder(orderId);
    }

    @DeleteMapping("/{orderId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteOrder(@PathVariable Long orderId) {
        orderService.deleteOrder(orderId);
    }
}