package com.uniride.uniridefinanceservice.finance.application.internal.commandservices;

import com.uniride.uniridefinanceservice.finance.domain.model.aggregates.DriverAccount;
import com.uniride.uniridefinanceservice.finance.domain.model.aggregates.Settlement;
import com.uniride.uniridefinanceservice.finance.domain.model.commands.PayDebtCommand;
import com.uniride.uniridefinanceservice.finance.domain.model.commands.ProcessSettlementCommand;
import com.uniride.uniridefinanceservice.finance.domain.model.commands.WithdrawFundsCommand;
import com.uniride.uniridefinanceservice.finance.domain.model.valueobjects.PaymentMethod;
import com.uniride.uniridefinanceservice.finance.domain.services.FinanceCommandService;
import com.uniride.uniridefinanceservice.finance.infrastructure.persistence.jpa.repositories.DriverAccountRepository;
import com.uniride.uniridefinanceservice.finance.infrastructure.persistence.jpa.repositories.SettlementRepository;
import org.springframework.stereotype.Service;

@Service
public class FinanceCommandServiceImpl implements FinanceCommandService {

  private final DriverAccountRepository driverAccountRepository;
  private final SettlementRepository settlementRepository;

  public FinanceCommandServiceImpl(DriverAccountRepository driverAccountRepository, SettlementRepository settlementRepository) {
    this.driverAccountRepository = driverAccountRepository;
    this.settlementRepository = settlementRepository;
  }

  @Override
  public Settlement handle(ProcessSettlementCommand command) {
    if (settlementRepository.existsByTripId(command.tripId())) {
      throw new IllegalArgumentException("El viaje con ID " + command.tripId() + " ya fue liquidado anteriormente.");
    }

    double commission = Math.max(command.totalAmount() * 0.10, 1.50);
    double earnings = command.totalAmount() - commission;

    DriverAccount account = driverAccountRepository.findByDriverId(command.driverId())
        .orElseGet(() -> new DriverAccount(command.driverId()));

    if (command.paymentMethod().equalsIgnoreCase("CASH")) {
      account.addDebt(commission);
    } else if (command.paymentMethod().equalsIgnoreCase("DIGITAL")) {
      account.payDebt(earnings);
    }

    driverAccountRepository.save(account);

    Settlement settlement = new Settlement(command.tripId(), command.driverId(), command.totalAmount(),
        commission, earnings, PaymentMethod.valueOf(command.paymentMethod().toUpperCase()));

    return settlementRepository.save(settlement);
  }

  @Override
  public DriverAccount handle(PayDebtCommand command) {
    DriverAccount account = driverAccountRepository.findByDriverId(command.driverId())
        .orElseThrow(() -> new RuntimeException("Cuenta de conductor no encontrada"));

    account.payDebt(command.amount());
    return driverAccountRepository.save(account);
  }

  @Override
  public DriverAccount handle(WithdrawFundsCommand command) {
    DriverAccount account = driverAccountRepository.findByDriverId(command.driverId())
        .orElseThrow(() -> new RuntimeException("Cuenta de conductor no encontrada"));

    account.withdraw(command.amount());
    return driverAccountRepository.save(account);
  }
}