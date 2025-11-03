package com.example.royalpizza.service;

import com.example.royalpizza.DTO.NewCustomerDTO;
import com.example.royalpizza.config.JwtTokenManager;
import com.example.royalpizza.entity.Customer;
import com.example.royalpizza.exception.CustomerException;
import com.example.royalpizza.exception.ErrorMessages;
import com.example.royalpizza.mapper.CustomerMapper;
import com.example.royalpizza.repository.CustomerRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;


@Service
public class CustomerService {

    private final JwtTokenManager jwtTokenManager;
    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;

    public CustomerService(JwtTokenManager jwtTokenManager, CustomerRepository customerRepository, PasswordEncoder passwordEncoder) {
        this.jwtTokenManager = jwtTokenManager;
        this.passwordEncoder = passwordEncoder;
        this.customerRepository = customerRepository;
    }

    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    public Customer getCustomer(Object object) {
        Optional<Customer> customerOpt;
        if (object instanceof Long) {
            customerOpt = customerRepository.findById((Long) object);
        } else if (object instanceof String) {
            customerOpt = customerRepository.findByEmailAddress((String) object).stream().findFirst();
        } else {
            throw new IllegalArgumentException("Type d'identifiant non supporté pour le client : " + object);
        }
        return customerOpt.orElse(null);
    }

    public Customer addCustomer(NewCustomerDTO newCustomerDTO) {
        Customer customer = CustomerMapper.toEntity(newCustomerDTO);
        if (customerRepository.findByEmailAddress(newCustomerDTO.getEmailAddress()).isPresent()) {
            throw new CustomerException(ErrorMessages.CUSTOMER_ALREADY_EXISTS);
        }
        customer.setPassword(passwordEncoder.encode(newCustomerDTO.getPassword()));
        return customerRepository.save(customer);
    }

    public void deleteCustomer(Object object) {
        Customer customer = getCustomer(object);
        if (customer != null) {
            customerRepository.delete(customer);
        }
    }

    public Customer updateCustomer(Customer customer, boolean passwordChanged) {
        if (passwordChanged) {
            customer.setPassword(passwordEncoder.encode(customer.getPassword()));
        }
        return customerRepository.save(customer);
    }

    public String loginCustomer(String email, String password) throws IOException, CustomerException {
        Customer customer = getCustomer(email);
        if (customer != null) {
            if (passwordEncoder.matches(password, customer.getPassword())) {
                return jwtTokenManager.generateToken(customer);
            } else {
                throw new CustomerException(ErrorMessages.INVALID_PASSWORD);
            }
        } else {
            throw new CustomerException(ErrorMessages.CUSTOMER_NOT_FOUND + " : " + email);
        }
    }

    public List<Customer> findCustomerByIsAdminTrue() {
        return customerRepository.findByIsAdminTrue();
    }

    public List<Customer> findCustomerByIsAdminFalse() {
        return customerRepository.findByIsAdminFalse();
    }

}

