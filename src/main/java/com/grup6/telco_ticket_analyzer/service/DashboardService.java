package com.grup6.telco_ticket_analyzer.service;

import com.grup6.telco_ticket_analyzer.dto.DailyTicketVolumeDto;
import com.grup6.telco_ticket_analyzer.dto.KpiMetricDto;
import com.grup6.telco_ticket_analyzer.dto.KpiSummaryDto;
import com.grup6.telco_ticket_analyzer.dto.NamedCountDto;
import com.grup6.telco_ticket_analyzer.dto.StatusCountDto;
import com.grup6.telco_ticket_analyzer.repository.CustomerRepository;
import com.grup6.telco_ticket_analyzer.repository.DepartmentRepository;
import com.grup6.telco_ticket_analyzer.repository.IssueTopicRepository;
import com.grup6.telco_ticket_analyzer.repository.RegionRepository;
import com.grup6.telco_ticket_analyzer.repository.TicketRepository;
import com.grup6.telco_ticket_analyzer.repository.projection.ResolutionTimeProjection;
import com.grup6.telco_ticket_analyzer.repository.projection.SatisfactionScoreProjection;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardService {

    private static final List<String> TICKET_STATUSES = List.of("OPEN", "IN_PROGRESS", "RESOLVED", "CLOSED");
    private static final int TOP_N = 5;

    private final TicketRepository ticketRepository;
    private final CustomerRepository customerRepository;
    private final RegionRepository regionRepository;
    private final IssueTopicRepository issueTopicRepository;
    private final DepartmentRepository departmentRepository;

    public KpiSummaryDto getKpiSummary() {
        LocalDate currentMonthStart = LocalDate.now().withDayOfMonth(1);
        LocalDateTime cutoff = currentMonthStart.atStartOfDay();

        KpiMetricDto totalCustomers = toKpiMetric(
                customerRepository.count(),
                customerRepository.countByCreatedAtBefore(currentMonthStart)
        );

        KpiMetricDto totalTickets = toKpiMetric(
                ticketRepository.count(),
                ticketRepository.countByCreatedAtBefore(cutoff)
        );

        KpiMetricDto openTickets = toKpiMetric(
                orZero(ticketRepository.countByStatus("OPEN")),
                ticketRepository.countByStatusAndCreatedAtBefore("OPEN", cutoff)
        );

        KpiMetricDto avgResolutionTimeHours = toKpiMetric(
                averageResolutionTimeHours(ticketRepository.findByResolutionTimeHoursIsNotNull()),
                averageResolutionTimeHours(ticketRepository.findByResolutionTimeHoursIsNotNullAndResolvedAtBefore(cutoff))
        );

        KpiMetricDto avgCustomerSatisfaction = toKpiMetric(
                averageSatisfactionScore(ticketRepository.findByCustomerSatisfactionScoreIsNotNull()),
                averageSatisfactionScore(ticketRepository.findByCustomerSatisfactionScoreIsNotNullAndResolvedAtBefore(cutoff))
        );

        return new KpiSummaryDto(totalCustomers, totalTickets, openTickets, avgResolutionTimeHours, avgCustomerSatisfaction);
    }

    public List<DailyTicketVolumeDto> getTicketVolumeLast30Days() {
        LocalDate today = LocalDate.now();
        return IntStream.rangeClosed(0, 29)
                .mapToObj(i -> today.minusDays(29 - i))
                .map(day -> new DailyTicketVolumeDto(
                        day,
                        orZero(ticketRepository.countByCreatedAtBetween(day.atStartOfDay(), day.plusDays(1).atStartOfDay()))
                ))
                .toList();
    }

    public List<StatusCountDto> getTicketsByStatus() {
        List<StatusCountDto> counts = TICKET_STATUSES.stream()
                .map(status -> new StatusCountDto(status, orZero(ticketRepository.countByStatus(status)), 0.0))
                .toList();

        long total = counts.stream().mapToLong(StatusCountDto::ticketCount).sum();

        return counts.stream()
                .map(c -> new StatusCountDto(c.status(), c.ticketCount(), percentageOf(c.ticketCount(), total)))
                .toList();
    }

    public List<NamedCountDto> getTicketsByRegion() {
        return regionRepository.findAll().stream()
                .map(region -> new NamedCountDto(region.getCityName(), orZero(ticketRepository.countByRegionId(region.getId()))))
                .sorted(Comparator.comparingLong(NamedCountDto::ticketCount).reversed())
                .limit(TOP_N)
                .toList();
    }

    public List<NamedCountDto> getTopIssueTopics() {
        return issueTopicRepository.findAll().stream()
                .map(topic -> new NamedCountDto(topic.getTopicName(), orZero(ticketRepository.countByTopicId(topic.getId()))))
                .sorted(Comparator.comparingLong(NamedCountDto::ticketCount).reversed())
                .limit(TOP_N)
                .toList();
    }

    public List<NamedCountDto> getDepartmentWorkload() {
        return departmentRepository.findAll().stream()
                .map(department -> new NamedCountDto(
                        department.getDepartmentName(),
                        orZero(ticketRepository.countByCurrentDepartmentId(department.getId()))
                ))
                .sorted(Comparator.comparingLong(NamedCountDto::ticketCount).reversed())
                .toList();
    }

    private static KpiMetricDto toKpiMetric(double current, double previous) {
        double changeAbsolute = current - previous;
        double changePercentage = previous == 0
                ? (current == 0 ? 0.0 : 100.0)
                : round2(100.0 * changeAbsolute / previous);
        return new KpiMetricDto(round2(current), round2(previous), round2(changeAbsolute), changePercentage);
    }

    private static double averageResolutionTimeHours(List<ResolutionTimeProjection> projections) {
        return projections.stream()
                .mapToDouble(p -> p.getResolutionTimeHours().doubleValue())
                .average()
                .orElse(0.0);
    }

    private static double averageSatisfactionScore(List<SatisfactionScoreProjection> projections) {
        return projections.stream()
                .mapToInt(SatisfactionScoreProjection::getCustomerSatisfactionScore)
                .average()
                .orElse(0.0);
    }

    private static double percentageOf(long value, long total) {
        return total == 0 ? 0.0 : round2(100.0 * value / total);
    }

    private static double round2(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

    private static long orZero(Long value) {
        return value != null ? value : 0L;
    }
}
