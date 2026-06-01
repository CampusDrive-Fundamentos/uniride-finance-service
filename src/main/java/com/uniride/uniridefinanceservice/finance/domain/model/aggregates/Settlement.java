package com.uniride.uniridefinanceservice.finance.domain.model.aggregates;

import com.uniride.uniridefinanceservice.finance.domain.model.valueobjects.PaymentMethod;
import com.uniride.uniridefinanceservice.shared.domain.model.entities.AuditableModel;
import jakarta.persistence.*;

@Entity
@Table(name = "settlements")
public class Settlement extends AuditableModel {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true) // <-- Idempotencia: Evita duplicados en base de datos
  private Long tripId;

  @Column(nullable = false)
  private Long driverId;

  @Column(nullable = false)
  private Double totalAmount;

  @Column(nullable = false)
  private Double commissionPlatform;

  @Column(nullable = false)
  private Double driverEarnings;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private PaymentMethod paymentMethod;

  public Settlement() {}

  public Settlement(Long tripId, Long driverId, Double totalAmount, Double commissionPlatform, Double driverEarnings, PaymentMethod paymentMethod) {
    this.tripId = tripId;
    this.driverId = driverId;
    this.totalAmount = totalAmount;
    this.commissionPlatform = commissionPlatform;
    this.driverEarnings = driverEarnings;
    this.paymentMethod = paymentMethod;
  }

  // --- GETTERS EXPLÍCITOS ---
  public Long getId() { return id; }
  public Long getTripId() { return tripId; }
  public Long getDriverId() { return driverId; }
  public Double getTotalAmount() { return totalAmount; }
  public Double getCommissionPlatform() { return commissionPlatform; }
  public Double getDriverEarnings() { return driverEarnings; }
  public PaymentMethod getPaymentMethod() { return paymentMethod; }
}