package com.api.shipment.repository;
import com.api.shipment.model.Shipment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;


public interface ShipmentRepository extends JpaRepository<Shipment, Long> {
    List<Shipment> findByCustomerId(Long customerId);
    // Add this custom method
    Optional<Shipment> findByTrackingId(String trackingId);
}