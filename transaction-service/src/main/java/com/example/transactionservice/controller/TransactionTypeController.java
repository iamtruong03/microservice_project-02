package com.example.transactionservice.controller;

import com.example.transactionservice.model.TransactionType;
import com.example.transactionservice.service.TransactionTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transaction-types")
public class TransactionTypeController {

  @Autowired
  private TransactionTypeService transactionTypeService;

  @GetMapping
  public ResponseEntity<?> getAll(
      @RequestHeader(name = "uid", defaultValue = "") String uid) {
    try {
      List<TransactionType> list = transactionTypeService.getAll();
      return ResponseEntity.ok(list);
    } catch (Exception e) {
      return ResponseEntity.internalServerError().body(e.getMessage());
    }
  }

  @GetMapping("/{id}")
  public ResponseEntity<?> getById(
      @RequestHeader(name = "uid", defaultValue = "") String uid,
      @PathVariable Long id) {
    try {
      return transactionTypeService.getById(id)
          .map(ResponseEntity::ok)
          .orElse(ResponseEntity.notFound().build());
    } catch (Exception e) {
      return ResponseEntity.internalServerError().body(e.getMessage());
    }
  }

  @PostMapping
  public ResponseEntity<?> create(
      @RequestHeader(name = "uid", defaultValue = "") String uid,
      @RequestBody TransactionType transactionType) {
    try {
      TransactionType created = transactionTypeService.create(uid, transactionType);
      return ResponseEntity.status(201).body(created);
    } catch (Exception e) {
      return ResponseEntity.internalServerError().body(e.getMessage());
    }
  }

  @PutMapping("/update")
  public ResponseEntity<?> update(
      @RequestHeader(name = "uid", defaultValue = "") String uid,
      @RequestBody TransactionType transactionType) {
    try {
      TransactionType updated = transactionTypeService.update(uid, transactionType);
      return ResponseEntity.ok(updated);
    } catch (Exception e) {
      return ResponseEntity.internalServerError().body(e.getMessage());
    }
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<?> delete(
      @RequestHeader(name = "uid", defaultValue = "") String uid,
      @PathVariable Long id) {
    try {
      if (!transactionTypeService.getById(id).isPresent()) {
        return ResponseEntity.notFound().build();
      }
      transactionTypeService.delete(id);
      return ResponseEntity.noContent().build();
    } catch (Exception e) {
      return ResponseEntity.internalServerError().body(e.getMessage());
    }
  }

  @PutMapping("/{id}/soft-delete")
  public ResponseEntity<?> softDelete(
      @RequestHeader(name = "uid", defaultValue = "") String uid,
      @PathVariable Long id) {
    try {
      TransactionType softDeleted = transactionTypeService.softDelete(uid, id);
      if (softDeleted == null) {
        return ResponseEntity.notFound().build();
      }
      return ResponseEntity.ok(softDeleted);
    } catch (Exception e) {
      return ResponseEntity.internalServerError().body(e.getMessage());
    }
  }
}