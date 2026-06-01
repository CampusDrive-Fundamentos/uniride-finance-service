package com.uniride.uniridefinanceservice.finance.interfaces.rest.resources;
import java.util.List;
public record QuoteResource(Double totalTripPrice, Double platformCommission, List<PassengerQuoteResource> breakdown) {
  public record PassengerQuoteResource(Long passengerId, Double amountToPay) {}
}