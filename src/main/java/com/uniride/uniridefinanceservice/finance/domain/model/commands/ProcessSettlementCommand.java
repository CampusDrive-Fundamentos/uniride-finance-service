package com.uniride.uniridefinanceservice.finance.domain.model.commands;
public record ProcessSettlementCommand(Long tripId, Long driverId, Double totalAmount, String paymentMethod) {}