package com.api.shipment.service;

import com.api.shipment.model.Customer;
import com.api.shipment.model.Shipment;
import com.api.shipment.repository.CustomerRepository;
import com.api.shipment.repository.ShipmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;


@Service
public class ShipmentService {

    @Autowired
    private ShipmentRepository shipmentRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private EmailService emailService;

    public Shipment addShipment(Long customerId, Shipment shipment) {
        // Find the customer by ID or throw an exception if not found
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
    
        // Check if a shipment with the same trackingId already exists
        if (shipmentRepository.findByTrackingId(shipment.getTrackingId()).isPresent()) {
            throw new RuntimeException("Tracking ID already exists!");
        }

          // Ensure createdDate is set (this happens automatically with @PrePersist)
    if (shipment.getCreatedDate() == null) {
        shipment.setCreatedDate(LocalDate.now());
    }
        // Set the customer for the shipment
        shipment.setCustomer(customer);

        
    
        // Save and return the shipment
        return shipmentRepository.save(shipment);
    }
    
    public List<Shipment> getShipmentsByCustomer(Long customerId) {
        return shipmentRepository.findByCustomerId(customerId);
    }
    public Shipment updateShipment(Long shipmentId, Shipment shipmentDetails) {
        Shipment existingShipment = shipmentRepository.findById(shipmentId)
                .orElseThrow(() -> new RuntimeException("Shipment not found"));

        existingShipment.setTrackingId(shipmentDetails.getTrackingId());
        existingShipment.setStatus(shipmentDetails.getStatus());
        existingShipment.setEstimatedDelivery(shipmentDetails.getEstimatedDelivery());
        existingShipment.setUpdates(shipmentDetails.getUpdates());

        return shipmentRepository.save(existingShipment);
    }

    public void deleteShipment(Long shipmentId) {
        Shipment existingShipment = shipmentRepository.findById(shipmentId)
                .orElseThrow(() -> new RuntimeException("Shipment not found"));
        shipmentRepository.delete(existingShipment);
    }
     // **Track a Shipment by Tracking ID**
     public Shipment trackShipment(String trackingId) {
        return shipmentRepository.findByTrackingId(trackingId)
                .orElseThrow(() -> new RuntimeException("Shipment with tracking ID not found"));
    }

    // **Reschedule Shipment Delivery**
    public Shipment rescheduleShipment(Long shipmentId, String newDate, String instructions) {
        Shipment existingShipment = shipmentRepository.findById(shipmentId)
                .orElseThrow(() -> new RuntimeException("Shipment not found"));

        // Prevent rescheduling to the same date
    if (existingShipment.getEstimatedDelivery().equals(newDate)) {
        throw new RuntimeException("This shipment is already scheduled for this date.");
    }        
    
        // Update the estimated delivery date
        existingShipment.setEstimatedDelivery(newDate);
    
        // Update the instructions
        existingShipment.setInstructions(instructions);
        existingShipment.setRescheduledDate(LocalDate.now()); // Track reschedule date
    
        // Save and return the updated shipment
        Shipment updatedShipment = shipmentRepository.save(existingShipment);

        //Send email notification to customer
        String emailSubject = "Your Shipment Delivery Has Been Rescheduled";
        String emailBody = "Dear Customer,\n\n"
                + "Your shipment (Tracking ID: " + existingShipment.getTrackingId() + ") "
                + "has been rescheduled to " + newDate + ".\n\n"
                + "Instructions: " + instructions + "\n\n"
                + "Thank you for using our service!\n\nBest Regards,\nShipment Team";

        // Retrieve customer email
        String customerEmail = existingShipment.getCustomer().getEmail();

        emailService.sendEmail(customerEmail, emailSubject, emailBody);
        
        return updatedShipment;
    }
    

}