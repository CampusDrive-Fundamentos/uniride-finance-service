Feature: Gestión de Deuda de Comisión

Como sistema, 
Quiero auditar el saldo deudor de los conductores, 
Para bloquear el acceso a nuevos viajes a quienes acumulen más de S/ 10.00 en comisiones impagas.

Scenario: Suspensión preventiva por límite de deuda de comisiones.

Given que un conductor ha realizado múltiples viajes y sus comisiones se han ido acumulando.  
When su saldo deudor supera la barrera de los S/ 10.00.  
Then el sistema actualiza el estado de su cuenta a bloqueada y le exige liquidar la deuda para continuar trabajando.

Examples:

|Campo|	Valor Mostrado al Usuario|
|Saldo Pendiente|	S/ 12.50|
|Estado de Cuenta|	Suspendida (Paga tu deuda para volver a conducir)|