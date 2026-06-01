package com.uniride.uniridefinanceservice.finance.interfaces.rest.resources;
public record DriverAccountResource(Long driverId, Double currentDebt, String accountStatus) {}