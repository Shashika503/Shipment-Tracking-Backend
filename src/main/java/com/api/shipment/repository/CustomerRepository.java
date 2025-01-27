package com.api.shipment.repository;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.api.shipment.model.Customer;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Customer findByEmailAndPassword(String email, String password);

    Optional <Object> findByEmail(String email);
}
