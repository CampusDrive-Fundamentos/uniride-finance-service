package com.uniride.uniridefinanceservice.finance.domain.services;

import com.uniride.uniridefinanceservice.finance.domain.model.aggregates.DriverAccount;
import com.uniride.uniridefinanceservice.finance.domain.model.aggregates.Settlement;
import com.uniride.uniridefinanceservice.finance.domain.model.commands.*;

public interface FinanceCommandService {
  Settlement handle(ProcessSettlementCommand command);
  DriverAccount handle(PayDebtCommand command);
  DriverAccount handle(WithdrawFundsCommand command); // NUEVO
}