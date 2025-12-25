package com.example.royalpizza.service;

import com.example.royalpizza.DTO.AdaptedOrderLine;
import com.example.royalpizza.DTO.NewCustomerDTO;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
public class CustomerService {

    private final PizzaService pizzaService;
    private final CustomerRepository customerRepository;
    private final InvoiceRepository invoiceRepository;
    private final OrderLineRepository orderLineRepository;
    private final JwtTokenManager jwtTokenManager;
    private final PasswordEncoder passwordEncoder;
    private final CustomerMapper customerMapper;
    private final OrderLineMapper orderLineMapper;

    public CustomerService(PizzaService pizzaService, JwtTokenManager jwtTokenManager, CustomerRepository customerRepository, InvoiceRepository invoiceRepository, OrderLineRepository orderLineRepository, PasswordEncoder passwordEncoder, CustomerMapper customerMapper, OrderLineMapper orderLineMapper) {
        this.pizzaService = pizzaService;
        this.jwtTokenManager = jwtTokenManager;
        this.invoiceRepository = invoiceRepository;
        this.orderLineRepository = orderLineRepository;
        this.passwordEncoder = passwordEncoder;
        this.customerRepository = customerRepository;
        this.customerMapper = customerMapper;
        this.orderLineMapper = orderLineMapper;
    }


    public Customer getCustomer(Object object) {
        Optional<Customer> customerOpt;
        if (object instanceof Long) {
            customerOpt = customerRepository.findById((Long) object);
        } else if (object instanceof String) {
            customerOpt = customerRepository.findByEmailAddress((String) object).stream().findFirst();
        } else {
            log.error("ID type not supported for customer: " + object);
            throw new IllegalArgumentException("Type d'identifiant non supporté pour le client : " + object);
        }
        return customerOpt.orElse(null);
    }

    public Customer addCustomer(NewCustomerDTO newCustomerDTO) {
        Customer customer = this.customerMapper.toEntity(newCustomerDTO);
        customer.setAvailable(true);
        Customer customerFound = customerRepository.findByEmailAddress(newCustomerDTO.getEmailAddress()).orElse(null);
        if (customerFound != null) {
            if (customerFound.getAvailable()) {
                log.error("Customer already exists: " + newCustomerDTO.getEmailAddress());
                throw new CustomerException(ErrorMessages.CUSTOMER_ALREADY_EXISTS);
            } else {
                customer.setIdCustomer(customerFound.getIdCustomer());

            }
        }
        customer.setPassword(passwordEncoder.encode(newCustomerDTO.getPassword()));
        return customerRepository.save(customer);
    }

    public void deleteCustomer(Object object) {
        Customer customer = getCustomer(object);

        List<Customer> adminCustomers = customerRepository.findByIsAdminTrue();

        boolean isCurrentlyAdmin = adminCustomers.stream()
                .anyMatch(c -> c.getIdCustomer().equals(customer.getIdCustomer()));
        long countAdminAvailable = adminCustomers.stream()
                .filter(Customer::getAvailable)
                .count();
        boolean isLastAdminAvailable = countAdminAvailable <= 1;
        if (isCurrentlyAdmin && isLastAdminAvailable && customer.getAvailable()) {
            log.error("Cannot demote or delete the last admin customer: " + customer.getEmailAddress());
            throw new CustomerException(ErrorMessages.LAST_ADMIN_CANNOT_BE_DEMOTED);
        }
        customer.setAvailable(false);
        customerRepository.save(customer);
    }

    public Customer updateCustomer(Customer customer) {
        return customerRepository.save(customer);
    }

    public String loginCustomer(String email, String password) throws CustomerException {
        Customer customer = getCustomer(email);
        if (customer != null && customer.getAvailable()) {
            if (passwordEncoder.matches(password, customer.getPassword())) {
                return jwtTokenManager.generateToken(customer);
            } else {
                log.error("Invalid password attempt for customer: " + email);
                throw new CustomerException(ErrorMessages.INVALID_PASSWORD);
            }
        } else if (customer != null && !customer.getAvailable()) {
            log.error("Attempt to login to an unavailable customer account: " + email);
            throw new CustomerException(ErrorMessages.CUSTOMER_UNVAILABLE + " : " + email);
        } else {
            log.error("Customer not found during login attempt: " + email);
            throw new CustomerException(ErrorMessages.CUSTOMER_NOT_FOUND + " : " + email);
        }
    }

    public Map<String, List<AdaptedOrderLine>> getBasket(Customer customer) {
        Invoice invoice = invoiceRepository
                .findFirstByCustomerAndFinalizedFalseOrderByDateDesc(customer)
                .orElseGet(Invoice::new);
        List<OrderLine> orderLines = orderLineRepository.findByInvoice_IdInvoice(invoice.getIdInvoice());
        Map<String, List<AdaptedOrderLine>> basket = new HashMap<>();
        if (orderLines != null) {
            for (OrderLine orderLine : orderLines) {
                String namePizza = orderLine.getPizza().getNamePizza();
                Map<String, BigDecimal> priceBySize = pizzaService.getPriceRangeByPizzaAtDate(namePizza, LocalDateTime.now());
                AdaptedOrderLine adaptedOrderLineDTO = this.orderLineMapper.toAdapted(orderLine);
                adaptedOrderLineDTO.setPrice(priceBySize.get(orderLine.getSize().getNameSize()));
                if (!basket.containsKey(namePizza)) {
                    basket.put(namePizza, new ArrayList<>(List.of(adaptedOrderLineDTO)));
                } else {
                    basket.get(namePizza).add(adaptedOrderLineDTO);
                }
            }
        }
        return basket;
    }


}

