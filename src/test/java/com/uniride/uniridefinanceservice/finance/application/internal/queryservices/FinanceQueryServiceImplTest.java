package com.uniride.uniridefinanceservice.finance.application.internal.queryservices;

import com.uniride.uniridefinanceservice.finance.domain.model.queries.CalculateQuoteQuery;
import com.uniride.uniridefinanceservice.finance.domain.model.queries.PassengerDistance;
import com.uniride.uniridefinanceservice.finance.domain.model.queries.QuoteResult;
import com.uniride.uniridefinanceservice.finance.infrastructure.persistence.jpa.repositories.DriverAccountRepository;
import com.uniride.uniridefinanceservice.finance.infrastructure.persistence.jpa.repositories.SettlementRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class FinanceQueryServiceImplTest {

  @Mock
  private DriverAccountRepository driverAccountRepository;

  @Mock
  private SettlementRepository settlementRepository;

  private FinanceQueryServiceImpl financeQueryService;

  @BeforeEach
  void setUp() {
    financeQueryService = new FinanceQueryServiceImpl(driverAccountRepository, settlementRepository);
  }

  @Test
  void calculateQuote_leaderAlone_matchesCampusDriveExample() {
    // Arrange
    var query = new CalculateQuoteQuery(
        20.0,
        List.of(new PassengerDistance(1L, 10.0))
    );

    // Act
    QuoteResult result = financeQueryService.handle(query);

    // Assert
    assertEquals(20.0, result.totalTripPrice());
    assertEquals(2.0, result.platformCommission()); // 10% de S/ 20 = S/ 2.00 (supera el mínimo de S/ 1.50)
    assertEquals(20.0, result.breakdown().getFirst().amountToPay());
  }

  @Test
  void calculateQuote_leaderPlusPassenger_appliesFairDistributionFormula() {
    // Arrange
    var query = new CalculateQuoteQuery(
        20.0,
        List.of(
            new PassengerDistance(101L, 4.0),
            new PassengerDistance(1L, 10.0)
        )
    );

    // Act
    QuoteResult result = financeQueryService.handle(query);

    // Assert
    assertEquals(22.0, result.totalTripPrice());
    assertEquals(2.20, result.platformCommission());
    assertEquals(6.29, result.breakdown().get(0).amountToPay());
    assertEquals(15.71, result.breakdown().get(1).amountToPay());
  }
}
