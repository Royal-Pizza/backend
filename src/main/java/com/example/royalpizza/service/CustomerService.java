package com.example.royalpizza.service;

import com.example.royalpizza.DTO.NewCustomerDTO;
import com.example.royalpizza.DTO.OrderLineDTO;
import com.example.royalpizza.config.JwtTokenManager;
import com.example.royalpizza.entity.Customer;
import com.example.royalpizza.entity.Invoice;
import com.example.royalpizza.entity.OrderLine;
import com.example.royalpizza.exception.CustomerException;
import com.example.royalpizza.exception.ErrorMessages;
import com.example.royalpizza.mapper.CustomerMapper;
import com.example.royalpizza.mapper.OrderLineMapper;
import com.example.royalpizza.repository.CustomerRepository;
import com.example.royalpizza.repository.InvoiceRepository;
import com.example.royalpizza.repository.OrderLineRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;


@Service
public class CustomerService {

    private final PizzaService pizzaService;
    private final CustomerRepository customerRepository;
    private final InvoiceRepository invoiceRepository;
    private final OrderLineRepository orderLineRepository;
    private final JwtTokenManager jwtTokenManager;
    private final PasswordEncoder passwordEncoder;

    public CustomerService(PizzaService pizzaService, JwtTokenManager jwtTokenManager, CustomerRepository customerRepository, InvoiceRepository invoiceRepository, OrderLineRepository orderLineRepository, PasswordEncoder passwordEncoder) {
        this.pizzaService = pizzaService;
        this.jwtTokenManager = jwtTokenManager;
        this.invoiceRepository = invoiceRepository;
        this.orderLineRepository = orderLineRepository;
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

    public String loginCustomer(String email, String password) throws CustomerException {
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

    public Map<String, List<OrderLineDTO>> getBasket(Customer customer) {
        Invoice invoice = invoiceRepository
                .findFirstByCustomerAndFinalizedFalseOrderByDateDesc(customer)
                .orElseGet(Invoice::new);
        List<OrderLine> orderLines = orderLineRepository.findByInvoice_IdInvoice(invoice.getIdInvoice());
        Map<String, List<OrderLineDTO>> basket = new HashMap<>();
        if (orderLines != null){
            for(OrderLine orderLine : orderLines){
                String namePizza = orderLine.getPizza().getNamePizza();
                Map<String, BigDecimal> priceBySize = pizzaService.getPriceRangeByPizza(namePizza);
                OrderLineDTO orderLineDTO = OrderLineMapper.toDTO(orderLine);
                orderLineDTO.setPrice(priceBySize.get(orderLine.getSize().getNameSize()));
                if (!basket.containsKey(namePizza)){
                    basket.put(namePizza, new ArrayList<>(List.of(orderLineDTO)));
                } else {
                    basket.get(namePizza).add(orderLineDTO);
                }
            }
        }
        return basket;
    }

    public List<Customer> findCustomerByIsAdminTrue() {
        return customerRepository.findByIsAdminTrue();
    }

    public List<Customer> findCustomerByIsAdminFalse() {
        return customerRepository.findByIsAdminFalse();
    }

}

