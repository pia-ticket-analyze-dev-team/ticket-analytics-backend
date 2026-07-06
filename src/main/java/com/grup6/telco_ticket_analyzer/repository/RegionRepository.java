package com.grup6.telco_ticket_analyzer.repository;

import com.grup6.telco_ticket_analyzer.entity.Region;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RegionRepository extends JpaRepository<Region, UUID> {

    Optional<Region> findByCityName(String cityName);

    List<Region> findByGeographicalRegion(String geographicalRegion);
}