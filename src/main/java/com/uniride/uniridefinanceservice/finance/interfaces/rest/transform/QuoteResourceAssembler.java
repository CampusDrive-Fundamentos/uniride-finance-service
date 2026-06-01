package com.uniride.uniridefinanceservice.finance.interfaces.rest.transform;

import com.uniride.uniridefinanceservice.finance.domain.model.queries.QuoteResult;
import com.uniride.uniridefinanceservice.finance.interfaces.rest.resources.QuoteResource;

import java.util.stream.Collectors;

public class QuoteResourceAssembler {
  public static QuoteResource toResourceFromEntity(QuoteResult result) {
    var breakdown = result.breakdown().stream()
        .map(p -> new QuoteResource.PassengerQuoteResource(p.passengerId(), p.amountToPay()))
        .collect(Collectors.toList());

    return new QuoteResource(result.totalTripPrice(), result.platformCommission(), breakdown);
  }
}