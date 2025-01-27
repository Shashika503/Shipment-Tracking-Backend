package com.api.shipment.service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.api.shipment.model.Customer;
import com.api.shipment.repository.CustomerRepository;

@Service
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    // Register a new customer
    public Customer registerCustomer(Customer customer) {
        // Check if the email already exists
        if (customerRepository.findByEmail(customer.getEmail()).isPresent()) {
            throw new RuntimeException("Email already registered!");
        }
        return customerRepository.save(customer);
    }
    // Login a customer
    public Customer login(String email, String password) {
        return customerRepository.findByEmailAndPassword(email, password);
    }
}