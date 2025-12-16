package com.example.royalpizza.controller;

import com.example.royalpizza.DTO.CustomerDTO;
import com.example.royalpizza.DTO.LoginDTO;
import com.example.royalpizza.DTO.NewCustomerDTO;
import com.example.royalpizza.DTO.AdaptedOrderLine;
import com.example.royalpizza.config.JwtTokenManager;
import com.example.royalpizza.entity.Customer;
import com.example.royalpizza.exception.CustomerException;
import com.example.royalpizza.exception.ErrorMessages;
import com.example.royalpizza.service.CustomerService;
import com.example.royalpizza.service.InvoiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/customers")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @Autowired
    private InvoiceService invoiceService;

    @Autowired
    private JwtTokenManager jwtTokenManager;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody LoginDTO loginDTO) throws CustomerException {
        String token = customerService.loginCustomer(loginDTO.getEmail(), loginDTO.getPassword());
        Map<String, List<AdaptedOrderLine>> basket = customerService.getBasket(customerService.getCustomer(loginDTO.getEmail()));
        Map<String, Object> map = new HashMap<>();
        map.put("token", token);
        map.put("basket", basket);
        return map;
    }

    @GetMapping("/basket")
    public Map<String, List<AdaptedOrderLine>> getBasket(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long idCustomer = (Long) auth.getPrincipal();
        Customer customer = customerService.getCustomer(idCustomer);
        return customerService.getBasket(customer);
    }

    @GetMapping("checkToken")
    public void checkToken(){

    }

    @PostMapping("saveBasket")
    public void saveBasket(@RequestBody Map<String, List<AdaptedOrderLine>> basket) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long idCustomer = (Long) auth.getPrincipal();
        this.invoiceService.saveBasket(basket, customerService.getCustomer(idCustomer));
    }

    @PostMapping("/update")
    public Map<String, Object> update(@RequestBody CustomerDTO customerDTO) throws CustomerException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long idCustomer = (Long) auth.getPrincipal();
        if(idCustomer != customerDTO.getIdCustomer()) {
            System.err.println("Token customer ID does not match the provided customer ID during the update of customer info.");
            throw new CustomerException(ErrorMessages.INVALID_TOKEN);
        } else {
            Customer customer = this.customerService.getCustomer(idCustomer);
            customer.setFirstName(customerDTO.getFirstName());
            customer.setLastName(customerDTO.getLastName());
            customer.setEmailAddress(customerDTO.getEmailAddress());
            customer.setIsAdmin(customerDTO.isAdmin());
            this.customerService.updateCustomer(customer);
            Map<String, Object> map = new HashMap<>();
            map.put("token", jwtTokenManager.generateToken(customer));
            map.put("message", "Changements opérés avec succès.");
            return map;
        }
    }

    @PostMapping("/updatePassword")
    public Map<String, Object> updatePassword(@RequestBody String password) throws CustomerException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long idCustomer = (Long) auth.getPrincipal();
        Customer customer = this.customerService.getCustomer(idCustomer);
        customer.setPassword(passwordEncoder.encode(password));
        this.customerService.updateCustomer(customer);
        Map<String, Object> map = new HashMap<>();
        map.put("token", jwtTokenManager.generateToken(customer));
        map.put("message", "Mot de passe mis à jour avec succès.");
        return map;
    }

    @PostMapping("walletRecharge")
    public Map<String, Object> walletRecharge(@RequestBody Double amount) throws CustomerException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long idCustomer = (Long) auth.getPrincipal();
        Customer customer = this.customerService.getCustomer(idCustomer);
        customer.setWallet(customer.getWallet().add(java.math.BigDecimal.valueOf(amount)));
        this.customerService.updateCustomer(customer);
        Map<String, Object> map = new HashMap<>();
        map.put("token", jwtTokenManager.generateToken(customer));
        map.put("message", "Portefeuille rechargé avec succès.");
        return map;
    }

    @PostMapping("/deleteAccount")
    public void deleteAccount() throws CustomerException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long idCustomer = (Long) auth.getPrincipal();
        this.customerService.deleteCustomer(idCustomer);
    }

    @PostMapping("/register")
    public Customer register(@RequestBody NewCustomerDTO newCustomerDTO) {
        return customerService.addCustomer(newCustomerDTO);
    }
}



