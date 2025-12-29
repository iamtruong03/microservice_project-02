package com.example.inventoryservice.service;

import com.example.inventoryservice.dto.InventoryDTO;
import com.example.inventoryservice.model.Inventory;
import com.example.inventoryservice.repository.InventoryRepository;
import com.example.inventoryservice.security.UserContextHolder;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public InventoryDTO createInventory(InventoryDTO inventoryDTO) {
        Long currentUserId = UserContextHolder.getCurrentUserId();
        if (currentUserId == null) {
            throw new RuntimeException("User not authenticated");
        }
        
        Inventory inventory = new Inventory();
        inventory.setUserId(currentUserId);
        inventory.setProductId(inventoryDTO.getProductId());
        inventory.setQuantity(inventoryDTO.getQuantity());
        
        Inventory savedInventory = inventoryRepository.save(inventory);
        
        // Send event to Kafka
        kafkaTemplate.send("inventory-events", "inventory_created", savedInventory);
        
        return convertToDTO(savedInventory);
    }

    public InventoryDTO getInventoryById(Long id) {
        Inventory inventory = inventoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Inventory not found"));
        
        if (!UserContextHolder.hasAccess(inventory.getUserId())) {
            throw new RuntimeException("Access denied");
        }
        
        return convertToDTO(inventory);
    }

    public InventoryDTO getInventoryByProductId(Long productId) {
        return inventoryRepository.findByProductId(productId)
                .filter(inv -> UserContextHolder.hasAccess(inv.getUserId()))
                .map(this::convertToDTO)
                .orElseThrow(() -> new RuntimeException("Inventory not found or access denied"));
    }

    public List<InventoryDTO> getAllInventories() {
        Long currentUserId = UserContextHolder.getCurrentUserId();
        if (currentUserId == null) {
            throw new RuntimeException("User not authenticated");
        }
        
        return inventoryRepository.findByUserId(currentUserId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public InventoryDTO updateInventory(Long id, InventoryDTO inventoryDTO) {
        Inventory inventory = inventoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Inventory not found"));
        
        if (!UserContextHolder.hasAccess(inventory.getUserId())) {
            throw new RuntimeException("Access denied");
        }
        
        inventory.setQuantity(inventoryDTO.getQuantity());
        inventory.setReservedQuantity(inventoryDTO.getReservedQuantity());
        
        Inventory updatedInventory = inventoryRepository.save(inventory);
        
        // Send event to Kafka
        kafkaTemplate.send("inventory-events", "inventory_updated", updatedInventory);
        
        return convertToDTO(updatedInventory);
    }

    public boolean reserveInventory(Long productId, Integer quantity) {
        Inventory inventory = inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new RuntimeException("Inventory not found"));
        
        if (!UserContextHolder.hasAccess(inventory.getUserId())) {
            throw new RuntimeException("Access denied");
        }
        
        int availableQuantity = inventory.getQuantity() - inventory.getReservedQuantity();
        
        if (availableQuantity >= quantity) {
            inventory.setReservedQuantity(inventory.getReservedQuantity() + quantity);
            inventoryRepository.save(inventory);
            kafkaTemplate.send("inventory-events", "inventory_reserved", inventory);
            return true;
        }
        return false;
    }

    public void deleteInventory(Long id) {
        Inventory inventory = inventoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Inventory not found"));
        
        if (!UserContextHolder.hasAccess(inventory.getUserId())) {
            throw new RuntimeException("Access denied");
        }
        
        inventoryRepository.deleteById(id);
    }

    @KafkaListener(topics = "order-events", groupId = "inventory-service-group")
    public void handleOrderEvents(String message) {
        System.out.println("Received order event: " + message);
    }

    private InventoryDTO convertToDTO(Inventory inventory) {
        return new InventoryDTO(
                inventory.getId(),
                inventory.getProductId(),
                inventory.getQuantity(),
                inventory.getReservedQuantity(),
                inventory.getCreatedAt(),
                inventory.getUpdatedAt()
        );
    }
}
