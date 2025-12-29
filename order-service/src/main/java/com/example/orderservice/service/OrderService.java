package com.example.orderservice.service;

import com.example.orderservice.dto.OrderDTO;
import com.example.orderservice.model.Order;
import com.example.orderservice.repository.OrderRepository;
import com.example.orderservice.security.UserContextHolder;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public OrderDTO createOrder(OrderDTO orderDTO) {
        Order order = new Order();
        order.setCustomerId(orderDTO.getCustomerId());
        order.setTotalAmount(orderDTO.getTotalAmount());
        
        Order savedOrder = orderRepository.save(order);
        
        // Send event to Kafka
        kafkaTemplate.send("order-events", "order_created", savedOrder);
        
        return convertToDTO(savedOrder);
    }

    public OrderDTO getOrderById(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        
        // Verify user has access to this order
        if (!UserContextHolder.hasAccess(order.getCustomerId())) {
            throw new RuntimeException("Access denied");
        }
        
        return convertToDTO(order);
    }

    public List<OrderDTO> getAllOrders() {
        // Get only orders for current user
        Long currentUserId = UserContextHolder.getCurrentUserId();
        if (currentUserId == null) {
            throw new RuntimeException("User not authenticated");
        }
        
        return orderRepository.findByCustomerId(currentUserId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<OrderDTO> getOrdersByCustomerId(Long customerId) {
        // Only allow users to view their own orders
        if (!UserContextHolder.hasAccess(customerId)) {
            throw new RuntimeException("Access denied");
        }
        
        return orderRepository.findByCustomerId(customerId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public OrderDTO updateOrder(Long orderId, OrderDTO orderDTO) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        
        // Verify user has access to update this order
        if (!UserContextHolder.hasAccess(order.getCustomerId())) {
            throw new RuntimeException("Access denied");
        }
        
        order.setStatus(orderDTO.getStatus());
        Order updatedOrder = orderRepository.save(order);
        
        // Send event to Kafka
        kafkaTemplate.send("order-events", "order_updated", updatedOrder);
        
        return convertToDTO(updatedOrder);
    }

    public void deleteOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        
        // Verify user has access to delete this order
        if (!UserContextHolder.hasAccess(order.getCustomerId())) {
            throw new RuntimeException("Access denied");
        }
        
        orderRepository.deleteById(orderId);
    }

    private OrderDTO convertToDTO(Order order) {
        return new OrderDTO(
                order.getId(),
                order.getCustomerId(),
                order.getTotalAmount(),
                order.getStatus(),
                order.getCreatedAt(),
                order.getUpdatedAt()
        );
    }
}
