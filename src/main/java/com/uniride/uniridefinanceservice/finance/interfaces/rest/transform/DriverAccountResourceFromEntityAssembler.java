package com.uniride.uniridefinanceservice.finance.interfaces.rest.transform;

import com.uniride.uniridefinanceservice.finance.domain.model.aggregates.DriverAccount;
import com.uniride.uniridefinanceservice.finance.interfaces.rest.resources.DriverAccountResource;

public class DriverAccountResourceFromEntityAssembler {
  public static DriverAccountResource toResourceFromEntity(DriverAccount entity) {
    return new DriverAccountResource(
        entity.getDriverId(),
        entity.getCurrentDebt(),
        entity.getAccountStatus().name()
    );
  }
}