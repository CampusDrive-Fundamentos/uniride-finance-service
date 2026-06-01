package com.uniride.uniridefinanceservice.finance.interfaces.rest.resources;
import java.util.List;
public record QuoteRequest(Double baseTaxiPrice, List<PassengerDistanceRequest> passengers) {}