package com.example.orderservice.controller;

import com.example.orderservice.dto.OrderDTO;
import com.example.orderservice.service.OrderService;
import com.example.orderservice.util.ResponseUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<?> createOrder(
            @RequestHeader(name = "uid", defaultValue = "") String uid,
            @RequestBody OrderDTO orderDTO) {
        try {
            orderDTO.setCustomerId(Long.parseLong(uid));
            return ResponseUtils.handlerCreated(orderService.createOrder(orderDTO));
        } catch (Exception e) {
            return ResponseUtils.handlerException(e);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getOrder(
            @RequestHeader(name = "uid", defaultValue = "") String uid,
            @PathVariable Long id) {
        try {
            return ResponseUtils.handlerSuccess(orderService.getOrderById(id));
        } catch (Exception e) {
            return ResponseUtils.handlerException(e);
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllOrders(
            @RequestHeader(name = "uid", defaultValue = "") String uid) {
        try {
            return ResponseUtils.handlerSuccess(orderService.getAllOrders());
        } catch (Exception e) {
            return ResponseUtils.handlerException(e);
        }
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<?> getOrdersByCustomer(
            @RequestHeader(name = "uid", defaultValue = "") String uid,
            @PathVariable Long customerId) {
        try {
            return ResponseUtils.handlerSuccess(orderService.getOrdersByCustomerId(customerId));
        } catch (Exception e) {
            return ResponseUtils.handlerException(e);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateOrder(
            @RequestHeader(name = "uid", defaultValue = "") String uid,
            @PathVariable Long id,
            @RequestBody OrderDTO orderDTO) {
        try {
            orderDTO.setCustomerId(Long.parseLong(uid));
            return ResponseUtils.handlerSuccess(orderService.updateOrder(id, orderDTO));
        } catch (Exception e) {
            return ResponseUtils.handlerException(e);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteOrder(
            @RequestHeader(name = "uid", defaultValue = "") String uid,
            @PathVariable Long id) {
        try {
            orderService.deleteOrder(id);
            return ResponseUtils.handlerNoContent();
        } catch (Exception e) {
            return ResponseUtils.handlerException(e);
        }
    }
}
