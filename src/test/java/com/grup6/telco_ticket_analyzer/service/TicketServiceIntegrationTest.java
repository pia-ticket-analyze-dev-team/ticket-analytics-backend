package com.grup6.telco_ticket_analyzer.service;

import com.grup6.telco_ticket_analyzer.dto.NewCustomerDto;
import com.grup6.telco_ticket_analyzer.dto.PagedResponseDto;
import com.grup6.telco_ticket_analyzer.dto.TicketCreateDto;
import com.grup6.telco_ticket_analyzer.dto.TicketResponseDto;
import com.grup6.telco_ticket_analyzer.exception.CustomerNotFoundException;
import com.grup6.telco_ticket_analyzer.exception.ReferenceDataNotFoundException;
import com.grup6.telco_ticket_analyzer.exception.TicketNotFoundException;
import com.grup6.telco_ticket_analyzer.model.Agent;
import com.grup6.telco_ticket_analyzer.model.Customer;
import com.grup6.telco_ticket_analyzer.model.Department;
import com.grup6.telco_ticket_analyzer.model.InfrastructureType;
import com.grup6.telco_ticket_analyzer.model.IssueTopic;
import com.grup6.telco_ticket_analyzer.model.Region;
import com.grup6.telco_ticket_analyzer.model.ServiceType;
import com.grup6.telco_ticket_analyzer.model.Ticket;
import com.grup6.telco_ticket_analyzer.repository.AgentRepository;
import com.grup6.telco_ticket_analyzer.repository.CustomerRepository;
import com.grup6.telco_ticket_analyzer.repository.DepartmentRepository;
import com.grup6.telco_ticket_analyzer.repository.InfrastructureTypeRepository;
import com.grup6.telco_ticket_analyzer.repository.IssueTopicRepository;
import com.grup6.telco_ticket_analyzer.repository.RegionRepository;
import com.grup6.telco_ticket_analyzer.repository.ServiceTypeRepository;
import com.grup6.telco_ticket_analyzer.repository.TicketRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DisplayName("TicketService Integration Tests")
class TicketServiceIntegrationTest {

    @Autowired
    private TicketService ticketService;

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private AgentRepository agentRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private IssueTopicRepository issueTopicRepository;

    @Autowired
    private RegionRepository regionRepository;

    @Autowired
    private ServiceTypeRepository serviceTypeRepository;

    @Autowired
    private InfrastructureTypeRepository infrastructureTypeRepository;

    private Customer testCustomer;
    private Agent testAgent;
    private Department testDepartment;
    private IssueTopic testTopic;
    private Region testRegion;
    private ServiceType testServiceType;
    private InfrastructureType testInfrastructure;
    private UUID testCustomerId;
    private UUID testAgentId;
    private UUID testDepartmentId;
    private UUID testTopicId;
    private UUID testRegionId;
    private UUID testServiceTypeId;
    private UUID testInfrastructureId;

    @BeforeEach
    void setUp() {
        ticketRepository.deleteAll();
        agentRepository.deleteAll();
        customerRepository.deleteAll();
        departmentRepository.deleteAll();
        issueTopicRepository.deleteAll();
        regionRepository.deleteAll();
        serviceTypeRepository.deleteAll();
        infrastructureTypeRepository.deleteAll();

        // Create reference data
        testDepartment = createAndSaveDepartment("Support", "SUP-01");
        testDepartmentId = testDepartment.getId();

        testTopic = createAndSaveIssueTopic("Billing Issue", 24);
        testTopicId = testTopic.getId();

        testRegion = createAndSaveRegion("Istanbul", "MARMARA");
        testRegionId = testRegion.getId();

        testAgent = createAndSaveAgent(testDepartment, "John Support");
        testAgentId = testAgent.getId();

        testServiceType = createAndSaveServiceType("Internet");
        testServiceTypeId = testServiceType.getId();

        testInfrastructure = createAndSaveInfrastructureType("Fiber");
        testInfrastructureId = testInfrastructure.getId();

        // Create test customer
        testCustomer = createAndSaveCustomer("Alice", "Johnson", "alice@test.com", "Premium");
        testCustomerId = testCustomer.getId();
    }

    @Nested
    @DisplayName("createTicket Tests")
    class CreateTicketTests {

        @Test
        @DisplayName("Success: Should create ticket with existing customer")
        void testCreateTicketWithExistingCustomerSuccess() {
            // Arrange
            TicketCreateDto requestDto = new TicketCreateDto(
                    testCustomerId,
                    null,
                    testTopicId,
                    testDepartmentId,
                    testAgentId,
                    testRegionId,
                    testServiceTypeId,
                    testInfrastructureId,
                    "Internet connection issue",
                    "OPEN",
                    "HIGH",
                    false,
                    null,
                    null,
                    LocalDateTime.now(),
                    null
            );

            // Act
            TicketResponseDto result = ticketService.createTicket(requestDto);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.id()).isNotNull();
            assertThat(result.ticketNumber()).startsWith("TCK-");
            assertThat(result.status()).isEqualTo("OPEN");
            assertThat(result.priority()).isEqualTo("HIGH");
            assertThat(result.description()).isEqualTo("Internet connection issue");
            
            // Verify persistence in DB
            Ticket saved = ticketRepository.findById(result.id()).orElse(null);
            assertThat(saved).isNotNull();
            assertThat(saved.getCustomer().getId()).isEqualTo(testCustomerId);
        }

        @Test
        @DisplayName("Success: Should create ticket with new customer")
        void testCreateTicketWithNewCustomerSuccess() {
            // Arrange
            NewCustomerDto newCustomerDto = new NewCustomerDto(
                    "Bob",
                    "Smith",
                    "bob.smith@test.com",
                    "789 Oak Ave",
                    LocalDate.of(1992, 6, 15),
                    "+1234567890",
                    "Standard"
            );

            TicketCreateDto requestDto = new TicketCreateDto(
                    null,
                    newCustomerDto,
                    testTopicId,
                    testDepartmentId,
                    testAgentId,
                    testRegionId,
                    testServiceTypeId,
                    testInfrastructureId,
                    "Mobile service issue",
                    "IN_PROGRESS",
                    "MEDIUM",
                    false,
                    BigDecimal.valueOf(2.5),
                    null,
                    LocalDateTime.now(),
                    null
            );

            // Act
            TicketResponseDto result = ticketService.createTicket(requestDto);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.ticketNumber()).startsWith("TCK-");
            
            // Verify new customer was created
            assertThat(customerRepository.findByEmail("bob.smith@test.com")).isPresent();
        }

        @Test
        @DisplayName("Success: Should generate unique ticket numbers")
        void testCreateTicketUniqueTicketNumbers() {
            // Arrange
            TicketCreateDto requestDto1 = createValidTicketDto(testCustomerId);
            TicketCreateDto requestDto2 = createValidTicketDto(testCustomerId);

            // Act
            TicketResponseDto ticket1 = ticketService.createTicket(requestDto1);
            TicketResponseDto ticket2 = ticketService.createTicket(requestDto2);

            // Assert
            assertThat(ticket1.ticketNumber()).isNotEqualTo(ticket2.ticketNumber());
        }

        @Test
        @DisplayName("Success: Should set creation source to CALL_CENTER")
        void testCreateTicketCreationSource() {
            // Arrange
            TicketCreateDto requestDto = createValidTicketDto(testCustomerId);

            // Act
            TicketResponseDto result = ticketService.createTicket(requestDto);

            // Assert
            Ticket saved = ticketRepository.findById(result.id()).orElse(null);
            assertThat(saved).isNotNull();
            assertThat(saved.getCreationSource()).isEqualTo("CALL_CENTER");
        }

        @Test
        @DisplayName("Failure: Should throw exception when customer not found")
        void testCreateTicketCustomerNotFound() {
            // Arrange
            UUID nonExistentCustomerId = UUID.randomUUID();
            TicketCreateDto requestDto = new TicketCreateDto(
                    nonExistentCustomerId,
                    null,
                    testTopicId,
                    testDepartmentId,
                    testAgentId,
                    testRegionId,
                    testServiceTypeId,
                    testInfrastructureId,
                    "Test",
                    "OPEN",
                    "HIGH",
                    false,
                    null,
                    null,
                    LocalDateTime.now(),
                    null
            );

            // Act & Assert
            assertThatThrownBy(() -> ticketService.createTicket(requestDto))
                    .isInstanceOf(CustomerNotFoundException.class);
        }

        @Test
        @DisplayName("Failure: Should throw exception when topic not found")
        void testCreateTicketTopicNotFound() {
            // Arrange
            UUID nonExistentTopicId = UUID.randomUUID();
            TicketCreateDto requestDto = new TicketCreateDto(
                    testCustomerId,
                    null,
                    nonExistentTopicId,
                    testDepartmentId,
                    testAgentId,
                    testRegionId,
                    testServiceTypeId,
                    testInfrastructureId,
                    "Test",
                    "OPEN",
                    "HIGH",
                    false,
                    null,
                    null,
                    LocalDateTime.now(),
                    null
            );

            // Act & Assert
            assertThatThrownBy(() -> ticketService.createTicket(requestDto))
                    .isInstanceOf(ReferenceDataNotFoundException.class)
                    .hasMessageContaining("Issue topic");
        }

        @Test
        @DisplayName("Failure: Should throw exception when department not found")
        void testCreateTicketDepartmentNotFound() {
            // Arrange
            UUID nonExistentDepartmentId = UUID.randomUUID();
            TicketCreateDto requestDto = new TicketCreateDto(
                    testCustomerId,
                    null,
                    testTopicId,
                    nonExistentDepartmentId,
                    testAgentId,
                    testRegionId,
                    testServiceTypeId,
                    testInfrastructureId,
                    "Test",
                    "OPEN",
                    "HIGH",
                    false,
                    null,
                    null,
                    LocalDateTime.now(),
                    null
            );

            // Act & Assert
            assertThatThrownBy(() -> ticketService.createTicket(requestDto))
                    .isInstanceOf(ReferenceDataNotFoundException.class)
                    .hasMessageContaining("Department");
        }

        @Test
        @DisplayName("Failure: Should throw exception when agent not found")
        void testCreateTicketAgentNotFound() {
            // Arrange
            UUID nonExistentAgentId = UUID.randomUUID();
            TicketCreateDto requestDto = new TicketCreateDto(
                    testCustomerId,
                    null,
                    testTopicId,
                    testDepartmentId,
                    nonExistentAgentId,
                    testRegionId,
                    testServiceTypeId,
                    testInfrastructureId,
                    "Test",
                    "OPEN",
                    "HIGH",
                    false,
                    null,
                    null,
                    LocalDateTime.now(),
                    null
            );

            // Act & Assert
            assertThatThrownBy(() -> ticketService.createTicket(requestDto))
                    .isInstanceOf(ReferenceDataNotFoundException.class)
                    .hasMessageContaining("Agent");
        }

        @Test
        @DisplayName("Edge Case: Should handle ticket with SLA breach flag")
        void testCreateTicketWithSlaBreached() {
            // Arrange
            TicketCreateDto requestDto = new TicketCreateDto(
                    testCustomerId,
                    null,
                    testTopicId,
                    testDepartmentId,
                    testAgentId,
                    testRegionId,
                    testServiceTypeId,
                    testInfrastructureId,
                    "Urgent issue",
                    "RESOLVED",
                    "CRITICAL",
                    true,
                    BigDecimal.valueOf(48.5),
                    9,
                    LocalDateTime.now().minusHours(50),
                    LocalDateTime.now()
            );

            // Act
            TicketResponseDto result = ticketService.createTicket(requestDto);

            // Assert
            assertThat(result).isNotNull();
            Ticket saved = ticketRepository.findById(result.id()).orElse(null);
            assertThat(saved).isNotNull();
            assertThat(saved.isSlaBreached()).isTrue();
            assertThat(saved.getResolutionTimeHours()).isEqualTo(BigDecimal.valueOf(48.5));
            assertThat(saved.getCustomerSatisfactionScore()).isEqualTo(9);
        }

        @Test
        @DisplayName("Edge Case: Should use current datetime when createdAt is null")
        void testCreateTicketDefaultCreatedAt() {
            // Arrange
            TicketCreateDto requestDto = new TicketCreateDto(
                    testCustomerId,
                    null,
                    testTopicId,
                    testDepartmentId,
                    testAgentId,
                    testRegionId,
                    testServiceTypeId,
                    testInfrastructureId,
                    "Test",
                    "OPEN",
                    "HIGH",
                    false,
                    null,
                    null,
                    null,
                    null
            );

            // Act
            LocalDateTime beforeCreation = LocalDateTime.now();
            TicketResponseDto result = ticketService.createTicket(requestDto);
            LocalDateTime afterCreation = LocalDateTime.now();

            // Assert
            Ticket saved = ticketRepository.findById(result.id()).orElse(null);
            assertThat(saved).isNotNull();
            assertThat(saved.getCreatedAt()).isBetween(beforeCreation, afterCreation);
        }

        @Test
        @DisplayName("Failure: Should throw exception when both customerId and newCustomer provided")
        void testCreateTicketBothCustomerFieldsProvided() {
            // Arrange
            NewCustomerDto newCustomerDto = new NewCustomerDto(
                    "Test", "Test", "test@test.com", "Test", null, null, null
            );
                // Act & Assert - construction should throw because both customerId and newCustomer provided
                assertThatThrownBy(() -> new TicketCreateDto(
                    testCustomerId,
                    newCustomerDto,
                    testTopicId,
                    testDepartmentId,
                    testAgentId,
                    testRegionId,
                    testServiceTypeId,
                    testInfrastructureId,
                    "Test",
                    "OPEN",
                    "HIGH",
                    false,
                    null,
                    null,
                    LocalDateTime.now(),
                    null
                ))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Exactly one");
        }
    }

    @Nested
    @DisplayName("getTicketById Tests")
    class GetTicketByIdTests {

        @Test
        @DisplayName("Success: Should retrieve ticket by valid ID")
        void testGetTicketByIdSuccess() {
            // Arrange
            Ticket ticket = createAndSaveTicket(testCustomer, testTopic, testDepartment, testAgent, testRegion, testServiceType, testInfrastructure);
            UUID ticketId = ticket.getId();

            // Act
            TicketResponseDto result = ticketService.getTicketById(ticketId);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.id()).isEqualTo(ticketId);
            assertThat(result.status()).isEqualTo("OPEN");
        }

        @Test
        @DisplayName("Failure: Should throw TicketNotFoundException for non-existent ID")
        void testGetTicketByIdNotFound() {
            // Arrange
            UUID nonExistentId = UUID.randomUUID();

            // Act & Assert
            assertThatThrownBy(() -> ticketService.getTicketById(nonExistentId))
                    .isInstanceOf(TicketNotFoundException.class);
        }

        @Test
        @DisplayName("Edge Case: Should retrieve ticket with all fields populated")
        void testGetTicketByIdAllFields() {
            // Arrange
            LocalDateTime now = LocalDateTime.now();
            Ticket ticket = new Ticket();
            ticket.setCustomer(testCustomer);
            ticket.setTopic(testTopic);
            ticket.setCurrentDepartment(testDepartment);
            ticket.setAgent(testAgent);
            ticket.setRegion(testRegion);
            ticket.setServiceType(testServiceType);
            ticket.setInfrastructureType(testInfrastructure);
            ticket.setDescription("Complete ticket");
            ticket.setStatus("RESOLVED");
            ticket.setPriority("HIGH");
            ticket.setSlaBreached(true);
            ticket.setResolutionTimeHours(BigDecimal.valueOf(5.5));
            ticket.setCustomerSatisfactionScore(8);
            ticket.setCreatedAt(now.minusHours(24));
            ticket.setResolvedAt(now);
            ticket.setCreationSource("CALL_CENTER");
            ticket.setTicketNumber("TCK-202601-00001");
            Ticket saved = ticketRepository.save(ticket);

            // Act
            TicketResponseDto result = ticketService.getTicketById(saved.getId());

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.status()).isEqualTo("RESOLVED");
            assertThat(result.priority()).isEqualTo("HIGH");
        }
    }

    @Nested
    @DisplayName("getTicketsByCustomerId Tests")
    class GetTicketsByCustomerIdTests {

        @Test
        @DisplayName("Success: Should retrieve empty list when customer has no tickets")
        void testGetTicketsByCustomerIdEmpty() {
            // Act
            PagedResponseDto<TicketResponseDto> result = ticketService.getTicketsByCustomerId(testCustomerId, 0, 10);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.content()).isEmpty();
            assertThat(result.totalElements()).isEqualTo(0);
        }

        @Test
        @DisplayName("Success: Should retrieve tickets for customer with pagination")
        void testGetTicketsByCustomerIdWithTickets() {
            // Arrange
            createAndSaveTicket(testCustomer, testTopic, testDepartment, testAgent, testRegion, testServiceType, testInfrastructure);
            createAndSaveTicket(testCustomer, testTopic, testDepartment, testAgent, testRegion, testServiceType, testInfrastructure);

            // Act
            PagedResponseDto<TicketResponseDto> result = ticketService.getTicketsByCustomerId(testCustomerId, 0, 10);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.content()).hasSize(2);
            assertThat(result.totalElements()).isEqualTo(2);
            assertThat(result.page()).isEqualTo(0);
        }

        @Test
        @DisplayName("Edge Case: Should apply page size limit")
        void testGetTicketsByCustomerIdPageSizeLimit() {
            // Arrange
            for (int i = 0; i < 3; i++) {
                createAndSaveTicket(testCustomer, testTopic, testDepartment, testAgent, testRegion, testServiceType, testInfrastructure);
            }

            // Act
            PagedResponseDto<TicketResponseDto> result = ticketService.getTicketsByCustomerId(testCustomerId, 0, 1000);

            // Assert
            assertThat(result.size()).isEqualTo(100);
        }

        @Test
        @DisplayName("Edge Case: Should default to page size 50 when size < 1")
        void testGetTicketsByCustomerIdDefaultPageSize() {
            // Act
            PagedResponseDto<TicketResponseDto> result = ticketService.getTicketsByCustomerId(testCustomerId, 0, 0);

            // Assert
            assertThat(result.size()).isEqualTo(50);
        }
    }

    @Nested
    @DisplayName("getAllTickets Tests")
    class GetAllTicketsTests {

        @Test
        @DisplayName("Success: Should retrieve all tickets without filters")
        void testGetAllTicketsSuccess() {
            // Arrange
            createAndSaveTicket(testCustomer, testTopic, testDepartment, testAgent, testRegion, testServiceType, testInfrastructure);
            Customer customer2 = createAndSaveCustomer("Bob", "Smith", "bob@test.com", "Standard");
            createAndSaveTicket(customer2, testTopic, testDepartment, testAgent, testRegion, testServiceType, testInfrastructure);

            // Act
            PagedResponseDto<TicketResponseDto> result = ticketService.getAllTickets(
                    0, 10, null, null, null, null, null, null, null, null, null
            );

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.content()).hasSize(2);
        }

        @Test
        @DisplayName("Success: Should filter tickets by status")
        void testGetAllTicketsFilterByStatus() {
            // Arrange
            Ticket ticket1 = createAndSaveTicket(testCustomer, testTopic, testDepartment, testAgent, testRegion, testServiceType, testInfrastructure);
            ticket1.setStatus("OPEN");
            ticketRepository.save(ticket1);

            Ticket ticket2 = createAndSaveTicket(testCustomer, testTopic, testDepartment, testAgent, testRegion, testServiceType, testInfrastructure);
            ticket2.setStatus("RESOLVED");
            ticketRepository.save(ticket2);

            // Act
            PagedResponseDto<TicketResponseDto> result = ticketService.getAllTickets(
                    0, 10, "OPEN", null, null, null, null, null, null, null, null
            );

            // Assert
            assertThat(result.content()).hasSize(1);
            assertThat(result.content().get(0).status()).isEqualTo("OPEN");
        }

        @Test
        @DisplayName("Edge Case: Should return empty list when no tickets match filter")
        void testGetAllTicketsNoMatchingFilter() {
            // Arrange
            createAndSaveTicket(testCustomer, testTopic, testDepartment, testAgent, testRegion, testServiceType, testInfrastructure);

            // Act
            PagedResponseDto<TicketResponseDto> result = ticketService.getAllTickets(
                    0, 10, "NONEXISTENT_STATUS", null, null, null, null, null, null, null, null
            );

            // Assert
            assertThat(result.content()).isEmpty();
        }
    }

    // Helper Methods
    private TicketCreateDto createValidTicketDto(UUID customerId) {
        return new TicketCreateDto(
                customerId,
                null,
                testTopicId,
                testDepartmentId,
                testAgentId,
                testRegionId,
                testServiceTypeId,
                testInfrastructureId,
                "Test description",
                "OPEN",
                "MEDIUM",
                false,
                null,
                null,
                LocalDateTime.now(),
                null
        );
    }

    private Ticket createAndSaveTicket(Customer customer, IssueTopic topic, Department department, Agent agent, Region region, ServiceType serviceType, InfrastructureType infrastructure) {
        Ticket ticket = new Ticket();
        ticket.setCustomer(customer);
        ticket.setTopic(topic);
        ticket.setCurrentDepartment(department);
        ticket.setAgent(agent);
        ticket.setRegion(region);
        ticket.setServiceType(serviceType);
        ticket.setInfrastructureType(infrastructure);
        ticket.setDescription("Test ticket");
        ticket.setStatus("OPEN");
        ticket.setPriority("MEDIUM");
        ticket.setSlaBreached(false);
        ticket.setCreatedAt(LocalDateTime.now());
        ticket.setTicketNumber("TCK-" + UUID.randomUUID());
        ticket.setCreationSource("CALL_CENTER");
        return ticketRepository.save(ticket);
    }

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
