package com.example.accountservice.controller;

import com.example.accountservice.model.AccountType;
import com.example.accountservice.service.AccountTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/account-types")
public class AccountTypeController {
    @Autowired
    private AccountTypeService accountTypeService;

    @GetMapping
    public ResponseEntity<?> getAll(
        @RequestHeader(name = "uid", defaultValue = "") String uid) {
        try {
            List<AccountType> list = accountTypeService.getAll();
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

            return accountTypeService.getById(id)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }


    @PostMapping
    public ResponseEntity<?> create(
        @RequestHeader(name = "uid", defaultValue = "") String uid,
        @RequestBody AccountType accountType) {
        try {

            AccountType created = accountTypeService.create(uid, accountType);
            return ResponseEntity.status(201).body(created);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }


    @PutMapping("/update")
    public ResponseEntity<?> update(
        @RequestHeader(name = "uid", defaultValue = "") String uid,
        @RequestBody AccountType accountType) {
        try {
            AccountType updated = accountTypeService.update(uid, accountType);
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
            if (!accountTypeService.getById(id).isPresent()) {
                return ResponseEntity.notFound().build();
            }
            accountTypeService.delete(id);
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
            AccountType softDeleted = accountTypeService.softDelete(uid, id);
            if (softDeleted == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(softDeleted);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }
}