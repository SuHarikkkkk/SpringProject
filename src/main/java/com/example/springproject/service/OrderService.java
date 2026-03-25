package com.example.springproject.service;

import com.example.springproject.entity.Order;
import com.example.springproject.entity.OrderItem;
import com.example.springproject.entity.OrderStatus;
import com.example.springproject.entity.Product;
import com.example.springproject.repository.OrderItemRepository;
import com.example.springproject.repository.OrderRepository;
import com.example.springproject.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;

    public OrderService(OrderRepository orderRepository, OrderItemRepository orderItemRepository, ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.productRepository = productRepository;
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Order getOrderById(Long id) {
        return orderRepository.findById(id).orElse(null);
    }

    public Order saveOrder(Order order) {
        order.setCreatedAt(LocalDateTime.now());
        order.setStatus(OrderStatus.NEW);
        order.setTotalPrice(0.0);
        return orderRepository.save(order);
    }

    public OrderItem addProductToOrder(Long orderId, Long productId, int quantity) {
        Order order = orderRepository.findById(orderId).orElseThrow();
        Product product = productRepository.findById(productId).orElseThrow();

        if (product.getStock() < quantity) {
            throw new RuntimeException("Нет достаточного количества. Доступно: " + product.getStock());
        }

        OrderItem existingItem = order.getItems().stream().filter(item -> item.getId().equals(productId)).findFirst().orElse(null);

        if (existingItem != null) {
            existingItem.setQuantity(existingItem.getQuantity() + quantity);
            existingItem.setPrice(product.getPrice() * quantity);
            orderItemRepository.save(existingItem);
            order.setTotalPrice(order.getTotalPrice() + (product.getPrice() * quantity));
            orderRepository.save(order);
            return existingItem;

        } else {
            OrderItem item = new OrderItem();
            item.setOrder(order);
            item.setProduct(product);
            item.setQuantity(quantity);
            item.setPrice(product.getPrice() * quantity);
            item.setProductName(product.getName());

            orderItemRepository.save(item);

            order.setTotalPrice(order.getTotalPrice() + item.getPrice()); //увеличиваем общую стоимость заказа на цену добавленного товара
            orderRepository.save(order);

            return item;
        }
    }

    @Transactional
    public void removeProductFromOrder(Long orderId, Long orderItemId) {
        Order order = orderRepository.findById(orderId).orElseThrow();
        OrderItem item = orderItemRepository.findById(orderItemId).orElseThrow();

        order.setTotalPrice(order.getTotalPrice() - item.getPrice());
        order.getItems().remove(item);
        orderItemRepository.delete(item);
        orderRepository.save(order);
    }

    @Transactional
    public Order UpdateOrderStatus(Long orderId, OrderStatus orderStatus) {
        Order order = orderRepository.findById(orderId).orElseThrow();
        order.setStatus(orderStatus);
        order.setUpdatedAt(LocalDateTime.now());
        return orderRepository.save(order);
    }

    public void deleteOrder(Long id) {
        Order order = getOrderById(id);
        if (order.getStatus() != OrderStatus.NEW) {
            throw new RuntimeException("Невозможно удалить активный заказ");
        }
        orderRepository.deleteById(id);
    }
}
