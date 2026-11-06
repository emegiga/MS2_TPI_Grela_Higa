# ADR-003 — Una única excepción de dominio para los errores de negocio

**Estado:** aceptada

## Contexto
Cada método informaba los errores de una forma distinta (`-1`, `false`, `null`, mensaje impreso,
`catch` vacíos; CS-04) y los mensajes se imprimían desde la lógica de negocio (CS-03), por lo que la
precarga tenía que silenciar `System.out`.

## Decisión
El dominio y el servicio lanzan `ReglaDeNegocioException` con el motivo; ante un éxito devuelven la
`Solicitud` afectada. La consola es la única que decide cómo mostrar el resultado
(`"Error: " + motivo`, en un único método).

Los errores de programación (un criterio inexistente, un dato obligatorio nulo) no son reglas de
negocio: usan `IllegalArgumentException` / `NullPointerException`.

## Alternativas descartadas
- **Una excepción por tipo de error** (`SolicitudNoEncontradaException`, `TransicionInvalidaException`...):
  más expresivo, pero para 5 casos agregaba clases sin beneficio concreto (KISS).
- **Objeto resultado (`Resultado<T>`) en lugar de excepciones:** obligaba a chequear el resultado en
  cada llamada, incluida la precarga.

## Consecuencias
- ✅ Un único canal de errores; el gestor no imprime nada.
- ⚠️ El texto de la excepción es el que ve el usuario (sin tildes, para no cambiar la salida). Ver la observación CR-08 del code review.
