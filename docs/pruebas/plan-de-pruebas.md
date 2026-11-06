# Etapa 6 — Plan de pruebas y verificación del comportamiento

> Actualizado con las correcciones del code review de la Etapa 7 (CR-01 a CR-12).

## 1. Objetivo

Demostrar que los refactorings de las etapas 4 y 5 **no alteraron el comportamiento externo** del
sistema, y que las modificaciones intencionales quedaron cubiertas por pruebas: la corrección del
defecto CAP-16 (Etapa 5) y las correcciones CR-01 y CR-03 del code review (Etapa 7).

## 2. Estrategia: tres niveles de prueba

| Nivel | Qué verifica | Clases de test | Cuándo se escribió |
|---|---|---|---|
| **1. Caracterización (golden master)** | El comportamiento visible por consola: todo lo que el sistema imprime ante una secuencia de entradas | `CaracterizacionConsolaTest` + `EjecutorConsola` (19 escenarios) | **Antes** de refactorizar, sobre `estado-inicial` |
| **2. Regresión** | Que los defectos corregidos no vuelvan a aparecer: CAP-16, CR-01 (el menú ofrecía transiciones inválidas) y CR-03 (descripción de solo espacios) | `CaracterizacionConsolaTest.prioridadNoNumericaSeRechazaSinCerrarElPrograma`, escenario E08, `SolicitudTest` | Junto con cada corrección (`fix:`) |
| **3. Unitarias** | Reglas del dominio, casos de uso y componentes por separado | `EstadoTest`, `PrioridadTest`, `SolicitudTest`, `GestorSolicitudesTest`, `RepositoriosEnMemoriaTest`, `EntradaConsolaTest` | Después del refactoring (en `estado-inicial` no eran posibles) |

### 2.1 Por qué golden master primero

El sistema base no tenía pruebas y su lógica estaba mezclada con `System.out`, por lo que no se podía
testear por partes sin modificarlo antes. Por eso se probó **desde afuera**: `EjecutorConsola` ejecuta
`Main.main()` con un teclado simulado y captura toda la salida. Cada escenario se compara carácter
por carácter contra un archivo aprobado (`src/test/resources/golden/*.txt`) generado con la versión
`estado-inicial`.

- La fecha del día se reemplaza por `{HOY}` antes de comparar, para que el test no dependa del calendario.
- Los fines de línea se normalizan (`\r\n` → `\n`) para que el resultado sea igual en Windows y en Linux.
- Los goldens se revisaron manualmente antes de aprobarlos.

Mientras los 19 escenarios estén en verde, el usuario ve exactamente lo mismo que antes. Las únicas
excepciones son E07, E08 y E18, que se regeneraron a propósito con la corrección CR-01 (ver sección 5).

## 3. Casos de prueba de caracterización

La columna **Etapa 2** vincula cada escenario con la verificación funcional manual de la Etapa 2
(tabla 2.2.1 del informe y capturas del anexo).

| ID | Escenario | Qué cubre | Etapa 2 |
|---|---|---|---|
| E01 | Registrar solicitud válida | Alta completa + reporte con la nueva solicitud #4 | CAP-05 |
| E02 | Descripción corta | "Error: descripcion muy corta", no se registra | CAP-06 |
| E03 | Prioridad fuera de rango | "Error: prioridad invalida" | CAP-07 |
| E04 | Prioridad y descripción inválidas a la vez | Orden de las validaciones (se informa la prioridad) | — |
| E05 | Registro cancelado | Estudiante y tipo inválidos → "Registro cancelado." | — |
| E06 | Rechazar solicitud sin responsable | Solo ofrece RECHAZADA; el reporte refleja el cambio | CAP-08 |
| E07 | Resolver y modificar solicitud cerrada | EN_PROCESO → RESUELTA; luego cambio de estado y asignación bloqueados | CAP-09, CAP-10 |
| E08 | Solo ofrece transiciones válidas | Pendiente con responsable: EN_PROCESO o RECHAZADA; en proceso: RESUELTA o RECHAZADA; una opción fuera de la lista es "Opcion invalida" | — |
| E09 | Solicitud inexistente | Id inexistente, texto y vacío → "solicitud no encontrada" | — |
| E10 | Asignar y reasignar responsable | Muestra el responsable actual y lo reemplaza | CAP-11 |
| E11 | Buscar por estado | Los 4 estados (incluye resultados vacíos) | CAP-12 |
| E12 | Buscar por tipo | Los 4 tipos | — |
| E13 | Buscar por legajo | Estudiante con 2, 1 y 0 solicitudes | CAP-13 |
| E14 | Buscar por prioridad | Alta, Media y Baja | — |
| E15 | Búsqueda con opciones inválidas | Criterio, estado, legajo y prioridad fuera de rango | — |
| E16 | Reporte con datos precargados | Detalle y totales por estado y prioridad | CAP-14 |
| E17 | Opción inválida en el menú | Texto, vacío y número inexistente | CAP-15 |
| E18 | Ciclo de vida completo | Registrar → asignar → en proceso → resuelta → buscar → reporte | — |
| E19 | Un registro fallido no consume id | Registro rechazado + uno válido → la solicitud es la #4 | — |
| CAP-16 | Prioridad no numérica (regresión) | "Error: prioridad invalida", no consume id, el programa sigue | CAP-16 |

E07, E08 y E18 se regeneraron en el commit `fix(CR-01)`. Hasta ese commit, E08 se llamaba
`E08_transiciones_invalidas` y documentaba que el menú ofrecía PENDIENTE → RESUELTA y
EN_PROCESO → EN_PROCESO para después rechazarlas; E07 y E18 ajustaron la opción elegida para seguir
resolviendo la solicitud.

## 4. Pruebas unitarias (posibles gracias al refactoring)

| Clase de test | Casos | Qué verifica |
|---|---|---|
| `EstadoTest` | 19 | Las 16 combinaciones de la tabla de transiciones, qué estado requiere responsable, las etiquetas del reporte y cuáles estados son cerrados |
| `PrioridadTest` | 5 | Conversión de código 1/2/3 y rechazo de 0, 4, -1 y 99 |
| `SolicitudTest` | 21 | Estado inicial, descripción (nula, vacía, 3, 4 y 5 caracteres → valor límite; solo espacios o tabs, y texto corto rodeado de espacios → CR-03), ciclo completo, transiciones prohibidas, estados que ofrece el menú según estado y responsable (CR-01), asignación y reasignación, bloqueo en cerradas |
| `GestorSolicitudesTest` | 9 | Ids correlativos, fecha tomada de un **reloj fijo** inyectado, legajo / solicitud / responsable inexistentes, id no consumido, los 4 filtros, totales del reporte (con datos y vacío) |
| `RepositoriosEnMemoriaTest` | 5 | `siguienteId()` no reserva el id, búsqueda y orden, listados de solicitudes de solo lectura, copia defensiva |
| `EntradaConsolaTest` | 9 | Numeración de opciones, respuestas inválidas (0, 3, -1, texto, vacío, " 1"), lectura en la misma línea, conversión a número |

`GestorSolicitudesTest` usa repositorios armados en el propio test y `Clock.fixed(...)`: en
`estado-inicial` esto era imposible porque el gestor cargaba sus propios datos, imprimía por consola
y tomaba la fecha del sistema (CS-03 y CS-12).

## 5. Cómo se verificó que el refactoring no alteró el comportamiento

1. **Red de seguridad antes de tocar el código.** El primer commit del trabajo (`test: red de
   seguridad...`) agrega solo los tests; el código de producción es el original.
2. **Un refactoring por commit, suite completa después de cada uno.** Los commits `R-01` a `R-10`
   indican en su mensaje "19/19" o "20/20 tests en verde". Ningún commit de refactoring modifica un
   golden.
3. **La red de seguridad atrapó una regresión real.** Al preparar R-08 se vio que la primera versión
   del repositorio consumía un id en los registros fallidos. Se agregó el escenario E19 (golden
   generado sobre `estado-inicial`), se comprobó que **fallaba** con ese código (`#5` en lugar de
   `#4`, ver `evidencias/E19-regresion-detectada-en-R08.txt`) y se corrigió antes de confirmar R-08.
4. **La corrección del defecto fue un commit aparte.** El test de regresión de CAP-16 se ejecutó sin
   la corrección y falló por `NumberFormatException` (`evidencias/CAP16-test-regresion-en-rojo.txt`);
   con la corrección pasa.
5. **Comparación antes/después con la misma suite.** La suite de caracterización se ejecutó sobre el
   tag `estado-inicial` (en un worktree aparte, copiando solo el código de test): los 19 escenarios
   pasan y solo falla CAP-16, que es exactamente el defecto corregido
   (`evidencias/suite-sobre-estado-inicial.txt`). Sobre la versión refactorizada pasan los 20.
6. **Las correcciones del code review siguieron el mismo método (Etapa 7).** Cada observación es un
   commit propio con `gradlew check` en verde. Las que cambian lo que ve el usuario llevan el prefijo
   `fix:`: CR-01 regeneró E07, E08 y E18, y el diff de ese commit muestra que solo cambian las listas
   de opciones de "Nuevo estado"; CR-03 no cambia ningún golden y se prueba en `SolicitudTest`. Las
   demás (`refactor:`) no modifican ningún golden.
7. **Comparación final contra el sistema original.** La suite final se volvió a ejecutar sobre
   `estado-inicial`: fallan exactamente E07, E08 y E18 (CR-01) y la regresión CAP-16, y pasan los
   otros 16 escenarios (`evidencias/suite-final-sobre-estado-inicial.txt`).

| Versión | Escenarios golden (19) | Regresión CAP-16 | Unitarias (68) |
|---|---|---|---|
| `estado-inicial` con los goldens de la Etapa 6 | ✅ 19/19 | ❌ falla (defecto presente) | No aplicable: el diseño no lo permitía |
| `estado-inicial` con los goldens finales | 16/19: fallan solo E07, E08 y E18, los que cambió CR-01 | ❌ falla (defecto presente) | No aplicable |
| Versión final (`estado-refactorizado`) | ✅ 19/19 | ✅ pasa | ✅ 68/68 |

## 6. Resultados

- `gradlew clean check`: **88 tests, 0 fallos** con las correcciones del code review (78 al cierre de
  la Etapa 6) (`evidencias/ejecucion-gradlew-check.txt`).
- Cobertura JaCoCo: **96,8 % de líneas (330/341) y 96,8 % de ramas (90/93)**, con un umbral mínimo del
  80 % que hace fallar el build si no se cumple (`evidencias/cobertura-jacoco.txt`). Las 11 líneas sin
  cubrir están justificadas en ese archivo.

## 7. Cómo ejecutar

```bash
./gradlew test                          # todas las pruebas (Windows: gradlew.bat test)
./gradlew check                         # pruebas + verificación de cobertura mínima
./gradlew test -Dgolden.regenerar=true  # regenerar goldens (solo si cambia el comportamiento a propósito)
```

Reportes: `build/reports/tests/test/index.html` (resultados) y `build/reports/jacoco/test/html/index.html` (cobertura).

## 8. Límites

- Las pruebas de la consola comparan texto exacto: un cambio de redacción en un mensaje rompe el
  golden. Es el costo de usar la salida completa como contrato; es deseable mientras se refactoriza.
- La fecha se enmascara con `LocalDate.now()`: una ejecución que cruce la medianoche podría fallar
  (deuda aceptada CR-11, ver [ADR-006](../decisiones/ADR-006-deudas-tecnicas-aceptadas.md)).
- No hay pruebas de rendimiento ni de concurrencia (no aplican a un sistema de consola monousuario).
- Los caracteres no ASCII ("Sánchez") se ven bien en los tests (UTF-8), pero en la consola de Windows
  dependen de la página de códigos de la terminal.
