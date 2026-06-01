package com.uniride.uniridefinanceservice.finance.application.internal.queryservices;

import com.uniride.uniridefinanceservice.finance.domain.model.aggregates.DriverAccount;
import com.uniride.uniridefinanceservice.finance.domain.model.aggregates.Settlement;
import com.uniride.uniridefinanceservice.finance.domain.model.queries.*;
import com.uniride.uniridefinanceservice.finance.domain.services.FinanceQueryService;
import com.uniride.uniridefinanceservice.finance.infrastructure.persistence.jpa.repositories.DriverAccountRepository;
import com.uniride.uniridefinanceservice.finance.infrastructure.persistence.jpa.repositories.SettlementRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class FinanceQueryServiceImpl implements FinanceQueryService {

  private final DriverAccountRepository driverAccountRepository;
  private final SettlementRepository settlementRepository;

  public FinanceQueryServiceImpl(DriverAccountRepository driverAccountRepository, SettlementRepository settlementRepository) {
    this.driverAccountRepository = driverAccountRepository;
    this.settlementRepository = settlementRepository;
  }

  @Override
  public Optional<DriverAccount> handle(GetDriverAccountByIdQuery query) {
    return driverAccountRepository.findByDriverId(query.driverId());
  }

  @Override
  public QuoteResult handle(CalculateQuoteQuery query) {
    int additionalPassengers = Math.max(0, query.passengers().size() - 1);
    double totalTripPrice = query.baseTaxiPrice() + (additionalPassengers * 2.0);

    double commission = Math.max(totalTripPrice * 0.10, 1.50);
    commission = Math.round(commission * 100.0) / 100.0;

    double totalDistance = query.passengers().stream().mapToDouble(PassengerDistance::distanceKm).sum();
    List<QuoteResult.PassengerQuote> breakdown = new ArrayList<>();

    for (PassengerDistance p : query.passengers()) {
      double amount = (p.distanceKm() / totalDistance) * totalTripPrice;
      amount = Math.round(amount * 100.0) / 100.0;
      breakdown.add(new QuoteResult.PassengerQuote(p.passengerId(), amount));
    }

    return new QuoteResult(totalTripPrice, commission, breakdown);
  }

  @Override
  public List<Settlement> handle(GetSettlementsByDriverIdQuery query) {
    return settlementRepository.findByDriverId(query.driverId());
  }

  @Override
  public Double handle(GetTotalPlatformRevenueQuery query) {
    Double total = settlementRepository.calculateTotalPlatformRevenue();
    return total != null ? Math.round(total * 100.0) / 100.0 : 0.0;
  }

  @Override
  public List<DriverAccount> handle(GetDebtorAccountsQuery query) {
    return driverAccountRepository.findByAccountStatus(
        com.uniride.uniridefinanceservice.finance.domain.model.valueobjects.AccountStatus.BLOCKED
    );
  }

  @Override
  public List<Settlement> handle(GetAllSettlementsQuery query) {
    return settlementRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
  }

  // NUEVO MÉTODO
  @Override
  public Optional<Settlement> handle(GetSettlementByIdQuery query) {
    return settlementRepository.findById(query.settlementId());
  }
}