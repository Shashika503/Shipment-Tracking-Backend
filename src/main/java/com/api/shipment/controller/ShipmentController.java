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

    @PostMapping("/{customerId}")
    public ResponseEntity<Shipment> addShipment(
            @PathVariable Long customerId,
            @Valid @RequestBody Shipment shipment) {
        Shipment savedShipment = shipmentService.addShipment(customerId, shipment);
        return ResponseEntity.ok(savedShipment);
    }

    @PutMapping("/{shipmentId}")
    public ResponseEntity<Shipment> updateShipment(
            @PathVariable Long shipmentId,
            @Valid @RequestBody Shipment shipmentDetails) {
        Shipment updatedShipment = shipmentService.updateShipment(shipmentId, shipmentDetails);
        return ResponseEntity.ok(updatedShipment);
    }

    @DeleteMapping("/{shipmentId}")
    public ResponseEntity<Void> deleteShipment(@PathVariable Long shipmentId) {
        shipmentService.deleteShipment(shipmentId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{customerId}")
    public ResponseEntity<List<Shipment>> getShipmentsByCustomer(@PathVariable Long customerId) {
        List<Shipment> shipments = shipmentService.getShipmentsByCustomer(customerId);
        return ResponseEntity.ok(shipments);
    }

    // **Track a shipment by tracking ID**
    @GetMapping("/track/{trackingId}")
    public ResponseEntity<?> trackShipment(
            @PathVariable String trackingId,
            @RequestParam Long customerId // Pass customerId as a query parameter
    ) {
        Shipment shipment = shipmentService.trackShipment(trackingId);

        // Validate that the logged-in customer's ID matches the shipment's customer
        if (!shipment.getCustomer().getId().equals(customerId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", "Access denied: Shipment does not belong to the customer."));
        }

        return ResponseEntity.ok(shipment);
    }

    // **Reschedule shipment delivery**
    @PutMapping("/{shipmentId}/reschedule")
    public ResponseEntity<Shipment> rescheduleShipment(
            @PathVariable Long shipmentId,
            @RequestBody Map<String, String> rescheduleData) {
        String newDate = rescheduleData.get("newDate");
        String instructions = rescheduleData.get("instructions");
        Shipment updatedShipment = shipmentService.rescheduleShipment(shipmentId, newDate, instructions);
        return ResponseEntity.ok(updatedShipment);
    }
}
