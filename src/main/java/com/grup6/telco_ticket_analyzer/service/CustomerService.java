package com.grup6.telco_ticket_analyzer.service;

import com.grup6.telco_ticket_analyzer.dto.CustomerRequestDto;
import com.grup6.telco_ticket_analyzer.dto.CustomerResponseDto;
import com.grup6.telco_ticket_analyzer.dto.PagedResponseDto;
import com.grup6.telco_ticket_analyzer.exception.CustomerNotFoundException;
import com.grup6.telco_ticket_analyzer.model.Customer;
import com.grup6.telco_ticket_analyzer.repository.CustomerRepository;
import com.grup6.telco_ticket_analyzer.repository.TicketRepository;
import com.grup6.telco_ticket_analyzer.repository.projection.SatisfactionScoreProjection;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomerService implements CustomerServiceInterface {

    private static final int DEFAULT_PAGE_SIZE = 50;
    private static final int MAX_PAGE_SIZE = 100;
    private static final List<String> OPEN_STATUSES = List.of("OPEN", "IN_PROGRESS");

    private final CustomerRepository customerRepository;
    private final TicketRepository ticketRepository;

    @Override
    public PagedResponseDto<CustomerResponseDto> getAllCustomers(int page, int size, String search, String segment) {
        Pageable pageable = PageRequest.of(
                Math.max(page, 0),
                clampSize(size),
                Sort.by(Sort.Direction.DESC, "createdAt")
        );

        Page<Customer> customerPage = customerRepository.findAll(buildCustomerSpecification(search, segment), pageable);
        List<CustomerResponseDto> content = customerPage.getContent()
                .stream()
                .map(this::toResponseDto)
                .toList();

        return new PagedResponseDto<>(
                content,
                customerPage.getNumber(),
                customerPage.getSize(),
                customerPage.getTotalElements(),
                customerPage.getTotalPages()
        );
    }

    private Specification<Customer> buildCustomerSpecification(String search, String segment) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (search != null && !search.isBlank()) {
                String pattern = "%" + search.toLowerCase() + "%";
                predicates.add(criteriaBuilder.or(
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("firstName")), pattern),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("lastName")), pattern),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("email")), pattern),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("phone")), pattern)
                ));
            }

            if (segment != null && !segment.isBlank()) {
                predicates.add(criteriaBuilder.equal(root.get("segment"), segment));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    private int clampSize(int size) {
        if (size < 1) {
            return DEFAULT_PAGE_SIZE;
        }
        return Math.min(size, MAX_PAGE_SIZE);
    }

    @Override
    public CustomerResponseDto getCustomerById(UUID id) {
        return toResponseDto(findCustomerOrThrow(id));
    }

    @Override
    public CustomerResponseDto createCustomer(CustomerRequestDto requestDto) {
        Customer customer = new Customer();
        applyRequestDto(customer, requestDto);
        customer.setCreatedAt(LocalDate.now());

        return toResponseDto(customerRepository.save(customer));
    }

    @Override
    public CustomerResponseDto updateCustomer(UUID id, CustomerRequestDto requestDto) {
        Customer customer = findCustomerOrThrow(id);
        applyRequestDto(customer, requestDto);

        return toResponseDto(customerRepository.save(customer));
    }

    @Override
    public void deleteCustomer(UUID id) {
        customerRepository.delete(findCustomerOrThrow(id));
    }

    @Override
    public long getTotalTicketCount(UUID customerId) {
        findCustomerOrThrow(customerId);
        return ticketRepository.countByCustomerId(customerId);
    }

    @Override
    public long getOpenTicketCount(UUID customerId) {
        findCustomerOrThrow(customerId);
        return ticketRepository.countByCustomerIdAndStatusIn(customerId, OPEN_STATUSES);
    }

    @Override
    public long getSlaBreachCount(UUID customerId) {
        findCustomerOrThrow(customerId);
        return ticketRepository.countByCustomerIdAndSlaBreached(customerId, true);
    }

    @Override
    public double getAverageSatisfactionScore(UUID customerId) {
        findCustomerOrThrow(customerId);
        return ticketRepository.findByCustomerIdAndCustomerSatisfactionScoreIsNotNull(customerId)
                .stream()
                .mapToInt(SatisfactionScoreProjection::getCustomerSatisfactionScore)
                .average()
                .orElse(0.0);
    }

    private Customer findCustomerOrThrow(UUID id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException(id));
    }

    private void applyRequestDto(Customer customer, CustomerRequestDto requestDto) {
        customer.setFirstName(requestDto.firstName());
        customer.setLastName(requestDto.lastName());
        customer.setEmail(requestDto.email());
        customer.setAddress(requestDto.address());
        customer.setBirthdate(requestDto.birthdate());
        customer.setPhone(requestDto.phone());
        customer.setSegment(requestDto.segment());
    }

    private CustomerResponseDto toResponseDto(Customer customer) {
        return new CustomerResponseDto(
                customer.getId(),
                customer.getFirstName(),
                customer.getLastName(),
                customer.getEmail(),
                customer.getAddress(),
                customer.getBirthdate(),
                customer.getPhone(),
                customer.getCreatedAt(),
                customer.getSegment()
        );
    }
}
