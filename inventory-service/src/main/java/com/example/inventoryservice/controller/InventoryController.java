package com.example.inventoryservice.controller;

import com.example.inventoryservice.dto.InventoryDTO;
import com.example.inventoryservice.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    @PostMapping
    public ResponseEntity<InventoryDTO> createInventory(
            @RequestHeader(name = "uid", required = true) Long uid,
            @RequestBody InventoryDTO inventoryDTO) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(inventoryService.createInventory(uid, inventoryDTO));
    }

    @GetMapping("/{id}")
    public ResponseEntity<InventoryDTO> getInventory(
            @RequestHeader(name = "uid", required = true) Long uid,
            @PathVariable Long id) {
        return ResponseEntity.ok(inventoryService.getInventoryById(uid, id));
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<InventoryDTO> getInventoryByProduct(
            @RequestHeader(name = "uid", required = true) Long uid,
            @PathVariable Long productId) {
        return ResponseEntity.ok(inventoryService.getInventoryByProductId(uid, productId));
    }

    @GetMapping
    public ResponseEntity<List<InventoryDTO>> getAllInventories(
            @RequestHeader(name = "uid", required = true) Long uid) {
        return ResponseEntity.ok(inventoryService.getAllInventories(uid));
    }

    @PutMapping("/{id}")
    public ResponseEntity<InventoryDTO> updateInventory(
            @RequestHeader(name = "uid", required = true) Long uid,
            @PathVariable Long id,
            @RequestBody InventoryDTO inventoryDTO) {
        return ResponseEntity.ok(inventoryService.updateInventory(uid, id, inventoryDTO));
    }

    @PostMapping("/reserve")
    public ResponseEntity<Boolean> reserveInventory(
            @RequestHeader(name = "uid", required = true) Long uid,
            @RequestParam Long productId,
            @RequestParam Integer quantity) {
        boolean reserved = inventoryService.reserveInventory(uid, productId, quantity);
        return ResponseEntity.ok(reserved);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInventory(
            @RequestHeader(name = "uid", required = true) Long uid,
            @PathVariable Long id) {
        inventoryService.deleteInventory(uid, id);
        return ResponseEntity.noContent().build();
    }
}
