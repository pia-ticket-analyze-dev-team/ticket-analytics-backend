package com.grup6.telco_ticket_analyzer.controller;

import com.grup6.telco_ticket_analyzer.dto.CustomerRequestDto;
import com.grup6.telco_ticket_analyzer.dto.CustomerResponseDto;
import com.grup6.telco_ticket_analyzer.dto.PagedResponseDto;
import com.grup6.telco_ticket_analyzer.dto.TicketResponseDto;
import com.grup6.telco_ticket_analyzer.service.CustomerServiceInterface;
import com.grup6.telco_ticket_analyzer.service.TicketServiceInterface;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Müşteriler", description = "Müşteri kayıtlarını ve müşteriye bağlı ticket bilgilerini yöneten uçlar")
public class CustomerController {

    private final CustomerServiceInterface customerService;
    private final TicketServiceInterface ticketService;

    @Operation(summary = "Tüm müşterileri listele", description = "Sayfalanmış müşteri listesini döner; isme göre arama ve segmente göre filtreleme destekler.")
    @GetMapping
    public PagedResponseDto<CustomerResponseDto> getAllCustomers(
            @Parameter(description = "Sayfa numarası (0'dan başlar)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Sayfa başına kayıt sayısı") @RequestParam(defaultValue = "50") int size,
            @Parameter(description = "Müşteri adı/soyadına veya diğer alanlara göre arama metni") @RequestParam(required = false) String search,
            @Parameter(description = "Müşteri segmentine göre filtre (örn. Premium, Standart)") @RequestParam(required = false) String segment) {
        return customerService.getAllCustomers(page, size, search, segment);
    }

    @Operation(summary = "ID'ye göre müşteri getir", description = "Belirtilen UUID'ye sahip müşterinin detay bilgilerini döner.")
    @GetMapping("/{id}")
    public CustomerResponseDto getCustomerById(@PathVariable UUID id) {
        return customerService.getCustomerById(id);
    }

    @Operation(summary = "Yeni müşteri oluştur", description = "Verilen bilgilerle yeni bir müşteri kaydı oluşturur ve oluşturulan müşteriyi döner.")
    @PostMapping
    public ResponseEntity<CustomerResponseDto> createCustomer(@RequestBody CustomerRequestDto requestDto) {
        CustomerResponseDto created = customerService.createCustomer(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Operation(summary = "Müşteri bilgilerini güncelle", description = "Belirtilen ID'ye sahip müşterinin bilgilerini verilen değerlerle günceller.")
    @PutMapping("/{id}")
    public CustomerResponseDto updateCustomer(@PathVariable UUID id, @RequestBody CustomerRequestDto requestDto) {
        return customerService.updateCustomer(id, requestDto);
    }

    @Operation(summary = "Müşteriyi sil", description = "Belirtilen ID'ye sahip müşteri kaydını siler.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable UUID id) {
        customerService.deleteCustomer(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Müşterinin ticket'larını listele", description = "Belirtilen müşteriye ait ticket'ları sayfalanmış şekilde döner.")
    @GetMapping("/{id}/tickets")
    public PagedResponseDto<TicketResponseDto> getCustomerTickets(
            @PathVariable UUID id,
            @Parameter(description = "Sayfa numarası (0'dan başlar)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Sayfa başına kayıt sayısı") @RequestParam(defaultValue = "50") int size) {
        customerService.getCustomerById(id);
        return ticketService.getTicketsByCustomerId(id, page, size);
    }

    @Operation(summary = "Müşterinin toplam ticket sayısı", description = "Belirtilen müşterinin şimdiye kadar açtığı toplam ticket sayısını döner.")
    @GetMapping("/{id}/tickets/total-count")
    public long getTotalTicketCount(@PathVariable UUID id) {
        return customerService.getTotalTicketCount(id);
    }

    @Operation(summary = "Müşterinin açık ticket sayısı", description = "Belirtilen müşterinin halen açık (çözülmemiş) ticket sayısını döner.")
    @GetMapping("/{id}/tickets/open-count")
    public long getOpenTicketCount(@PathVariable UUID id) {
        return customerService.getOpenTicketCount(id);
    }

    @Operation(summary = "Müşterinin SLA ihlali sayısı", description = "Belirtilen müşteri için SLA (hizmet seviyesi anlaşması) süresi aşılmış ticket sayısını döner.")
    @GetMapping("/{id}/tickets/sla-breach-count")
    public long getSlaBreachCount(@PathVariable UUID id) {
        return customerService.getSlaBreachCount(id);
    }

    @Operation(summary = "Müşterinin ortalama memnuniyet skoru", description = "Belirtilen müşterinin ticket'larından hesaplanan ortalama memnuniyet skorunu döner.")
    @GetMapping("/{id}/tickets/avg-satisfaction")
    public double getAverageSatisfactionScore(@PathVariable UUID id) {
        return customerService.getAverageSatisfactionScore(id);
    }
}
