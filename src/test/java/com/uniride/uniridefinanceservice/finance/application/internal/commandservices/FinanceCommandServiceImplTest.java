package com.uniride.uniridefinanceservice.finance.application.internal.commandservices;

import com.uniride.uniridefinanceservice.finance.domain.model.aggregates.DriverAccount;
import com.uniride.uniridefinanceservice.finance.domain.model.aggregates.Settlement;
import com.uniride.uniridefinanceservice.finance.domain.model.commands.ProcessSettlementCommand;
import com.uniride.uniridefinanceservice.finance.domain.model.valueobjects.AccountStatus;
import com.uniride.uniridefinanceservice.finance.domain.model.valueobjects.PaymentMethod;
import com.uniride.uniridefinanceservice.finance.infrastructure.persistence.jpa.repositories.DriverAccountRepository;
import com.uniride.uniridefinanceservice.finance.infrastructure.persistence.jpa.repositories.SettlementRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FinanceCommandServiceImplTest {

  @Mock
  private DriverAccountRepository driverAccountRepository;

  @Mock
  private SettlementRepository settlementRepository;

  private FinanceCommandServiceImpl financeCommandService;

  @BeforeEach
  void setUp() {
    financeCommandService = new FinanceCommandServiceImpl(driverAccountRepository, settlementRepository);
  }

  @Test
  void processSettlement_withCashPayment_generatesCommissionDebt() {
    // pago en efectivo → la comisión se registra como deuda del taxista
    var command = new ProcessSettlementCommand(100L, 1L, 20.0, "CASH");

    when(settlementRepository.existsByTripId(100L)).thenReturn(false);
    when(driverAccountRepository.findByDriverId(1L)).thenReturn(Optional.empty());
    when(driverAccountRepository.save(any(DriverAccount.class))).thenAnswer(invocation -> invocation.getArgument(0));
    when(settlementRepository.save(any(Settlement.class))).thenAnswer(invocation -> invocation.getArgument(0));

    Settlement result = financeCommandService.handle(command);

    ArgumentCaptor<DriverAccount> accountCaptor = ArgumentCaptor.forClass(DriverAccount.class);
    org.mockito.Mockito.verify(driverAccountRepository).save(accountCaptor.capture());

    assertEquals(2.0, accountCaptor.getValue().getCurrentDebt());
    assertEquals(AccountStatus.ACTIVE, accountCaptor.getValue().getAccountStatus());
    assertEquals(PaymentMethod.CASH, result.getPaymentMethod());
  }

  @Test
  void processSettlement_whenDebtReachesTenSoles_blocksDriverAccount() {
    // al llegar a S/ 10.00 de deuda, el taxista queda bloqueado
    var command = new ProcessSettlementCommand(101L, 1L, 20.0, "CASH");
    DriverAccount account = new DriverAccount(1L);
    account.addDebt(9.0);

    when(settlementRepository.existsByTripId(101L)).thenReturn(false);
    when(driverAccountRepository.findByDriverId(1L)).thenReturn(Optional.of(account));
    when(driverAccountRepository.save(any(DriverAccount.class))).thenAnswer(invocation -> invocation.getArgument(0));
    when(settlementRepository.save(any(Settlement.class))).thenAnswer(invocation -> invocation.getArgument(0));

    financeCommandService.handle(command);

    ArgumentCaptor<DriverAccount> accountCaptor = ArgumentCaptor.forClass(DriverAccount.class);
    org.mockito.Mockito.verify(driverAccountRepository).save(accountCaptor.capture());

    assertEquals(11.0, accountCaptor.getValue().getCurrentDebt());
    assertEquals(AccountStatus.BLOCKED, accountCaptor.getValue().getAccountStatus());
  }
}
