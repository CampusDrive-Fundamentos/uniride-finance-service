package com.uniride.uniridefinanceservice.finance.domain.model.aggregates;

import com.uniride.uniridefinanceservice.finance.domain.model.valueobjects.AccountStatus;
import com.uniride.uniridefinanceservice.shared.domain.model.entities.AuditableModel;
import jakarta.persistence.*;

@Entity
@Table(name = "driver_accounts")
public class DriverAccount extends AuditableModel {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true)
  private Long driverId;

  @Column(nullable = false)
  private Double currentDebt;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private AccountStatus accountStatus;

  public DriverAccount() {}

  public DriverAccount(Long driverId) {
    this.driverId = driverId;
    this.currentDebt = 0.0;
    this.accountStatus = AccountStatus.ACTIVE;
  }

  public void addDebt(Double amount) {
    this.currentDebt += amount;
    updateStatus();
  }

  public void payDebt(Double amount) {
    this.currentDebt -= amount;
    updateStatus();
  }

  // NUEVO MÉTODO: Retiro de fondos (Cash out)
  public void withdraw(Double amount) {
    if (this.currentDebt >= 0 || Math.abs(this.currentDebt) < amount) {
      throw new IllegalArgumentException("Fondos insuficientes para retirar.");
    }
    this.currentDebt += amount;
    updateStatus();
  }

  private void updateStatus() {
    if (this.currentDebt >= 10.00) {
      this.accountStatus = AccountStatus.BLOCKED;
    } else {
      this.accountStatus = AccountStatus.ACTIVE;
    }
  }

  // --- GETTERS EXPLÍCITOS ---
  public Long getId() { return id; }
  public Long getDriverId() { return driverId; }
  public Double getCurrentDebt() { return currentDebt; }
  public AccountStatus getAccountStatus() { return accountStatus; }
}