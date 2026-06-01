package com.uniride.uniridefinanceservice.finance.domain.model.commands;

public record WithdrawFundsCommand(Long driverId, Double amount) {
}