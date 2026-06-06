Feature: Visualización de monto individual (Cotización del viaje)

Como estudiante pasajero, 
Quiero ver la tarifa exacta de mi trayecto, 
Para saber cuánto me corresponde pagar antes de confirmar mi unión al viaje

Scenario: Cotización dinámica de tarifa según la distancia de bajada.

Given que el usuario ingresa su punto de bajada exacto en el buscador.  
When el motor de finanzas calcula las distancias de todos los pasajeros a bordo.  
Then la pantalla muestra el precio individualizado calculado de forma justa.  

Examples:

|       Campo       |          Valor Mostrado al Usuario           |
| Tarifa de tu ruta |                   S/ 4.50                    |
|       Aviso       | Monto calculado según tu distancia de bajada |