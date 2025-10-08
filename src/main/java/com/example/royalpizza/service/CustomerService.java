package com.example.royalpizza.service;

import com.example.royalpizza.DTO.NewCustomerDTO;
import com.example.royalpizza.entity.Customer;
import com.example.royalpizza.exception.CustomerException;
import com.example.royalpizza.exception.ErrorMessages;
import com.example.royalpizza.mapper.CustomerMapper;
import com.example.royalpizza.repository.CustomerRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.SignatureAlgorithm;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;


@Service
public class CustomerService {

    private SecretKey jwtSecret = Keys.secretKeyFor(SignatureAlgorithm.HS512);

    @Value("${jwt.expiration}")
    private long jwtExpirationMs;


    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;

    public CustomerService(CustomerRepository customerRepository, PasswordEncoder passwordEncoder) {
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

    public Customer updateCustomer(Customer customer) {
        customer.setPassword(passwordEncoder.encode(customer.getPassword()));
        return customerRepository.save(customer);
    }

    public String loginCustomer(String email, String password) {
        Customer customer = getCustomer(email);
        if (customer != null) {
            if (passwordEncoder.matches(password, customer.getPassword())) {
                return generateToken(customer);
            } else {
                throw new CustomerException(ErrorMessages.INVALID_PASSWORD);
            }
        }
        else {
            throw new CustomerException(ErrorMessages.CUSTOMER_NOT_FOUND + " : " + email);
        }
    }

    private String generateToken(Customer customer) {
        return Jwts.builder()
                .setSubject(customer.getEmailAddress())
                .claim("id", customer.getIdCustomer())
                .claim("firstName", customer.getFirstName())
                .claim("lastName", customer.getLastName())
                .claim("emailAddress", customer.getEmailAddress())
                .claim("wallet", customer.getWallet())
                .claim("isAdmin", customer.getIsAdmin())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

    public List<Customer> findCustomerByIsAdminTrue() {
        return customerRepository.findByIsAdminTrue();
    }

    public List<Customer> findCustomerByIsAdminFalse() {
        return customerRepository.findByIsAdminFalse();
    }

}

