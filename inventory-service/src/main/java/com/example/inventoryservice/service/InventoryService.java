package com.example.inventoryservice.service;

import com.example.inventoryservice.dto.InventoryDTO;
import com.example.inventoryservice.model.Inventory;
import com.example.inventoryservice.repository.InventoryRepository;
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
        Inventory inventory = new Inventory();
        inventory.setProductId(inventoryDTO.getProductId());
        inventory.setQuantity(inventoryDTO.getQuantity());
        
        Inventory savedInventory = inventoryRepository.save(inventory);
        
        // Send event to Kafka
        kafkaTemplate.send("inventory-events", "inventory_created", savedInventory);
        
        return convertToDTO(savedInventory);
    }

    public InventoryDTO getInventoryById(Long id) {
        return inventoryRepository.findById(id)
                .map(this::convertToDTO)
                .orElseThrow(() -> new RuntimeException("Inventory not found"));
    }

    public InventoryDTO getInventoryByProductId(Long productId) {
        return inventoryRepository.findByProductId(productId)
                .map(this::convertToDTO)
                .orElseThrow(() -> new RuntimeException("Inventory not found for product"));
    }

    public List<InventoryDTO> getAllInventories() {
        return inventoryRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public InventoryDTO updateInventory(Long id, InventoryDTO inventoryDTO) {
        Inventory inventory = inventoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Inventory not found"));
        
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
