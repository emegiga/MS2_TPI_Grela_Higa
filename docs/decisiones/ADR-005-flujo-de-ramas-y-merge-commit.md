# ADR-005 — Una rama por etapa, PR con revisión y merge commit

**Estado:** aceptada

## Contexto
El trabajo lo hacen dos integrantes en paralelo y la consigna exige tags en los commits clave de cada
etapa, un historial claro y participación demostrable de ambos.

## Decisión
- Una rama por etapa (`docs/etapa-2`, `docs/etapa-3`, `refactor/etapa-4-5`,
  `test/etapa-6`, `review/etapa-7`), integrada a `master` mediante Pull Request revisado por el otro
  integrante.
- Integración con **merge commit**: conserva los commits de la rama (uno por refactoring) y, con ellos,
  los tags `etapa-refactoring`, `etapa-pruebas` y `etapa-docs`.
- Conventional Commits; los refactorings se numeran `R-xx` para cruzarlos con el informe.

## Alternativas descartadas
- **Squash and merge:** historial más corto, pero colapsaría los 10 refactorings en un commit (se
  perdería la evidencia de "un cambio, una verificación") y los tags quedarían apuntando a commits
  fuera de `master`.
- **Rebase and merge:** reescribe los hashes y dejaría desactualizadas las referencias del informe.

## Consecuencias
- ✅ Cada refactoring se puede revisar, revertir o mostrar en el video por separado.
- ⚠️ Las ramas se apilan (cada una parte de la anterior), así que los PR deben integrarse en orden.
