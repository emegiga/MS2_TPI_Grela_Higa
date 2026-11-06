# ADR-006 — Deudas técnicas aceptadas en el code review

**Estado:** aceptada

## Contexto
El code review de la Etapa 7 ([checklist](../checklist.md)) registró 13 observaciones. Diez se
corrigieron; tres no se corrigen en este trabajo. Se registran como deuda técnica, con su motivo y
un plan, para que queden visibles.

## Decisión

| Observación | Qué queda pendiente | Por qué se acepta |
|---|---|---|
| CR-08 | Los mensajes de `ReglaDeNegocioException` están en minúscula, sin tildes y se repiten en la interfaz | Corregirlos cambia casi todos los goldens. Se prioriza conservar el comportamiento externo verificado en la Etapa 6 |
| CR-09 | Los emails de `DatosIniciales` usan dominios reales (gmail, outlook, hotmail, yahoo) | Los nombres y los emails son inventados. Cambiar el dominio obliga a regenerar 7 goldens (E01 a E05, E18 y E19) sin ningún cambio de comportamiento |
| CR-11 | Las pruebas de caracterización enmascaran la fecha con `LocalDate.now()` | Riesgo bajo: solo falla una ejecución que cruce la medianoche. Corregirlo implica que `Main` reciba un reloj configurable |

## Alternativas descartadas
- **Corregir todo en la Etapa 7:** CR-08 y CR-09 obligan a regenerar goldens por cambios que no son de
  comportamiento. Mezclados con los de CR-01, el diff de los goldens dejaría de mostrar con claridad
  qué cambió para el usuario.
- **No registrarlas:** la deuda existiría igual, pero sin justificación ni plan.

## Plan para saldarlas
- **CR-08:** definir los mensajes en la interfaz (la excepción lleva un código y la UI elige el texto)
  y regenerar los goldens en un commit propio.
- **CR-09:** pasar los emails a `@example.com`, un dominio reservado para ejemplos, en un commit propio
  que regenere solo esos 7 goldens, y verificar con `git diff --word-diff` que solo cambia el dominio.
- **CR-11:** que `Main` reciba el `Clock` (el gestor ya lo recibe por constructor) y que las pruebas
  fijen la fecha.

## Consecuencias
- ✅ El checklist y este registro dicen qué falta y por qué.
- ⚠️ El usuario sigue viendo mensajes sin tildes, los datos de ejemplo tienen dominios reales y existe
  un caso borde (medianoche) en el que la suite podría fallar sin que haya un error real.
