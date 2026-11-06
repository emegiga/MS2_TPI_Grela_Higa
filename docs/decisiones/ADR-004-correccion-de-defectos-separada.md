# ADR-004 — Corregir los defectos en commits aparte

**Estado:** aceptada

## Contexto
En la Etapa 2 se detectó que una prioridad no numérica (`ALTA`) cerraba el programa con
`NumberFormatException` (CAP-16). Corregirlo cambia el comportamiento externo, mientras que un
refactoring, por definición, no debe cambiarlo.

## Decisión
- Mientras se refactorizaba, el defecto se **conservó** y un test lo documentaba (`assertThrows`).
- La corrección se hizo al final, en un commit `fix:` separado, que reemplaza ese test por uno de
  regresión. Se verificó que el test de regresión falla sin la corrección y pasa con ella.
- Comportamiento elegido: el mismo mensaje que una prioridad fuera de rango
  (`Error: prioridad invalida`), sin consumir id y volviendo al menú. Es consistente con lo que el
  sistema ya hacía y no agrega pasos nuevos para el usuario.

## Alternativas descartadas
- **Corregirlo "de paso" durante R-05 o R-10:** ese commit dejaría de ser un refactoring puro y no se
  podría afirmar que no cambió el comportamiento.
- **Volver a pedir la prioridad hasta que sea válida:** mejor experiencia, pero introduce un flujo
  nuevo; queda como mejora futura.

## Aplicación en la Etapa 7
El mismo criterio se aplicó a las observaciones del code review que cambian el comportamiento:
CR-01 (el menú ofrecía transiciones que después rechazaba) y CR-03 (una descripción de solo espacios
pasaba la validación). Cada una es un commit `fix:` propio. CR-01 regeneró los goldens E07, E08 y
E18, y el diff de ese commit muestra exactamente qué cambió para el usuario.

## Consecuencias
- ✅ Trazabilidad: `git log` muestra exactamente qué commit cambió lo que ve el usuario y por qué.
- ✅ Los cambios de comportamiento del trabajo son tres (CAP-16, CR-01 y CR-03), cada uno en su
  commit y cubierto por pruebas.
