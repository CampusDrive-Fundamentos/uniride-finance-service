Feature: Vinculación de Tarjeta

Como conductor, 
Quiero registrar un método de pago válido, 
Para que la plataforma pueda procesar mis transacciones y cobros de forma automatizada

Scenario: Configuración de cuenta financiera del conductor

Given que el conductor navega a la sección de cobros en su perfil.  
When ingresa los datos de su tarjeta de débito o crédito y los guarda.  
Then el sistema asocia el método de pago a su cuenta financiera y la habilita para operar.  

Examples:

|       Campo       |        Valor Mostrado al Usuario         |
| Métodos de Pago   |       Tarjeta terminada en ****4567      |
|       Estado      |           Vinculación Exitosa            |