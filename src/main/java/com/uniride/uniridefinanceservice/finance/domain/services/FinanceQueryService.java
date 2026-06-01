package com.uniride.uniridefinanceservice.finance.domain.services;

import com.uniride.uniridefinanceservice.finance.domain.model.aggregates.DriverAccount;
import com.uniride.uniridefinanceservice.finance.domain.model.aggregates.Settlement;
import com.uniride.uniridefinanceservice.finance.domain.model.queries.*;

import java.util.List;
import java.util.Optional;

public interface FinanceQueryService {
  QuoteResult handle(CalculateQuoteQuery query);
  Optional<DriverAccount> handle(GetDriverAccountByIdQuery query);
  List<Settlement> handle(GetSettlementsByDriverIdQuery query);
  Double handle(GetTotalPlatformRevenueQuery query);
  List<DriverAccount> handle(GetDebtorAccountsQuery query);
  List<Settlement> handle(GetAllSettlementsQuery query);
  Optional<Settlement> handle(GetSettlementByIdQuery query); // NUEVO
}