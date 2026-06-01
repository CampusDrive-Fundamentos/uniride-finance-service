package com.uniride.uniridefinanceservice.finance.domain.model.queries;
import java.util.List;
public record CalculateQuoteQuery(Double baseTaxiPrice, List<PassengerDistance> passengers) {}