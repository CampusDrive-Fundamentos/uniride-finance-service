package com.uniride.uniridefinanceservice.finance.domain.model.commands;
public record PayDebtCommand(Long driverId, Double amount, String transactionId) {}