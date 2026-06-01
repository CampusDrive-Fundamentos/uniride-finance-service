package com.uniride.uniridefinanceservice.finance.interfaces.rest;

import com.uniride.uniridefinanceservice.finance.domain.model.aggregates.DriverAccount;
import com.uniride.uniridefinanceservice.finance.domain.model.aggregates.Settlement;
import com.uniride.uniridefinanceservice.finance.domain.model.commands.PayDebtCommand;
import com.uniride.uniridefinanceservice.finance.domain.model.commands.ProcessSettlementCommand;
import com.uniride.uniridefinanceservice.finance.domain.model.commands.WithdrawFundsCommand;
import com.uniride.uniridefinanceservice.finance.domain.model.queries.*;
import com.uniride.uniridefinanceservice.finance.domain.services.FinanceCommandService;
import com.uniride.uniridefinanceservice.finance.domain.services.FinanceQueryService;
import com.uniride.uniridefinanceservice.finance.interfaces.rest.resources.*;
import com.uniride.uniridefinanceservice.finance.interfaces.rest.transform.DriverAccountResourceFromEntityAssembler;
import com.uniride.uniridefinanceservice.finance.interfaces.rest.transform.QuoteResourceAssembler;
import com.uniride.uniridefinanceservice.finance.interfaces.rest.transform.SettlementResourceFromEntityAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/finance")
@Tag(name = "Finance", description = "Quotes, Settlements and Debt Management")
public class FinanceController {

  private final FinanceCommandService financeCommandService;
  private final FinanceQueryService financeQueryService;

  public FinanceController(FinanceCommandService financeCommandService, FinanceQueryService financeQueryService) {
    this.financeCommandService = financeCommandService;
    this.financeQueryService = financeQueryService;
  }

  @PostMapping("/quotes")
  @Operation(summary = "Genera una cotización usando la Fórmula de Repartición Justa (No guarda en BD)")
  public ResponseEntity<QuoteResource> calculateQuote(@RequestBody QuoteRequest request) {
    List<PassengerDistance> distances = request.passengers().stream()
        .map(p -> new PassengerDistance(p.passengerId(), p.distanceKm()))
        .collect(Collectors.toList());

    QuoteResult result = financeQueryService.handle(new CalculateQuoteQuery(request.baseTaxiPrice(), distances));

    return ResponseEntity.ok(QuoteResourceAssembler.toResourceFromEntity(result));
  }

  @PostMapping("/settlements")
  @Operation(summary = "Liquida un viaje finalizado, calcula comisiones y actualiza la deuda del taxista")
  public ResponseEntity<SettlementResource> processSettlement(@RequestBody SettlementRequest request) {
    Settlement settlement = financeCommandService.handle(
        new ProcessSettlementCommand(request.tripId(), request.driverId(), request.totalAmount(), request.paymentMethod())
    );

    return ResponseEntity.ok(SettlementResourceFromEntityAssembler.toResourceFromEntity(settlement));
  }

  @GetMapping("/drivers/{driverId}/account")
  @Operation(summary = "Obtiene el balance financiero de un conductor y su estado (ACTIVE / BLOCKED)")
  public ResponseEntity<DriverAccountResource> getDriverAccount(@PathVariable Long driverId) {
    return financeQueryService.handle(new GetDriverAccountByIdQuery(driverId))
        .map(account -> ResponseEntity.ok(DriverAccountResourceFromEntityAssembler.toResourceFromEntity(account)))
        .orElse(ResponseEntity.ok(new DriverAccountResource(driverId, 0.0, "ACTIVE")));
  }

  @PostMapping("/drivers/{driverId}/pay-debt")
  @Operation(summary = "El taxista paga su deuda. Si baja de S/ 10, su estado cambia a ACTIVE automáticamente")
  public ResponseEntity<DriverAccountResource> payDebt(@PathVariable Long driverId, @RequestBody PayDebtRequest request) {
    DriverAccount account = financeCommandService.handle(new PayDebtCommand(driverId, request.amount(), request.transactionId()));

    return ResponseEntity.ok(DriverAccountResourceFromEntityAssembler.toResourceFromEntity(account));
  }

  @GetMapping("/drivers/{driverId}/settlements")
  @Operation(summary = "Obtiene el historial de viajes y ganancias de un taxista específico")
  public ResponseEntity<List<SettlementResource>> getDriverSettlements(@PathVariable Long driverId) {
    var query = new GetSettlementsByDriverIdQuery(driverId);
    var settlements = financeQueryService.handle(query).stream()
        .map(SettlementResourceFromEntityAssembler::toResourceFromEntity)
        .collect(Collectors.toList());

    return ResponseEntity.ok(settlements);
  }

  @PostMapping("/drivers/{driverId}/withdraw")
  @Operation(summary = "El taxista retira su saldo a favor a su cuenta bancaria (Cash Out)")
  public ResponseEntity<DriverAccountResource> withdrawFunds(@PathVariable Long driverId, @RequestParam Double amount) {
    DriverAccount account = financeCommandService.handle(new WithdrawFundsCommand(driverId, amount));
    return ResponseEntity.ok(DriverAccountResourceFromEntityAssembler.toResourceFromEntity(account));
  }

  @GetMapping("/admin/revenue")
  @Operation(summary = "Obtiene las ganancias totales históricas de la plataforma CampusDrive")
  public ResponseEntity<Double> getTotalRevenue() {
    return ResponseEntity.ok(financeQueryService.handle(new GetTotalPlatformRevenueQuery()));
  }

  @GetMapping("/admin/debtors")
  @Operation(summary = "Obtiene la lista de todos los taxistas morosos (BLOCKED)")
  public ResponseEntity<List<DriverAccountResource>> getDebtorDrivers() {
    var query = new GetDebtorAccountsQuery();
    var debtors = financeQueryService.handle(query).stream()
        .map(DriverAccountResourceFromEntityAssembler::toResourceFromEntity)
        .collect(Collectors.toList());

    return ResponseEntity.ok(debtors);
  }

  @GetMapping("/admin/settlements")
  @Operation(summary = "Obtiene el historial completo de todos los viajes liquidados en la plataforma (Para Auditoría Admin)")
  public ResponseEntity<List<SettlementResource>> getAllSettlements() {
    var allSettlements = financeQueryService.handle(new GetAllSettlementsQuery()).stream()
        .map(SettlementResourceFromEntityAssembler::toResourceFromEntity)
        .collect(Collectors.toList());

    return ResponseEntity.ok(allSettlements);
  }

  // NUEVO ENDPOINT
  @GetMapping("/settlements/{id}")
  @Operation(summary = "Obtiene los detalles de un recibo/liquidación específico por su ID (Para vista de detalle en la App)")
  public ResponseEntity<SettlementResource> getSettlementById(@PathVariable Long id) {
    return financeQueryService.handle(new GetSettlementByIdQuery(id))
        .map(settlement -> ResponseEntity.ok(SettlementResourceFromEntityAssembler.toResourceFromEntity(settlement)))
        .orElse(ResponseEntity.notFound().build());
  }
}