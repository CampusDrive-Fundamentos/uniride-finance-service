package com.uniride.uniridefinanceservice.finance.interfaces.rest.transform;

import com.uniride.uniridefinanceservice.finance.domain.model.aggregates.Settlement;
import com.uniride.uniridefinanceservice.finance.interfaces.rest.resources.SettlementResource;

public class SettlementResourceFromEntityAssembler {
  public static SettlementResource toResourceFromEntity(Settlement entity) {
    return new SettlementResource(
        entity.getId(),
        entity.getTripId(),
        entity.getDriverId(),
        entity.getTotalAmount(),
        entity.getCommissionPlatform(),
        entity.getDriverEarnings(),
        entity.getPaymentMethod().name()
    );
  }
}