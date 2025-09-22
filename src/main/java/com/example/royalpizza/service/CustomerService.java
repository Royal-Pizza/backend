package com.example.royalpizza.service;

import com.example.royalpizza.DTO.CustomerDTO;
import com.example.royalpizza.DTO.NewCustomerDTO;
import com.example.royalpizza.entity.Customer;
import com.example.royalpizza.mapper.CustomerMapper;
import com.example.royalpizza.repository.CustomerRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
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
        return customerRepository.save(CustomerMapper.toEntity(newCustomerDTO));
    }

    public void deleteCustomer(Object object) {
        Customer customer = getCustomer(object);
        if (customer != null) {
            customerRepository.delete(customer);
        }
    }

    public Customer updateCustomer(CustomerDTO customerDTO, String password) {
        Customer customer = getCustomer(customerDTO.getIdCustomer());
        if (customer == null) {
            throw new RuntimeException("Client non trouvé avec l'attribut : " + customerDTO.getIdCustomer());
        }
        customer.setPassword(password);
        return customerRepository.save(customer);
    }

    public List<Customer> findCustomerByIsAdminTrue() {
        return customerRepository.findByIsAdminTrue();
    }

    public List<Customer> findCustomerByIsAdminFalse() {
        return customerRepository.findByIsAdminFalse();
    }

}

