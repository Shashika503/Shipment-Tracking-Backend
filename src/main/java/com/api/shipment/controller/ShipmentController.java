package com.api.shipment.controller;

import com.api.shipment.model.Shipment;
import com.api.shipment.service.ShipmentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/shipments")
@CrossOrigin(origins = "*") // Allows all origins to access the API
public class ShipmentController {

    @Autowired
    private ShipmentService shipmentService;

    // Add Shipment
    @PostMapping("/{customerId}")
    public ResponseEntity<?> addShipment(
            @PathVariable Long customerId,
            @Valid @RequestBody Shipment shipment) {
        try {
            Shipment savedShipment = shipmentService.addShipment(customerId, shipment);
            return ResponseEntity.ok(savedShipment);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", e.getMessage()));
        }
    }

    // Update Shipment
    @PutMapping("/{shipmentId}")
    public ResponseEntity<?> updateShipment(
            @PathVariable Long shipmentId,
            @Valid @RequestBody Shipment shipmentDetails) {
        try {
            Shipment updatedShipment = shipmentService.updateShipment(shipmentId, shipmentDetails);
            return ResponseEntity.ok(updatedShipment);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", e.getMessage()));
        }
    }

    // Delete Shipment
    @DeleteMapping("/{shipmentId}")
    public ResponseEntity<?> deleteShipment(@PathVariable Long shipmentId) {
        try {
            shipmentService.deleteShipment(shipmentId);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", e.getMessage()));
        }
    }

    // Get Shipments by Customer ID
    @GetMapping("/{customerId}")
    public ResponseEntity<?> getShipmentsByCustomer(@PathVariable Long customerId) {
        try {
            List<Shipment> shipments = shipmentService.getShipmentsByCustomer(customerId);
            return ResponseEntity.ok(shipments);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", e.getMessage()));
        }
    }

    // Track Shipment by Tracking ID
    @GetMapping("/track/{trackingId}")
    public ResponseEntity<?> trackShipment(
            @PathVariable String trackingId,
            @RequestParam Long customerId) {
        try {
            Shipment shipment = shipmentService.trackShipment(trackingId);

            // Validate that the logged-in customer's ID matches the shipment's customer
            if (!shipment.getCustomer().getId().equals(customerId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("message", "Access denied: Shipment does not belong to the customer."));
            }

            return ResponseEntity.ok(shipment);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Shipment with the given tracking ID not found."));
        }
    }

    // Reschedule Shipment Delivery
    @PutMapping("/{trackingId}/reschedule")
    public ResponseEntity<?> rescheduleShipment(
            @PathVariable String trackingId,
            @RequestBody Map<String, String> rescheduleData) {
        try {
            String newDate = rescheduleData.get("newDate");
            String instructions = rescheduleData.get("instructions");
            String customerId = rescheduleData.get("customerId");

            Shipment updatedShipment = shipmentService.rescheduleShipment(trackingId, newDate, instructions , customerId);
            return ResponseEntity.ok(updatedShipment);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", e.getMessage()));
        }
    }
}
