package com.example.royalpizza.service;

import com.example.royalpizza.DTO.NewCustomerDTO;
import com.example.royalpizza.DTO.AdaptedOrderLine;
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
import java.time.LocalDateTime;
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
        customer.setAvailable(true);
        Customer customerFound = customerRepository.findByEmailAddress(newCustomerDTO.getEmailAddress()).orElse(null);
        if(customerFound != null){
            if(customerFound.getAvailable()) {
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
        customer.setAvailable(false);

        List<Customer> adminCustomers = customerRepository.findByIsAdminTrue();

        boolean isCurrentlyAdmin = adminCustomers.stream()
                .anyMatch(c -> c.getIdCustomer().equals(customer.getIdCustomer()));

        boolean isLastAdmin = adminCustomers.size() == 1;

        if (isLastAdmin && isCurrentlyAdmin) {
            if (!customer.getIsAdmin() || !customer.getAvailable()) {
                throw new CustomerException(ErrorMessages.LAST_ADMIN_CANNOT_BE_DEMOTED);
            }
        }
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
                throw new CustomerException(ErrorMessages.INVALID_PASSWORD);
            }
        } else if (customer != null && !customer.getAvailable()) {
            throw new CustomerException(ErrorMessages.CUSTOMER_UNVAILABLE + " : " + email);
        } else {
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
                AdaptedOrderLine adaptedOrderLineDTO = OrderLineMapper.toAdapted(orderLine);
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

