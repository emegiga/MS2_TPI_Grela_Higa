# Checklist - Etapa 7

> **Rama revisada:** `review/etapa-7` (base: `master`, tag `etapa-pruebas`)<br>
> **Revisores:** `Grela, Ariel` · `Higa, Matías` (review cruzado)<br>
> **Fecha:** `04/11/2026`

---

## Cómo usar este checklist

- Cada ítem se responde con **✅ Cumple**, **❌ No cumple** o **➖ No aplica**.
- Un ❌ se registra como observación (`CR-xx`) en la tabla de observaciones
- Evidencia: Archivo, comando y salida, comentario del PR o captura (`CAP-xx`).
- Cuando un ítem se vincula con un code smell de la Etapa 3, se indica en la columna **Ref.**

### Escala de severidad

| Severidad | Criterio |
|---|---|
| 🔴 Crítica | Rompe funcionalidad, provoca un cierre inesperado o hace fallar el build/tests. Impide el merge |
| 🟠 Mayor | Viola una regla de diseño (SOLID, encapsulamiento) o deja información incorrecta. Se corrige antes del merge |
| 🟡 Menor | Afecta la legibilidad o la consistencia sin riesgo funcional. Se corrige o se acepta como deuda justificada |
| 🔵 Opcional | Sugerencia. Mejora opcional |

---

## 1. Funcionalidad y comportamiento externo

| ID | Criterio | Ref. | Resultado | Evidencia |
|---|---|---|---|---|
| F-01 | Los 5 casos de uso (registrar, cambiar estado, asignar responsable, buscar/filtrar, reporte) funcionan desde el menú. | Etapa 1 | ✅ | Escenarios golden E01, E06–E07, E10–E16 y E18 |
| F-02 | Las salidas de consola coinciden con los escenarios golden E01–E19. | Etapa 6 | ✅ | `CaracterizacionConsolaTest` + `src/test/resources/golden/` (ver T-05) |
| F-03 | Ninguna entrada inválida (texto, número fuera de rango, vacío) cierra el programa. | CAP-16 | ✅ | Test `prioridadNoNumericaSeRechazaSinCerrarElPrograma`; golden E15 y E17 |
| F-04 | Las transiciones de estado respetan el ciclo de vida: PENDIENTE → EN_PROCESO/RECHAZADA, EN_PROCESO → RESUELTA/RECHAZADA. | | ✅ | `Estado.permiteTransicionA` + `EstadoTest.transiciones` |
| F-05 | Las opciones que ofrece la UI son coherentes con las reglas del dominio (no ofrece acciones que después rechaza). | | ❌ CR-01 | Golden E08: ofrece RESUELTA desde PENDIENTE y EN_PROCESO desde EN_PROCESO → "transicion invalida" |
| F-06 | Un registro fallido no consume un id. | E19 | ✅ | Golden E19 + `GestorSolicitudesTest.registroFallidoNoConsumeId` |

## 2. Diseño y principios SOLID

| ID | Criterio | Ref. | Resultado | Evidencia |
|---|---|---|---|---|
| D-01 | **SRP:** Cada clase tiene una sola razón para cambiar (UI, servicio, dominio, repositorio y datos están separados). | CS-02, CS-03 | ✅ | Paquetes `ui`, `servicio`, `model`, `repositorio`, `datos` (R-08, R-10) |
| D-02 | **OCP:** Agregar un estado, una prioridad, un tipo o un criterio de búsqueda no obliga a modificar varias clases. | CS-06, CS-08 | ❌ CR-01, CR-02 | `MenuConsola.cambiarEstado` y `mostrarReporte` enumeran valores a mano. La búsqueda sí cumple (`FiltrosSolicitud`) |
| D-03 | **LSP:** Las implementaciones en memoria pueden reemplazar a sus interfaces sin cambiar el comportamiento esperado. | | ✅ | `RepositoriosEnMemoriaTest` |
| D-04 | **ISP:** Las interfaces de repositorio exponen solo las operaciones que se usan. | | ✅ | `EstudianteRepositorio` y `ResponsableRepositorio` solo lectura |
| D-05 | **DIP:** `GestorSolicitudes` depende de abstracciones. Solo `Main` conoce las implementaciones concretas | CS-12 | ✅ | Constructor de `GestorSolicitudes` (repositorios + `Clock`); `Main` como composition root |
| D-06 | No hay dependencias en sentido inverso (el dominio no conoce la UI ni los repositorios) | CS-03 | ✅ | `model` no importa `ui`, `servicio` ni `repositorio` |

## 3. Clean Code

| ID | Criterio | Ref. | Resultado | Evidencia |
|---|---|---|---|---|
| C-01 | Nombres descriptivos y en el mismo idioma | CS-11 | ✅ | Nombres descriptivos en castellano (R-01) |
| C-02 | Los métodos tienen una sola tarea y no superan las 20 líneas | CS-01 | ❌ CR-07 |`MenuConsola.registrarSolicitud` (mas de 30 líneas, más de una tarea) |
| C-03 | No hay código duplicado  | CS-05 | ❌ CR-07 | "Registro cancelado." ×2 <br>"No hay estudiantes cargados." ×2 en `MenuConsola` |
| C-04 | No hay números ni strings mágicos | CS-07 | ✅ | `LARGO_MINIMO_DESCRIPCION`, `OPCION_SALIR`, `SIN_RESPONSABLE` |
| C-05 | No hay listas largas de parámetros (mas de 3) | CS-13 | ✅ | `NuevaSolicitud` (R-06). Los 4 parámetros del constructor del gestor son inyección de dependencias (aceptado) |
| C-06 | Comentarios explican el *por qué*, no el *qué*.<br>No hay código comentado ni comentarios desactualizados | | ❌ CR-10 | Comentarios "antes…" en `DatosIniciales`, `NuevaSolicitud`, `MenuConsola` |
| C-07 | Formato es consistente (indentación, imports sin usar, líneas en blanco) | | ❌ CR-12 | IntelliJ *Inspect Code* (CAP-75): 6 warnings, 17 weak warnings, 2.466 typos. 5 warnings de Java son reales (ver CR-12). La versión de Gradle y la sugerencia de `record` se descartan; los typos son falsos positivos del diccionario en inglés |

## 4. Manejo de errores

| ID | Criterio | Ref. | Resultado | Evidencia |
|---|---|---|---|---|
| ER-01 | Unico mecanismo para los errores de negocio (`ReglaDeNegocioException`). No hay códigos de error ni `null` | CS-04 | ✅ | R-03; los repositorios devuelven `Optional` |
| ER-02 | Las excepciones se capturan en la capa de presentación, no se silencian. | CS-04 | ✅ | Solo `MenuConsola` las captura (`mostrarError`) |
| ER-03 | Mensajes de error claros, consistentes y comprensibles | | ❌ CR-08 | Minúscula y sin tildes; `elegirSolicitudAbierta` repite los mensajes del gestor |
| ER-04 | Las validaciones del dominio cubren casos borde (nulos, vacíos, solo espacios) | | ❌ CR-03 | Constructor de `Solicitud`: 5 espacios pasan la validación |

## 5. Dominio y encapsulamiento

| ID | Criterio | Ref. | Resultado | Evidencia |
|---|---|---|---|---|
| M-01 | Las reglas de negocio no viven en la UI ni en el servicio | CS-03, CS-10 | ❌ CR-01 | "EN_PROCESO exige responsable" está en `Solicitud` y `MenuConsola.cambiarEstado` |
| M-02 | No hay atributos públicos ni setters que permitan saltear las validaciones | CS-09 | ✅ | Atributos `private final`, sin setters (R-07) |
| M-03 | Las colecciones se exponen sin poder modificarse desde afuera | CS-09 | ✅ | `unmodifiableList`, `List.copyOf`, `listadoDeSoloLectura` (test) |
| M-04 | Los conceptos del dominio se representan con tipos propios (enums, records), no con primitivos. | CS-07 | ✅ | `Estado`, `Prioridad`, `TipoSolicitud`, `NuevaSolicitud` |

## 6. Pruebas

| ID | Criterio | Ref. | Resultado | Evidencia |
|---|---|---|---|---|
| T-01 | gradlew check termina sin errores | Etapa 6 | ✅ | `gradlew clean check` → BUILD SUCCESSFUL, 78 tests, 0 fallos (CAP-71) |
| T-02 | Cobertura de JaCoCo ≥ 80 % en líneas y ≥ 80 % en ramas | Etapa 6 | ✅ | Líneas 96,7 % (326/337) · Ramas 96,6 % (84/87) · Instrucciones 98,5 % (CAP-72) |
| T-03 | Cada regla de negocio tiene al menos un test válido y otro inválido | | ❌ CR-03 | Falta el caso "descripción solo con espacios" |
| T-04 | Los tests son independientes, legibles (nombre que describe el escenario) y no dependen de la fecha del sistema. | | ✅ con reserva (CR-11) | `Clock` fijo en `GestorSolicitudesTest`; `CaracterizacionConsolaTest` usa `LocalDate.now()` |
| T-05 | Los tests de caracterización (golden) siguen pasando sin regenerarse. | Etapa 6 | ✅ | Los 19 escenarios golden pasan sin regenerar (CAP-71). `git log --name-status -- src/test/resources/golden`: los archivos solo se agregaron, nunca se modificaron |

## 7. Repositorio y documentación

| ID | Criterio | Ref. | Resultado | Evidencia |
|---|---|---|---|---|
| GH-01 | README describe la versión actual | 6.1 | ❌ CR-04 | `8bbd080` actualiza el README, pero todavía menciona Python 3.12, una búsqueda por responsable que no existe y tiene bloques de código mal cerrados (ver CR-04) |
| GH-02 | Tags `estado-inicial`, `etapa-diagnostico`, `etapa-refactoring`, `etapa-pruebas`, `etapa-docs` y `estado-refactorizado` presentes | 6.1 | ➖ | Están 4 de 6: `estado-inicial`, `etapa-diagnostico`, `etapa-refactoring`, `etapa-pruebas`. `etapa-docs` y `estado-refactorizado` se crean al cerrar esta etapa |
| GH-03 | Los commits con mensajes descriptivos y siguen una convención (`refactor:`, `test:`, `docs:`, `fix:`). | 6.1 | ✅ | Conventional Commits con IDs (R-01 a R-10, CAP-16, CR-xx). Única excepción: el commit inicial `df893a7`. Observación: Matías figura con dos autores, "Matias H" (commits locales) y "Matías" (merges desde GitHub) |
| GH-04 | `/docs` contiene el checklist de code review, las evidencias y las decisiones técnicas | 6.1 | ❌ CR-13 | `docs/` contiene solo este checklist y los PDF del informe y del anexo |
| GH-05 | `.gitignore` excluye `build/`, `.gradle/`, `.idea/` y no hay archivos del IDE versionados | | ❌ CR-05 | `.gitignore` excluye `build/`, `.gradle` e `.idea/`, pero `.idea/` estaba versionado (4 archivos). Corrección en `ef745e2`: `git ls-files .idea` → 0 (a verificar en el PR) |
| GH-06 | Los finales de línea son consistentes (sin diffs falsos por CRLF/LF) | | ❌ CR-06 | No había `.gitattributes`. Corrección en `44be626`: `git ls-files --eol` → todos los archivos de texto en LF en el repositorio (a verificar en el PR) |

## 8. Datos y seguridad

| ID | Criterio | Ref. | Resultado | Evidencia |
|---|---|---|---|---|
| S-01 | Todos los datos son sintéticos y no parecen datos reales | Cond. 10 | ❌ CR-09 | `DatosIniciales`: nombres ficticios, pero emails con dominios reales (gmail, outlook, hotmail, yahoo). Aceptado como deuda (CR-09) |
| S-02 | No hay credenciales, tokens ni rutas locales en el código | | ✅ | `git grep` de rutas locales, contraseñas, tokens y claves en `src/`, `build.gradle` y `README.md`: sin resultados |

---

## Registro de observaciones

| ID | Ítem | Archivo / línea | Severidad | Observación | Feedback al autor | Acción correctiva | Commit | Estado |
|---|---|---|---|---|---|---|---|---|
| CR-01 | F-05, D-02, M-01 | `ui/MenuConsola.java` – `cambiarEstado()` | 🟠 Mayor | Lista de estados escrita a mano y repite la regla del responsable<br>UI ofrece transiciones que después rechaza (golden E08) | Si cambia el ciclo de vida hay que tocar dos lugares. Sería mejor armar las opciones con `Estado.values()` filtrado por `permiteTransicionA()`. | | | |
| CR-02 | D-02 | `ui/MenuConsola.java` – `mostrarReporte()` | 🟠 Mayor | El reporte nombra cada estado y prioridad por separado | Si se agrega un estado, el reporte lo omite sin dar error (OCP). Sugiero recorrer los enums y agregar una etiqueta a `Estado`, manteniendo el texto actual | | | |
| CR-03 | ER-04, T-03 | `model/Solicitud.java` – constructor | 🟠 Mayor | Validación usa `length()`: 5 espacios se aceptan como descripción. No hay test de ese caso. | Es posible saltear una regla de negocio. Sugiero validar y agregar el caso a `SolicitudTest` | | | |
| CR-04 | GH-01 | `README.md` | 🟠 Mayor | Describía el "sistema base", decía "Python 3.12 / Java 21" y tenía secciones "a completar". `8bbd080` lo actualiza, pero todavía: (1) "Tecnologías" menciona Python 3.12; (2) "Buscar / filtrar" promete búsqueda por responsable, que el sistema no tiene; (3) los comandos de instalación no están en un bloque de código y en GitHub se ven en una sola línea; (4) el árbol de "Estructura" abre el bloque de código dos veces y queda cortado en `docs/`; (5) el reporte también totaliza por prioridad. | Quien clone el repo se guía por el README, así que tiene que coincidir con el código. Sugiero sacar Python, quitar "o responsable", poner los comandos en un bloque de código y cerrar bien el bloque de la estructura. | | | |
| CR-05 | GH-05 | `.idea/` | 🟡 Menor | `.idea/gradle.xml`, `misc.xml`, `vcs.xml` y `.gitignore` están versionados aunque `.gitignore` excluye la carpeta. | Generan ruido y conflictos según el IDE de cada uno. Sugiero dejar de versionar la carpeta | | | |
| CR-06 | GH-06 | Raíz del repo | 🟡 Menor | No hay `.gitattributes`; LF en el repo y CRLF en Windows generan diffs falsos. | Ensucia los reviews. Sugiero `.gitattributes` con `* text=auto eol=lf` y `*.bat text eol=crlf` | | | |
| CR-07 | C-02, C-03 | `ui/MenuConsola.java` – `registrarSolicitud()` | 🟡 Menor | Método de +30 líneas con 3 tareas y mensajes repetidos. | Sugiero extraer `leerPrioridad()` y pasar los mensajes repetidos a constantes | | | |
| CR-08 | ER-03 | Mensajes de `ReglaDeNegocioException` | 🟡 Menor | Mensajes en minúscula, sin tildes y repetidos en la UI. | Corregirlos cambia casi todos los golden. | Se acepta como deuda: se prioriza conservar el comportamiento externo verificado en la Etapa 6. | — | 📌 Aceptado como deuda |
| CR-09 | S-01 | `datos/DatosIniciales.java` | 🔵 Opcional | Emails sintéticos con dominios reales (gmail, outlook…). | Sugiero algo genérico como `@example.com` | Se acepta como deuda: los nombres y emails son inventados; cambiar el dominio obliga a regenerar 7 escenarios golden (E01–E05, E18, E19) sin cambio de comportamiento. Se documenta en ADR-006. | — | 📌 Aceptado como deuda |
| CR-10 | C-06 | `DatosIniciales`, `NuevaSolicitud`, `MenuConsola` | 🔵 Opcional | Comentarios que cuentan la historia del código ("antes…"). | La historia ya está en Git. Sugiero dejar solo el porqué del diseño actual | | | |
| CR-11 | T-04 | `CaracterizacionConsolaTest` | 🔵 Opcional | La fecha se enmascara con `LocalDate.now()` | Riesgo bajo: Corregirlo implica que `Main` reciba un reloj configurable. | Se acepta como deuda. | — | 📌 Aceptado como deuda |
| CR-12 | C-07 | `GestorSolicitudes`, `Estudiante`, `Responsable`, `CaracterizacionConsolaTest` | 🟡 Menor | Inspect Code reporta: `asignarResponsable()` devuelve un valor que nunca se usa; `Estudiante.getId()` y `Responsable.getArea()` no se usan; 2 líneas en blanco en Javadoc que se ignoran. | El código que no se usa agrega mantenimiento sin aportar nada y tapa las advertencias que importan. Sugiero que `asignarResponsable()` devuelva `void`, borrar los dos getters y usar `<p>` en el Javadoc. Se descartan la versión de Gradle y la conversión a `record` (ver C-07). | | | |
| CR-13 | GH-04 | `docs/` | 🟡 Menor | `/docs` solo tiene el checklist y los PDF del informe y del anexo; faltan las evidencias de pruebas y las decisiones técnicas que pide la consigna (6.1). | Es parte de lo que se evalúa en la entrega. Sugiero sumar el plan de pruebas con sus evidencias de ejecución y un registro de decisiones técnicas. | | | |

**Estados posibles:** ✅ Corregido · 📌 Aceptado como deuda (con justificación) · ❎ Descartado (con justificación)

> **Formato del feedback:** *observación → por qué importa → sugerencia concreta.* Se refiere siempre al código, nunca a la persona.

---

## Criterios de aceptación técnica

El PR de la Etapa 7 se aprueba cuando se cumplen **todas** estas condiciones:

- [ ] `gradlew check` pasa: todos los tests en verde y cobertura ≥ 80 % en líneas y ramas.
- [ ] Los 19 escenarios golden (E01–E19) pasan sin regenerarse, o cada cambio de salida está justificado en una observación.
- [ ] No queda abierta ninguna observación 🔴 crítica ni 🟠 mayor.
- [ ] Las observaciones 🟡 Menor están corregidas o aceptadas como deuda con justificación.
- [ ] No reaparece ninguno de los code smells CS-01 a CS-13.
- [ ] Cada corrección está en un commit propio que referencia su `CR-xx`.
- [ ] El README y `/docs` reflejan el estado final del sistema.
- [ ] El PR tiene la aprobación del revisor.

---

## Resultado del review

| Métrica | Valor |
|---|---|
| Ítems evaluados | |
| ✅ Cumple | |
| ❌ No cumple | |
| ➖ No aplica | |
| Observaciones registradas | |
| Corregidas / Aceptadas / Descartadas | |
| Cobertura final (líneas / ramas) | |
| **Dictamen** | `Aprobado` / `Requiere cambios` |