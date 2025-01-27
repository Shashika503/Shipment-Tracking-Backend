package com.api.shipment.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.api.shipment.model.Customer;
import com.api.shipment.service.CustomerService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/customers")
@CrossOrigin(origins = "*")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @PostMapping("/register")
    public ResponseEntity<?> registerCustomer(@Valid @RequestBody Customer customer) {
        try {
            Customer registeredCustomer = customerService.registerCustomer(customer);
            return ResponseEntity.status(HttpStatus.CREATED).body(registeredCustomer); // 201 Created
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage()); // 400 Bad Request
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody Customer customer) {
        Customer loggedInCustomer = customerService.login(customer.getEmail(), customer.getPassword());
        if (loggedInCustomer != null) {
            return ResponseEntity.status(200).body(Map.of(
                "status", 200,
                "message", "Login successful",
                "customerId", loggedInCustomer.getId()
            ));
        }
        return ResponseEntity.status(401).body(Map.of(
            "status", 401,
            "message", "Invalid credentials"
        ));
    }
}

