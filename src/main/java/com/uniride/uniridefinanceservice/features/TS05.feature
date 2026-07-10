Feature: Generación de liquidación y cobro de comisión al finalizar el trayecto.

Como conductor, 
Quiero que el sistema calcule mi comisión automáticamente, 
Para mantener un historial transparente de mis ganancias y retenciones

Scenario: Generación de liquidación y cobro de comisión al finalizar el trayecto

Given que el viaje acaba de finalizar y el conductor confirma la llegada. 
When el módulo de finanzas recibe y procesa los datos del servicio.  
Then el sistema descuenta el 10% (con un mínimo de S/ 1.50) y lo registra en la cuenta del conductor.

Examples:

|        Campo      | Valor Mostrado al Usuario|
| Ingreso del Viaje |         S/ 20.00         |
| Comisión retenida |      - S/ 2.00 (10%)     |

