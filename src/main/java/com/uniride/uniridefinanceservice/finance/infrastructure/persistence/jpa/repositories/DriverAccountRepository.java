package com.uniride.uniridefinanceservice.finance.infrastructure.persistence.jpa.repositories;

import com.uniride.uniridefinanceservice.finance.domain.model.aggregates.DriverAccount;
import com.uniride.uniridefinanceservice.finance.domain.model.valueobjects.AccountStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DriverAccountRepository extends JpaRepository<DriverAccount, Long> {
  Optional<DriverAccount> findByDriverId(Long driverId);

  // Lista todos los conductores según su estado (ej. BLOCKED)
  List<DriverAccount> findByAccountStatus(AccountStatus status);
}