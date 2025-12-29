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
            Long uidLong = Long.parseLong(uid);
            return ResponseUtils.handlerSuccess(orderService.getOrderById(id, uidLong));
        } catch (Exception e) {
            return ResponseUtils.handlerException(e);
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllOrders(
            @RequestHeader(name = "uid", defaultValue = "") String uid) {
        try {
            Long uidLong = Long.parseLong(uid);
            return ResponseUtils.handlerSuccess(orderService.getAllOrders(uidLong));
        } catch (Exception e) {
            return ResponseUtils.handlerException(e);
        }
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<?> getOrdersByCustomer(
            @RequestHeader(name = "uid", defaultValue = "") String uid,
            @PathVariable Long customerId) {
        try {
            Long uidLong = Long.parseLong(uid);
            return ResponseUtils.handlerSuccess(orderService.getOrdersByCustomerId(customerId, uidLong));
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
            Long uidLong = Long.parseLong(uid);
            orderDTO.setCustomerId(uidLong);
            return ResponseUtils.handlerSuccess(orderService.updateOrder(id, orderDTO, uidLong));
        } catch (Exception e) {
            return ResponseUtils.handlerException(e);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteOrder(
            @RequestHeader(name = "uid", defaultValue = "") String uid,
            @PathVariable Long id) {
        try {
            Long uidLong = Long.parseLong(uid);
            orderService.deleteOrder(id, uidLong);
            return ResponseUtils.handlerNoContent();
        } catch (Exception e) {
            return ResponseUtils.handlerException(e);
        }
    }
}
