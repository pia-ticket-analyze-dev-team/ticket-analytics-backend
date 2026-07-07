package com.grup6.telco_ticket_analyzer.controller;

import com.grup6.telco_ticket_analyzer.dto.CustomerRequestDto;
import com.grup6.telco_ticket_analyzer.dto.CustomerResponseDto;
import com.grup6.telco_ticket_analyzer.dto.PagedResponseDto;
import com.grup6.telco_ticket_analyzer.service.CustomerServiceInterface;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerServiceInterface customerService;

    @GetMapping
    public PagedResponseDto<CustomerResponseDto> getAllCustomers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        return customerService.getAllCustomers(page, size);
    }

    @GetMapping("/{id}")
    public CustomerResponseDto getCustomerById(@PathVariable UUID id) {
        return customerService.getCustomerById(id);
    }

    @PostMapping
    public ResponseEntity<CustomerResponseDto> createCustomer(@RequestBody CustomerRequestDto requestDto) {
        CustomerResponseDto created = customerService.createCustomer(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public CustomerResponseDto updateCustomer(@PathVariable UUID id, @RequestBody CustomerRequestDto requestDto) {
        return customerService.updateCustomer(id, requestDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable UUID id) {
        customerService.deleteCustomer(id);
        return ResponseEntity.noContent().build();
    }
}
