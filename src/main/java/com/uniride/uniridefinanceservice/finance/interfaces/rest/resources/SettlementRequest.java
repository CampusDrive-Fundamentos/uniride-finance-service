package com.uniride.uniridefinanceservice.finance.interfaces.rest.resources;
public record SettlementRequest(Long tripId, Long driverId, Double totalAmount, String paymentMethod) {}