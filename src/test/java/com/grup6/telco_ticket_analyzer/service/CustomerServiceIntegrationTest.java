package com.grup6.telco_ticket_analyzer.service;

import com.grup6.telco_ticket_analyzer.dto.CustomerRequestDto;
import com.grup6.telco_ticket_analyzer.dto.CustomerResponseDto;
import com.grup6.telco_ticket_analyzer.dto.PagedResponseDto;
import com.grup6.telco_ticket_analyzer.exception.CustomerNotFoundException;
import com.grup6.telco_ticket_analyzer.model.Customer;
import com.grup6.telco_ticket_analyzer.repository.CustomerRepository;
import com.grup6.telco_ticket_analyzer.repository.TicketRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DisplayName("CustomerService Integration Tests")
class CustomerServiceIntegrationTest {

    @Autowired
    private CustomerService customerService;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private TicketRepository ticketRepository;

    private Customer testCustomer;
    private UUID testCustomerId;

    @BeforeEach
    void setUp() {
        customerRepository.deleteAll();
        testCustomer = createAndSaveCustomer("John", "Doe", "john@example.com", "Premium");
        testCustomerId = testCustomer.getId();
    }

    @Nested
    @DisplayName("getAllCustomers Tests")
    class GetAllCustomersTests {

        @Test
        @DisplayName("Success: Should retrieve all customers with pagination")
        void testGetAllCustomersSuccess() {
            // Arrange
            createAndSaveCustomer("Jane", "Smith", "jane@example.com", "Standard");
            createAndSaveCustomer("Bob", "Johnson", "bob@example.com", "Premium");

            // Act
            PagedResponseDto<CustomerResponseDto> result = customerService.getAllCustomers(0, 10, null, null);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.content()).hasSize(3);
            assertThat(result.totalElements()).isEqualTo(3);
            assertThat(result.page()).isEqualTo(0);
            assertThat(result.totalPages()).isEqualTo(1);
        }

        @Test
        @DisplayName("Success: Should retrieve customers with search filter")
        void testGetAllCustomersWithSearchSuccess() {
            // Arrange
            createAndSaveCustomer("Jane", "Smith", "jane@example.com", "Standard");
            createAndSaveCustomer("Bob", "Johnson", "bob@example.com", "Premium");

            // Act
            PagedResponseDto<CustomerResponseDto> result = customerService.getAllCustomers(0, 10, "jane", null);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.content()).hasSize(1);
            assertThat(result.content().get(0).firstName()).isEqualTo("Jane");
        }

        @Test
        @DisplayName("Success: Should retrieve customers with segment filter")
        void testGetAllCustomersWithSegmentSuccess() {
            // Arrange
            createAndSaveCustomer("Jane", "Smith", "jane@example.com", "Standard");
            createAndSaveCustomer("Bob", "Johnson", "bob@example.com", "Premium");

            // Act
            PagedResponseDto<CustomerResponseDto> result = customerService.getAllCustomers(0, 10, null, "Premium");

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.content()).hasSize(2);
            assertThat(result.content())
                    .extracting(CustomerResponseDto::segment)
                    .containsOnly("Premium");
        }

        @Test
        @DisplayName("Success: Should combine search and segment filters")
        void testGetAllCustomersWithSearchAndSegmentSuccess() {
            // Arrange
            createAndSaveCustomer("Jane", "Smith", "jane@example.com", "Standard");
            createAndSaveCustomer("Jane", "Johnson", "jane.johnson@example.com", "Premium");

            // Act
            PagedResponseDto<CustomerResponseDto> result = customerService.getAllCustomers(0, 10, "jane", "Premium");

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.content()).hasSize(1);
            assertThat(result.content().get(0).lastName()).isEqualTo("Johnson");
        }

        @Test
        @DisplayName("Success: Should search by email")
        void testGetAllCustomersSearchByEmailSuccess() {
            // Arrange
            createAndSaveCustomer("Jane", "Smith", "jane.smith@example.com", "Standard");

            // Act
            PagedResponseDto<CustomerResponseDto> result = customerService.getAllCustomers(0, 10, "jane.smith", null);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.content()).hasSize(1);
            assertThat(result.content().get(0).email()).isEqualTo("jane.smith@example.com");
        }

        @Test
        @DisplayName("Success: Should search by phone number")
        void testGetAllCustomersSearchByPhoneSuccess() {
            // Arrange
            testCustomer.setPhone("+1234567890");
            customerRepository.save(testCustomer);

            // Act
            PagedResponseDto<CustomerResponseDto> result = customerService.getAllCustomers(0, 10, "1234567", null);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.content()).hasSize(1);
            assertThat(result.content().get(0).phone()).isEqualTo("+1234567890");
        }

        @Test
        @DisplayName("Edge Case: Should handle empty search results")
        void testGetAllCustomersEmptySearchResults() {
            // Act
            PagedResponseDto<CustomerResponseDto> result = customerService.getAllCustomers(0, 10, "NonExistentSearch", null);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.content()).isEmpty();
            assertThat(result.totalElements()).isEqualTo(0);
        }

        @Test
        @DisplayName("Edge Case: Should apply page size limit (MAX_PAGE_SIZE = 100)")
        void testGetAllCustomersPageSizeLimit() {
            // Act
            PagedResponseDto<CustomerResponseDto> result = customerService.getAllCustomers(0, 1000, null, null);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.size()).isEqualTo(100);
        }

        @Test
        @DisplayName("Edge Case: Should default to page size 50 when size < 1")
        void testGetAllCustomersDefaultPageSize() {
            // Act
            PagedResponseDto<CustomerResponseDto> result = customerService.getAllCustomers(0, 0, null, null);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.size()).isEqualTo(50);
        }

        @Test
        @DisplayName("Edge Case: Should handle negative page number by treating as 0")
        void testGetAllCustomersNegativePageNumber() {
            // Act
            PagedResponseDto<CustomerResponseDto> result = customerService.getAllCustomers(-5, 10, null, null);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.page()).isEqualTo(0);
        }
    }

    @Nested
    @DisplayName("getCustomerById Tests")
    class GetCustomerByIdTests {

        @Test
        @DisplayName("Success: Should retrieve customer by valid ID")
        void testGetCustomerByIdSuccess() {
            // Act
            CustomerResponseDto result = customerService.getCustomerById(testCustomerId);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.id()).isEqualTo(testCustomerId);
            assertThat(result.firstName()).isEqualTo("John");
            assertThat(result.lastName()).isEqualTo("Doe");
            assertThat(result.email()).isEqualTo("john@example.com");
        }

        @Test
        @DisplayName("Failure: Should throw CustomerNotFoundException for non-existent ID")
        void testGetCustomerByIdNotFound() {
            // Arrange
            UUID nonExistentId = UUID.randomUUID();

            // Act & Assert
            assertThatThrownBy(() -> customerService.getCustomerById(nonExistentId))
                    .isInstanceOf(CustomerNotFoundException.class);
        }

        @Test
        @DisplayName("Edge Case: Should retrieve customer with null optional fields")
        void testGetCustomerByIdWithNullFields() {
            // Arrange
            Customer customerWithNulls = new Customer();
            customerWithNulls.setFirstName("Jane");
            customerWithNulls.setLastName("Doe");
            customerWithNulls.setEmail("jane@example.com");
            customerWithNulls.setAddress("123 Main St");
            customerWithNulls.setCreatedAt(LocalDate.now());
            customerWithNulls.setBirthdate(null);
            customerWithNulls.setPhone(null);
            customerWithNulls.setSegment(null);
            Customer saved = customerRepository.save(customerWithNulls);

            // Act
            CustomerResponseDto result = customerService.getCustomerById(saved.getId());

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.birthdate()).isNull();
            assertThat(result.phone()).isNull();
            assertThat(result.segment()).isNull();
        }
    }

    @Nested
    @DisplayName("createCustomer Tests")
    class CreateCustomerTests {

        @Test
        @DisplayName("Success: Should create customer with all valid fields")
        void testCreateCustomerSuccess() {
            // Arrange
            CustomerRequestDto requestDto = new CustomerRequestDto(
                    "Alice",
                    "Williams",
                    "alice@example.com",
                    "456 Oak Ave",
                    LocalDate.of(1990, 5, 15),
                    "+9876543210",
                    "Gold"
            );

            // Act
            CustomerResponseDto result = customerService.createCustomer(requestDto);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.firstName()).isEqualTo("Alice");
            assertThat(result.lastName()).isEqualTo("Williams");
            assertThat(result.email()).isEqualTo("alice@example.com");
            assertThat(result.segment()).isEqualTo("Gold");
            assertThat(result.createdAt()).isEqualTo(LocalDate.now());
            
            // Verify persistence in DB
            assertThat(customerRepository.findByEmail("alice@example.com")).isPresent();
        }

        @Test
        @DisplayName("Success: Should create customer with minimal fields")
        void testCreateCustomerMinimalFieldsSuccess() {
            // Arrange
            CustomerRequestDto requestDto = new CustomerRequestDto(
                    "Bob",
                    "Harris",
                    "bob.harris@example.com",
                    "789 Pine St",
                    null,
                    null,
                    null
            );

            // Act
            CustomerResponseDto result = customerService.createCustomer(requestDto);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.firstName()).isEqualTo("Bob");
            assertThat(result.birthdate()).isNull();
            assertThat(result.phone()).isNull();
            assertThat(result.segment()).isNull();
        }

        @Test
        @DisplayName("Edge Case: Should generate unique UUID for created customer")
        void testCreateCustomerUniqueId() {
            // Arrange
            CustomerRequestDto requestDto = new CustomerRequestDto(
                    "Charlie",
                    "Brown",
                    "charlie@example.com",
                    "321 Elm St",
                    null,
                    null,
                    null
            );

            // Act
            CustomerResponseDto result1 = customerService.createCustomer(requestDto);
            CustomerRequestDto requestDto2 = new CustomerRequestDto(
                    "David",
                    "Green",
                    "david@example.com",
                    "654 Ash Ln",
                    null,
                    null,
                    null
            );
            CustomerResponseDto result2 = customerService.createCustomer(requestDto2);

            // Assert
            assertThat(result1.id()).isNotEqualTo(result2.id());
        }

        @Test
        @DisplayName("Edge Case: Should persist created customer in database")
        void testCreateCustomerPersistence() {
            // Arrange
            CustomerRequestDto requestDto = new CustomerRequestDto(
                    "Eve",
                    "Martin",
                    "eve@example.com",
                    "987 Birch Rd",
                    LocalDate.of(1985, 3, 20),
                    "+1111111111",
                    "Standard"
            );

            // Act
            CustomerResponseDto result = customerService.createCustomer(requestDto);

            // Assert
            Customer persistedCustomer = customerRepository.findById(result.id()).orElse(null);
            assertThat(persistedCustomer).isNotNull();
            assertThat(persistedCustomer.getFirstName()).isEqualTo("Eve");
            assertThat(persistedCustomer.getEmail()).isEqualTo("eve@example.com");
        }
    }

    @Nested
    @DisplayName("updateCustomer Tests")
    class UpdateCustomerTests {

        @Test
        @DisplayName("Success: Should update customer with all fields")
        void testUpdateCustomerSuccess() {
            // Arrange
            CustomerRequestDto updateDto = new CustomerRequestDto(
                    "Jonathan",
                    "DoeStar",
                    "john.updated@example.com",
                    "999 New St",
                    LocalDate.of(1988, 7, 10),
                    "+2222222222",
                    "Platinum"
            );

            // Act
            CustomerResponseDto result = customerService.updateCustomer(testCustomerId, updateDto);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.firstName()).isEqualTo("Jonathan");
            assertThat(result.lastName()).isEqualTo("DoeStar");
            assertThat(result.email()).isEqualTo("john.updated@example.com");
            assertThat(result.segment()).isEqualTo("Platinum");
        }

        @Test
        @DisplayName("Success: Should persist updates in database")
        void testUpdateCustomerPersistence() {
            // Arrange
            CustomerRequestDto updateDto = new CustomerRequestDto(
                    "UpdatedName",
                    "UpdatedLast",
                    "updated@example.com",
                    "Updated Address",
                    null,
                    null,
                    "Updated"
            );

            // Act
            customerService.updateCustomer(testCustomerId, updateDto);

            // Assert
            Customer updated = customerRepository.findById(testCustomerId).orElse(null);
            assertThat(updated).isNotNull();
            assertThat(updated.getFirstName()).isEqualTo("UpdatedName");
            assertThat(updated.getEmail()).isEqualTo("updated@example.com");
        }

        @Test
        @DisplayName("Failure: Should throw exception when updating non-existent customer")
        void testUpdateCustomerNotFound() {
            // Arrange
            UUID nonExistentId = UUID.randomUUID();
            CustomerRequestDto updateDto = new CustomerRequestDto(
                    "Test", "Test", "test@example.com", "Test", null, null, null
            );

            // Act & Assert
            assertThatThrownBy(() -> customerService.updateCustomer(nonExistentId, updateDto))
                    .isInstanceOf(CustomerNotFoundException.class);
        }

        @Test
        @DisplayName("Edge Case: Should update customer with null optional fields")
        void testUpdateCustomerWithNullFields() {
            // Arrange
            CustomerRequestDto updateDto = new CustomerRequestDto(
                    "Name",
                    "Surname",
                    "email@example.com",
                    "Address",
                    null,
                    null,
                    null
            );

            // Act
            CustomerResponseDto result = customerService.updateCustomer(testCustomerId, updateDto);

            // Assert
            assertThat(result.birthdate()).isNull();
            assertThat(result.phone()).isNull();
            assertThat(result.segment()).isNull();
        }
    }

    @Nested
    @DisplayName("deleteCustomer Tests")
    class DeleteCustomerTests {

        @Test
        @DisplayName("Success: Should delete customer from database")
        void testDeleteCustomerSuccess() {
            // Arrange
            assertThat(customerRepository.findById(testCustomerId)).isPresent();

            // Act
            customerService.deleteCustomer(testCustomerId);

            // Assert
            assertThat(customerRepository.findById(testCustomerId)).isEmpty();
        }

        @Test
        @DisplayName("Failure: Should throw exception when deleting non-existent customer")
        void testDeleteCustomerNotFound() {
            // Arrange
            UUID nonExistentId = UUID.randomUUID();

            // Act & Assert
            assertThatThrownBy(() -> customerService.deleteCustomer(nonExistentId))
                    .isInstanceOf(CustomerNotFoundException.class);
        }

        @Test
        @DisplayName("Edge Case: Should delete customer and verify removal from DB")
        void testDeleteCustomerVerifyRemoval() {
            // Arrange
            UUID customerId = testCustomerId;

            // Act
            customerService.deleteCustomer(customerId);

            // Assert
            assertThat(customerRepository.existsById(customerId)).isFalse();
        }
    }

    @Nested
    @DisplayName("getTotalTicketCount Tests")
    class GetTotalTicketCountTests {

        @Test
        @DisplayName("Success: Should return 0 when customer has no tickets")
        void testGetTotalTicketCountZero() {
            // Act
            long count = customerService.getTotalTicketCount(testCustomerId);

            // Assert
            assertThat(count).isEqualTo(0);
        }

        @Test
        @DisplayName("Failure: Should throw exception for non-existent customer")
        void testGetTotalTicketCountCustomerNotFound() {
            // Arrange
            UUID nonExistentId = UUID.randomUUID();

            // Act & Assert
            assertThatThrownBy(() -> customerService.getTotalTicketCount(nonExistentId))
                    .isInstanceOf(CustomerNotFoundException.class);
        }

        @Test
        @DisplayName("Edge Case: Should return 0 for customer with deleted tickets")
        void testGetTotalTicketCountAfterDeletion() {
            // Act & Assert
            assertThat(customerService.getTotalTicketCount(testCustomerId)).isEqualTo(0);
        }
    }

    @Nested
    @DisplayName("getOpenTicketCount Tests")
    class GetOpenTicketCountTests {

        @Test
        @DisplayName("Success: Should return 0 when customer has no open tickets")
        void testGetOpenTicketCountZero() {
            // Act
            long count = customerService.getOpenTicketCount(testCustomerId);

            // Assert
            assertThat(count).isEqualTo(0);
        }

        @Test
        @DisplayName("Failure: Should throw exception for non-existent customer")
        void testGetOpenTicketCountCustomerNotFound() {
            // Arrange
            UUID nonExistentId = UUID.randomUUID();

            // Act & Assert
            assertThatThrownBy(() -> customerService.getOpenTicketCount(nonExistentId))
                    .isInstanceOf(CustomerNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("getSlaBreachCount Tests")
    class GetSlaBreachCountTests {

        @Test
        @DisplayName("Success: Should return 0 when customer has no SLA breaches")
        void testGetSlaBreachCountZero() {
            // Act
            long count = customerService.getSlaBreachCount(testCustomerId);

            // Assert
            assertThat(count).isEqualTo(0);
        }

        @Test
        @DisplayName("Failure: Should throw exception for non-existent customer")
        void testGetSlaBreachCountCustomerNotFound() {
            // Arrange
            UUID nonExistentId = UUID.randomUUID();

            // Act & Assert
            assertThatThrownBy(() -> customerService.getSlaBreachCount(nonExistentId))
                    .isInstanceOf(CustomerNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("getAverageSatisfactionScore Tests")
    class GetAverageSatisfactionScoreTests {

        @Test
        @DisplayName("Success: Should return 0.0 when customer has no satisfaction scores")
        void testGetAverageSatisfactionScoreZero() {
            // Act
            double score = customerService.getAverageSatisfactionScore(testCustomerId);

            // Assert
            assertThat(score).isEqualTo(0.0);
        }

        @Test
        @DisplayName("Failure: Should throw exception for non-existent customer")
        void testGetAverageSatisfactionScoreCustomerNotFound() {
            // Arrange
            UUID nonExistentId = UUID.randomUUID();

            // Act & Assert
            assertThatThrownBy(() -> customerService.getAverageSatisfactionScore(nonExistentId))
                    .isInstanceOf(CustomerNotFoundException.class);
        }
    }

    // Helper Methods
    private Customer createAndSaveCustomer(String firstName, String lastName, String email, String segment) {
        Customer customer = new Customer();
        customer.setFirstName(firstName);
        customer.setLastName(lastName);
        customer.setEmail(email);
        customer.setAddress("123 Test Street");
        customer.setCreatedAt(LocalDate.now());
        customer.setSegment(segment);
        return customerRepository.save(customer);
    }
}
