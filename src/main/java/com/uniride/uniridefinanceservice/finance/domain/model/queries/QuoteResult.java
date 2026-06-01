package com.uniride.uniridefinanceservice.finance.domain.model.queries;
import java.util.List;
public record QuoteResult(Double totalTripPrice, Double platformCommission, List<PassengerQuote> breakdown) {
  public record PassengerQuote(Long passengerId, Double amountToPay) {}
}