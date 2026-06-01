package com.uniride.uniridefinanceservice.finance.infrastructure.persistence.jpa.repositories;

import com.uniride.uniridefinanceservice.finance.domain.model.aggregates.Settlement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SettlementRepository extends JpaRepository<Settlement, Long> {

  // Historial del taxista
  List<Settlement> findByDriverId(Long driverId);

  // Protección contra doble cobro (Idempotencia)
  boolean existsByTripId(Long tripId);

  // Ganancias totales de la plataforma (Admin)
  @Query("SELECT SUM(s.commissionPlatform) FROM Settlement s")
  Double calculateTotalPlatformRevenue();
}