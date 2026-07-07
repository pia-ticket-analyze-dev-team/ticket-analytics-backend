package com.grup6.telco_ticket_analyzer.service;

import com.grup6.telco_ticket_analyzer.dto.CustomerRequestDto;
import com.grup6.telco_ticket_analyzer.dto.CustomerResponseDto;
import com.grup6.telco_ticket_analyzer.dto.PagedResponseDto;

import java.util.UUID;

public interface CustomerServiceInterface {

    PagedResponseDto<CustomerResponseDto> getAllCustomers(int page, int size);

    CustomerResponseDto getCustomerById(UUID id);

    CustomerResponseDto createCustomer(CustomerRequestDto requestDto);

    CustomerResponseDto updateCustomer(UUID id, CustomerRequestDto requestDto);

    void deleteCustomer(UUID id);
}
