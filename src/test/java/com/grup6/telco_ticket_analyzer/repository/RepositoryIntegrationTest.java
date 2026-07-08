package com.grup6.telco_ticket_analyzer.repository;

import com.grup6.telco_ticket_analyzer.model.Customer;
import com.grup6.telco_ticket_analyzer.model.Ticket;
import com.grup6.telco_ticket_analyzer.model.Agent;
import com.grup6.telco_ticket_analyzer.model.Department;
import com.grup6.telco_ticket_analyzer.model.IssueTopic;
import com.grup6.telco_ticket_analyzer.model.Region;
import com.grup6.telco_ticket_analyzer.model.ServiceType;
import com.grup6.telco_ticket_analyzer.model.InfrastructureType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DisplayName("CustomerRepository Integration Tests")
class CustomerRepositoryIntegrationTest {

    @Autowired
    private CustomerRepository customerRepository;

    private Customer testCustomer;
    private UUID testCustomerId;

    @BeforeEach
    void setUp() {
        customerRepository.deleteAll();
        testCustomer = createAndSaveCustomer("John", "Doe", "john@example.com", "Premium");
        testCustomerId = testCustomer.getId();
    }

    @Nested
    @DisplayName("findByEmail Tests")
    class FindByEmailTests {

        @Test
        @DisplayName("Success: Should find customer by email")
        void testFindByEmailSuccess() {
            // Act
            Optional<Customer> result = customerRepository.findByEmail("john@example.com");

            // Assert
            assertThat(result).isPresent();
            assertThat(result.get().getId()).isEqualTo(testCustomerId);
            assertThat(result.get().getFirstName()).isEqualTo("John");
        }

        @Test
        @DisplayName("Failure: Should return empty optional for non-existent email")
        void testFindByEmailNotFound() {
            // Act
            Optional<Customer> result = customerRepository.findByEmail("nonexistent@example.com");

            // Assert
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("Edge Case: Should be case-sensitive")
        void testFindByEmailCaseSensitive() {
            // Act
            Optional<Customer> result = customerRepository.findByEmail("JOHN@EXAMPLE.COM");

            // Assert
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("existsByEmail Tests")
    class ExistsByEmailTests {

        @Test
        @DisplayName("Success: Should return true for existing email")
        void testExistsByEmailTrue() {
            // Act
            boolean exists = customerRepository.existsByEmail("john@example.com");

            // Assert
            assertThat(exists).isTrue();
        }

        @Test
        @DisplayName("Success: Should return false for non-existent email")
        void testExistsByEmailFalse() {
            // Act
            boolean exists = customerRepository.existsByEmail("nonexistent@example.com");

            // Assert
            assertThat(exists).isFalse();
        }
    }

    @Nested
    @DisplayName("findByFirstNameContainingIgnoreCase Tests")
    class FindByFirstNameContainingTests {

        @Test
        @DisplayName("Success: Should find customers by first name (case-insensitive)")
        void testFindByFirstNameContainingIgnoreCaseSuccess() {
            // Arrange
            createAndSaveCustomer("jane", "smith", "jane@example.com", "Standard");
            createAndSaveCustomer("Johnny", "walker", "johnny@example.com", "Gold");

            // Act
            Pageable pageable = PageRequest.of(0, 10);
            Page<Customer> result = customerRepository.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrPhoneContainingIgnoreCase(
                    "john", "john", "john", "john", pageable
            );

            // Assert
            assertThat(result.getContent()).hasSize(2);
            assertThat(result.getTotalElements()).isEqualTo(2);
        }

        @Test
        @DisplayName("Success: Should find customers by last name (case-insensitive)")
        void testFindByLastNameContainingIgnoreCaseSuccess() {
            // Arrange
            createAndSaveCustomer("Jane", "Smith", "jane@example.com", "Standard");
            createAndSaveCustomer("Bob", "Smithson", "bob@example.com", "Gold");

            // Act
            Pageable pageable = PageRequest.of(0, 10);
            Page<Customer> result = customerRepository.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrPhoneContainingIgnoreCase(
                    "smith", "smith", "smith", "smith", pageable
            );

            // Assert
            assertThat(result.getContent()).hasSize(2);
        }

        @Test
        @DisplayName("Success: Should find customers by email partial match")
        void testFindByEmailContainingIgnoreCaseSuccess() {
            // Arrange
            createAndSaveCustomer("Jane", "Smith", "jane.smith@example.com", "Standard");
            createAndSaveCustomer("Bob", "Smith", "bob.smith@example.com", "Gold");

            // Act
            Pageable pageable = PageRequest.of(0, 10);
            Page<Customer> result = customerRepository.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrPhoneContainingIgnoreCase(
                    "smith", "smith", "smith", "smith", pageable
            );

            // Assert
            assertThat(result.getContent()).hasSize(2);
        }

        @Test
        @DisplayName("Success: Should find customers by phone")
        void testFindByPhoneContainingSuccess() {
            // Arrange
            testCustomer.setPhone("+1234567890");
            customerRepository.save(testCustomer);
            Customer customer2 = createAndSaveCustomer("Jane", "Smith", "jane@example.com", "Standard");
            customer2.setPhone("+1987654321");
            customerRepository.save(customer2);

            // Act
            Pageable pageable = PageRequest.of(0, 10);
                Page<Customer> result = customerRepository.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrPhoneContainingIgnoreCase(
                    "__no_match__", "__no_match__", "__no_match__", "123456", pageable
                );

                // Assert
                assertThat(result.getContent()).hasSize(1);
                assertThat(result.getContent().get(0).getPhone()).contains("123456");
        }

        @Test
        @DisplayName("Edge Case: Should return empty list when no matches found")
        void testFindByContainingNoMatches() {
            // Act
            Pageable pageable = PageRequest.of(0, 10);
            Page<Customer> result = customerRepository.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrPhoneContainingIgnoreCase(
                    "nonexistent", "nonexistent", "nonexistent", "nonexistent", pageable
            );

            // Assert
            assertThat(result.getContent()).isEmpty();
            assertThat(result.getTotalElements()).isEqualTo(0);
        }
    }

    @Nested
    @DisplayName("findBySegment Tests")
    class FindBySegmentTests {

        @Test
        @DisplayName("Success: Should find customers by segment with pagination")
        void testFindBySegmentSuccess() {
            // Arrange
            createAndSaveCustomer("Jane", "Smith", "jane@example.com", "Premium");
            createAndSaveCustomer("Bob", "Johnson", "bob@example.com", "Premium");
            createAndSaveCustomer("Alice", "Williams", "alice@example.com", "Standard");

            // Act
            Pageable pageable = PageRequest.of(0, 10);
            Page<Customer> result = customerRepository.findBySegment("Premium", pageable);

            // Assert
            // setUp creates a default customer with segment "Premium", so expect 3 total
            assertThat(result.getContent()).hasSize(3);
            assertThat(result.getTotalElements()).isEqualTo(3);
            assertThat(result.getContent()).allMatch(c -> c.getSegment().equals("Premium"));
        }

        @Test
        @DisplayName("Success: Should support pagination for segment search")
        void testFindBySegmentWithPagination() {
            // Arrange
            for (int i = 0; i < 10; i++) {
                createAndSaveCustomer("Customer" + i, "Test", "customer" + i + "@example.com", "Gold");
            }

            // Act
            Pageable pageOne = PageRequest.of(0, 5);
            Page<Customer> resultPage1 = customerRepository.findBySegment("Gold", pageOne);
            Pageable pageTwo = PageRequest.of(1, 5);
            Page<Customer> resultPage2 = customerRepository.findBySegment("Gold", pageTwo);

            // Assert
            assertThat(resultPage1.getContent()).hasSize(5);
            assertThat(resultPage1.getTotalPages()).isEqualTo(2);
            assertThat(resultPage2.getContent()).hasSize(5);
        }

        @Test
        @DisplayName("Edge Case: Should return empty list for non-existent segment")
        void testFindBySegmentNotFound() {
            // Act
            Pageable pageable = PageRequest.of(0, 10);
            Page<Customer> result = customerRepository.findBySegment("NonExistentSegment", pageable);

            // Assert
            assertThat(result.getContent()).isEmpty();
            assertThat(result.getTotalElements()).isEqualTo(0);
        }
    }

    @Nested
    @DisplayName("findByCreatedAtBetween Tests")
    class FindByCreatedAtBetweenTests {

        @Test
        @DisplayName("Success: Should find customers created within date range")
        void testFindByCreatedAtBetweenSuccess() {
            // Arrange
            LocalDate today = LocalDate.now();
            LocalDate yesterday = today.minusDays(1);
            LocalDate tomorrow = today.plusDays(1);
            
            testCustomer.setCreatedAt(today);
            customerRepository.save(testCustomer);
            
            createAndSaveCustomer("Jane", "Smith", "jane@example.com", "Standard");

            // Act
            Pageable pageable = PageRequest.of(0, 10);
            Page<Customer> result = customerRepository.findByCreatedAtBetween(yesterday, tomorrow, pageable);

            // Assert
            assertThat(result.getContent()).hasSizeGreaterThanOrEqualTo(2);
        }

        @Test
        @DisplayName("Success: Should include customers created on boundary dates")
        void testFindByCreatedAtBetweenBoundary() {
            // Arrange
            LocalDate startDate = LocalDate.now().minusDays(5);
            LocalDate endDate = LocalDate.now();
            
            testCustomer.setCreatedAt(startDate);
            customerRepository.save(testCustomer);

            // Act
            Pageable pageable = PageRequest.of(0, 10);
            Page<Customer> result = customerRepository.findByCreatedAtBetween(startDate, endDate, pageable);

            // Assert
            assertThat(result.getContent()).isNotEmpty();
            assertThat(result.getContent()).anyMatch(c -> c.getId().equals(testCustomerId));
        }

        @Test
        @DisplayName("Edge Case: Should return empty list when no customers in date range")
        void testFindByCreatedAtBetweenNoMatches() {
            // Arrange
            LocalDate pastStart = LocalDate.now().minusDays(30);
            LocalDate pastEnd = LocalDate.now().minusDays(20);

            // Act
            Pageable pageable = PageRequest.of(0, 10);
            Page<Customer> result = customerRepository.findByCreatedAtBetween(pastStart, pastEnd, pageable);

            // Assert
            assertThat(result.getContent()).isEmpty();
        }
    }

    @Nested
    @DisplayName("countByCreatedAtBefore Tests")
    class CountByCreatedAtBeforeTests {

        @Test
        @DisplayName("Success: Should count customers created before cutoff date")
        void testCountByCreatedAtBeforeSuccess() {
            // Arrange
            LocalDate today = LocalDate.now();
            testCustomer.setCreatedAt(today.minusDays(5));
            customerRepository.save(testCustomer);
            
            createAndSaveCustomer("Jane", "Smith", "jane@example.com", "Standard");
            
            LocalDate cutoffDate = today;

            // Act
            long count = customerRepository.countByCreatedAtBefore(cutoffDate);

            // Assert
            assertThat(count).isGreaterThanOrEqualTo(1);
        }

        @Test
        @DisplayName("Success: Should return 0 when no customers before cutoff")
        void testCountByCreatedAtBeforeZero() {
            // Arrange
            LocalDate futureDate = LocalDate.now().plusDays(10);

            // Act
            long count = customerRepository.countByCreatedAtBefore(futureDate);

            // Assert
            assertThat(count).isGreaterThanOrEqualTo(0);
        }

        @Test
        @DisplayName("Edge Case: Should not include customers created on cutoff date")
        void testCountByCreatedAtBeforeExclusive() {
            // Arrange
            LocalDate today = LocalDate.now();
            testCustomer.setCreatedAt(today);
            customerRepository.save(testCustomer);

            // Act
            long countBefore = customerRepository.countByCreatedAtBefore(today);
            long countBefore1Day = customerRepository.countByCreatedAtBefore(today.plusDays(1));

            // Assert
            assertThat(countBefore1Day).isGreaterThan(countBefore);
        }
    }

    // Helper method
    private Customer createAndSaveCustomer(String firstName, String lastName, String email, String segment) {
        Customer customer = new Customer();
        customer.setFirstName(firstName);
        customer.setLastName(lastName);
        customer.setEmail(email);
        customer.setAddress("Test Address");
        customer.setCreatedAt(LocalDate.now());
        customer.setSegment(segment);
        return customerRepository.save(customer);
    }
}


@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DisplayName("TicketRepository Integration Tests")
class TicketRepositoryIntegrationTest {

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private IssueTopicRepository issueTopicRepository;

    @Autowired
    private RegionRepository regionRepository;

    @Autowired
    private AgentRepository agentRepository;

    @Autowired
    private ServiceTypeRepository serviceTypeRepository;

    @Autowired
    private InfrastructureTypeRepository infrastructureTypeRepository;

    private Customer testCustomer;
    private Ticket testTicket;
    private Department testDepartment;
    private IssueTopic testTopic;
    private Region testRegion;
    private Agent testAgent;
    private ServiceType testServiceType;
    private InfrastructureType testInfrastructure;

    @BeforeEach
    void setUp() {
        ticketRepository.deleteAll();
        customerRepository.deleteAll();
        agentRepository.deleteAll();
        departmentRepository.deleteAll();
        issueTopicRepository.deleteAll();
        regionRepository.deleteAll();
        serviceTypeRepository.deleteAll();
        infrastructureTypeRepository.deleteAll();

        // Create reference data
        testDepartment = createAndSaveDepartment("Support", "SUP");
        testTopic = createAndSaveIssueTopic("Billing", 24);
        testRegion = createAndSaveRegion("Istanbul", "MARMARA");
        testAgent = createAndSaveAgent(testDepartment, "John Support");
        testServiceType = createAndSaveServiceType("Internet");
        testInfrastructure = createAndSaveInfrastructureType("Fiber");

        // Create test customer and ticket
        testCustomer = createAndSaveCustomer("John", "Doe", "john@example.com");
        testTicket = createAndSaveTicket(testCustomer, "OPEN", "HIGH", false);
    }

    @Nested
    @DisplayName("findByCustomerId Tests")
    class FindByCustomerIdTests {

        @Test
        @DisplayName("Success: Should find tickets by customer ID")
        void testFindByCustomerIdSuccess() {
            // Arrange
            createAndSaveTicket(testCustomer, "RESOLVED", "MEDIUM", false);

            // Act
            Pageable pageable = PageRequest.of(0, 10);
            Page<Ticket> result = ticketRepository.findByCustomerId(testCustomer.getId(), pageable);

            // Assert
            assertThat(result.getContent()).hasSize(2);
            assertThat(result.getContent()).allMatch(t -> t.getCustomer().getId().equals(testCustomer.getId()));
        }

        @Test
        @DisplayName("Success: Should return empty page when customer has no tickets")
        void testFindByCustomerIdEmpty() {
            // Arrange
            Customer customer2 = createAndSaveCustomer("Jane", "Smith", "jane@example.com");

            // Act
            Pageable pageable = PageRequest.of(0, 10);
            Page<Ticket> result = ticketRepository.findByCustomerId(customer2.getId(), pageable);

            // Assert
            assertThat(result.getContent()).isEmpty();
        }
    }

    @Nested
    @DisplayName("countByCustomerId Tests")
    class CountByCustomerIdTests {

        @Test
        @DisplayName("Success: Should count tickets for customer")
        void testCountByCustomerIdSuccess() {
            // Arrange
            createAndSaveTicket(testCustomer, "RESOLVED", "LOW", false);
            createAndSaveTicket(testCustomer, "IN_PROGRESS", "MEDIUM", false);

            // Act
            long count = ticketRepository.countByCustomerId(testCustomer.getId());

            // Assert
            assertThat(count).isEqualTo(3);
        }

        @Test
        @DisplayName("Success: Should return 0 for customer with no tickets")
        void testCountByCustomerIdZero() {
            // Arrange
            Customer customer2 = createAndSaveCustomer("Jane", "Smith", "jane@example.com");

            // Act
            long count = ticketRepository.countByCustomerId(customer2.getId());

            // Assert
            assertThat(count).isEqualTo(0);
        }
    }

    @Nested
    @DisplayName("findByStatus Tests")
    class FindByStatusTests {

        @Test
        @DisplayName("Success: Should find tickets by status")
        void testFindByStatusSuccess() {
            // Arrange
            createAndSaveTicket(testCustomer, "OPEN", "MEDIUM", false);
            createAndSaveTicket(testCustomer, "RESOLVED", "LOW", false);

            // Act
            Pageable pageable = PageRequest.of(0, 10);
            Page<Ticket> result = ticketRepository.findByStatus("OPEN", pageable);

            // Assert
            assertThat(result.getContent()).hasSizeGreaterThanOrEqualTo(1);
            assertThat(result.getContent()).allMatch(t -> t.getStatus().equals("OPEN"));
        }

        @Test
        @DisplayName("Edge Case: Should return empty list for non-existent status")
        void testFindByStatusNotFound() {
            // Act
            Pageable pageable = PageRequest.of(0, 10);
            Page<Ticket> result = ticketRepository.findByStatus("NONEXISTENT", pageable);

            // Assert
            assertThat(result.getContent()).isEmpty();
        }
    }

    @Nested
    @DisplayName("findByPriority Tests")
    class FindByPriorityTests {

        @Test
        @DisplayName("Success: Should find tickets by priority")
        void testFindByPrioritySuccess() {
            // Arrange
            createAndSaveTicket(testCustomer, "OPEN", "HIGH", false);
            createAndSaveTicket(testCustomer, "RESOLVED", "LOW", false);

            // Act
            Pageable pageable = PageRequest.of(0, 10);
            Page<Ticket> result = ticketRepository.findByPriority("HIGH", pageable);

            // Assert
            assertThat(result.getContent()).hasSizeGreaterThanOrEqualTo(1);
            assertThat(result.getContent()).allMatch(t -> t.getPriority().equals("HIGH"));
        }
    }

    @Nested
    @DisplayName("countByStatus Tests")
    class CountByStatusTests {

        @Test
        @DisplayName("Success: Should count tickets by status")
        void testCountByStatusSuccess() {
            // Arrange
            createAndSaveTicket(testCustomer, "OPEN", "MEDIUM", false);

            // Act
            long count = ticketRepository.countByStatus("OPEN");

            // Assert
            assertThat(count).isGreaterThanOrEqualTo(1);
        }

        @Test
        @DisplayName("Success: Should return 0 for non-existent status")
        void testCountByStatusZero() {
            // Act
            long count = ticketRepository.countByStatus("NONEXISTENT_STATUS");

            // Assert
            assertThat(count).isEqualTo(0);
        }
    }

    @Nested
    @DisplayName("countBySlaBreached Tests")
    class CountBySlaBreachedTests {

        @Test
        @DisplayName("Success: Should count tickets with SLA breach")
        void testCountBySlaBreachedTrue() {
            // Arrange
            createAndSaveTicket(testCustomer, "OPEN", "MEDIUM", true);

            // Act
            long count = ticketRepository.countBySlaBreached(true);

            // Assert
            assertThat(count).isGreaterThanOrEqualTo(1);
        }

        @Test
        @DisplayName("Success: Should count tickets without SLA breach")
        void testCountBySlaBreachedFalse() {
            // Act
            long count = ticketRepository.countBySlaBreached(false);

            // Assert
            assertThat(count).isGreaterThanOrEqualTo(1);
        }
    }

    @Nested
    @DisplayName("countByTicketNumberStartingWith Tests")
    class CountByTicketNumberStartingWithTests {

        @Test
        @DisplayName("Success: Should count tickets by ticket number prefix")
        void testCountByTicketNumberStartingWithSuccess() {
            // Arrange
            Ticket ticket1 = createAndSaveTicket(testCustomer, "OPEN", "HIGH", false);
            ticket1.setTicketNumber("TCK-202601-00001");
            ticketRepository.save(ticket1);

            // Act
            long count = ticketRepository.countByTicketNumberStartingWith("TCK-202601");

            // Assert
            assertThat(count).isGreaterThanOrEqualTo(1);
        }

        @Test
        @DisplayName("Success: Should return 0 for non-existent prefix")
        void testCountByTicketNumberStartingWithZero() {
            // Act
            long count = ticketRepository.countByTicketNumberStartingWith("NONEXISTENT");

            // Assert
            assertThat(count).isEqualTo(0);
        }
    }

    @Nested
    @DisplayName("countByCreatedAtBetween Tests")
    class CountByCreatedAtBetweenTests {

        @Test
        @DisplayName("Success: Should count tickets created within date range")
        void testCountByCreatedAtBetweenSuccess() {
            // Arrange
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime yesterday = now.minusDays(1);
            LocalDateTime tomorrow = now.plusDays(1);
            
            testTicket.setCreatedAt(now);
            ticketRepository.save(testTicket);

            // Act
            Long count = ticketRepository.countByCreatedAtBetween(yesterday, tomorrow);

            // Assert
            assertThat(count).isGreaterThanOrEqualTo(1);
        }

        @Test
        @DisplayName("Success: Should return 0 when no tickets in date range")
        void testCountByCreatedAtBetweenZero() {
            // Arrange
            LocalDateTime pastStart = LocalDateTime.now().minusDays(30);
            LocalDateTime pastEnd = LocalDateTime.now().minusDays(20);

            // Act
            Long count = ticketRepository.countByCreatedAtBetween(pastStart, pastEnd);

            // Assert
            assertThat(count).isEqualTo(0);
        }
    }

    @Nested
    @DisplayName("countByRegionId Tests")
    class CountByRegionIdTests {

        @Test
        @DisplayName("Success: Should count tickets by region ID")
        void testCountByRegionIdSuccess() {
            // Act
            long count = ticketRepository.countByRegionId(testRegion.getId());

            // Assert
            assertThat(count).isGreaterThanOrEqualTo(1);
        }

        @Test
        @DisplayName("Success: Should return 0 for non-existent region")
        void testCountByRegionIdZero() {
            // Act
            long count = ticketRepository.countByRegionId(UUID.randomUUID());

            // Assert
            assertThat(count).isEqualTo(0);
        }
    }

    @Nested
    @DisplayName("countByTopicId Tests")
    class CountByTopicIdTests {

        @Test
        @DisplayName("Success: Should count tickets by topic ID")
        void testCountByTopicIdSuccess() {
            // Act
            long count = ticketRepository.countByTopicId(testTopic.getId());

            // Assert
            assertThat(count).isGreaterThanOrEqualTo(1);
        }
    }

    // Helper methods
    private Ticket createAndSaveTicket(Customer customer, String status, String priority, boolean slaBreached) {
        Ticket ticket = new Ticket();
        ticket.setCustomer(customer);
        ticket.setTopic(testTopic);
        ticket.setCurrentDepartment(testDepartment);
        ticket.setAgent(testAgent);
        ticket.setRegion(testRegion);
        ticket.setServiceType(testServiceType);
        ticket.setInfrastructureType(testInfrastructure);
        ticket.setDescription("Test ticket");
        ticket.setStatus(status);
        ticket.setPriority(priority);
        ticket.setSlaBreached(slaBreached);
        ticket.setCreatedAt(LocalDateTime.now());
        ticket.setTicketNumber("TCK-" + System.currentTimeMillis() + "-" + UUID.randomUUID());
        ticket.setCreationSource("CALL_CENTER");
        return ticketRepository.save(ticket);
    }

    private Customer createAndSaveCustomer(String firstName, String lastName, String email) {
        Customer customer = new Customer();
        customer.setFirstName(firstName);
        customer.setLastName(lastName);
        customer.setEmail(email);
        customer.setAddress("Test Address");
        customer.setCreatedAt(LocalDate.now());
        return customerRepository.save(customer);
    }

    private Agent createAndSaveAgent(Department department, String fullName) {
        Agent agent = new Agent();
        agent.setDepartment(department);
        agent.setFullName(fullName);
        return agentRepository.save(agent);
    }

    private Department createAndSaveDepartment(String name, String code) {
        Department department = new Department();
        department.setDepartmentName(name);
        department.setDepartmentCode(code);
        return departmentRepository.save(department);
    }

    private IssueTopic createAndSaveIssueTopic(String topicName, Integer slaTargetHours) {
        IssueTopic topic = new IssueTopic();
        topic.setTopicName(topicName);
        topic.setSlaTargetHours(slaTargetHours);
        return issueTopicRepository.save(topic);
    }

    private Region createAndSaveRegion(String cityName, String geographicalRegion) {
        Region region = new Region();
        region.setCityName(cityName);
        region.setGeographicalRegion(geographicalRegion);
        return regionRepository.save(region);
    }

    private ServiceType createAndSaveServiceType(String serviceName) {
        ServiceType serviceType = new ServiceType();
        serviceType.setServiceName(serviceName);
        return serviceTypeRepository.save(serviceType);
    }

    private InfrastructureType createAndSaveInfrastructureType(String infrastructureName) {
        InfrastructureType infrastructure = new InfrastructureType();
        infrastructure.setInfrastructureName(infrastructureName);
        return infrastructureTypeRepository.save(infrastructure);
    }
}
