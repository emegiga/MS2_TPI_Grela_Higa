# Etapa 8 — Conclusiones y aprendizaje

> Sistema de Gestión de Solicitudes Académicas · Metodología de Sistemas II · Comisión 1<br>
> Integrantes: Grela, Ariel · Higa, Matías

## 1. Resultado del trabajo

El sistema base (`estado-inicial`) funcionaba correctamente para el usuario, pero tenía una calidad interna baja: 13 code smells diagnosticados en la Etapa 3. La versión final (`estado-refactorizado`) conserva el comportamiento externo y es mucho más fácil de modificar, probar y extender.

| Indicador | `estado-inicial` | `estado-refactorizado` |
|---|---|---|
| Método más largo | `Main.main()`: 162 líneas | `MenuConsola.registrarSolicitud()`: 26 líneas |
| Anidamiento máximo en un método | 5 niveles | 2 niveles |
| Atributos públicos | 18 | 0 |
| `System.out` en la lógica de negocio | 23 | 0 |
| Copias de la lista de tipos válidos | 3 | 1 |
| Pruebas automatizadas | 0 | 88 |
| Cobertura (líneas / ramas) | — | 96,8 % / 96,8 % |
| Code smells del diagnóstico resueltos | — | 13 de 13 |

El método más largo de la versión final se midió sin contar `DatosIniciales.estudiantes()` (29 líneas), que es una tabla de datos de ejemplo.

Hubo tres cambios de comportamiento intencionales, cada uno en un commit `fix:` propio y con su prueba:

- **CAP-16** (Etapa 5): una prioridad no numérica ya no cierra el programa.
- **CR-01** (Etapa 7): el menú solo ofrece las transiciones de estado válidas (se regeneraron los goldens E07, E08 y E18).
- **CR-03** (Etapa 7): una descripción formada solo por espacios ya no pasa la validación del largo mínimo.

Fuera de esos cambios, en los 19 escenarios de caracterización el usuario ve exactamente lo mismo que en el sistema original: la suite final ejecutada sobre `estado-inicial` solo falla en E07, E08, E18 y CAP-16 (`docs/pruebas/evidencias/suite-final-sobre-estado-inicial.txt`).

## 2. Aprendizajes

- **"Funciona" no es lo mismo que "se puede mantener".** El sistema base cumplía todo lo que veía el usuario, pero un cambio chico (agregar un tipo de solicitud) obligaba a tocar varios archivos. La calidad interna no se ve en la pantalla, pero condiciona el costo de cada cambio.
- **Sin pruebas no hay refactoring seguro.** La red de seguridad (19 pruebas de caracterización sobre `estado-inicial`) tuvo que construirse antes que cualquier mejora, y demostró su valor al detectar una regresión que a simple vista no se veía: un registro fallido consumía un id (escenario E19).
- **Pasos chicos y verificables.** Un refactoring por commit hizo que cada cambio fuera fácil de revisar y, si hacía falta, de revertir. También permitió separar con claridad las correcciones que sí cambiaban el comportamiento.
- **Los principios se refuerzan entre sí.** Separar responsabilidades (SRP) e invertir dependencias (DIP) fue lo que habilitó las pruebas unitarias, que en la versión inicial eran imposibles.
- **El review cruzado encuentra lo que las pruebas no.** El checklist aplicado por el otro integrante detectó problemas que ninguna prueba marcaba: una regla de negocio que se podía saltear con espacios (CR-03), opciones del menú incoherentes con el dominio (CR-01) y código sin uso (CR-12). También obligó a decidir, y a dejar por escrito, qué no se corregía y por qué (deudas CR-08, CR-09 y CR-11, ADR-006).

## 3. Dificultades

- Preservar **exactamente** la salida de la consola (mismos textos, espacios y orden de las validaciones) obligó a conservar detalles del diseño original, como los mensajes sin tildes (CR-08).
- Decidir qué **no** corregir durante el refactoring: las mejoras de usabilidad evidentes quedaron para la Etapa 7 porque cambiaban el comportamiento.
- Coordinar el trabajo de dos integrantes con una rama y un Pull Request por etapa, y mantener la trazabilidad entre el informe, los commits, los tags y las capturas.

## 4. Límites del trabajo

- La persistencia sigue siendo en memoria: los datos se pierden al cerrar el programa, como en el sistema base.
- Las pruebas de caracterización comparan texto exacto: cualquier cambio de redacción en un mensaje las rompe.
- La prueba de usabilidad de la Etapa 2 usó datos simulados y no se repitió sobre la versión final.

## 5. Mejoras futuras

| Mejora | Origen |
|---|---|
| Persistencia en archivo o base de datos como nueva implementación de los repositorios, sin tocar el gestor | Límite del trabajo |
| Búsqueda por responsable (un filtro nuevo en `FiltrosSolicitud`) | Funcionalidad pendiente |
| Mensajes de error con códigos y textos definidos en la interfaz | CR-08 (deuda aceptada) |
| Emails de ejemplo con un dominio reservado (`@example.com`) | CR-09 (deuda aceptada) |
| Reloj configurable en `Main` para que los goldens no dependan de la fecha | CR-11 (deuda aceptada) |
| Separar `MenuConsola` en una clase por pantalla si el sistema crece (hoy concentra 10 de las 11 líneas sin cubrir) | Cobertura (Etapa 7) |

## 6. Relación con los contenidos de la materia

| Unidad | Dónde se aplicó |
|---|---|
| 1 — Calidad de diseño y construcción | Calidad interna y externa, deuda técnica, code smells, Clean Code, SOLID y refactoring (Etapas 2 a 5) |
| 2 — Patrones de diseño | Criterios de búsqueda intercambiables (estilo Strategy), opciones del menú como objetos (estilo Command), repositorios detrás de interfaces |
| 3 — Verificación y validación | Pruebas de caracterización, regresión y unitarias; cobertura con umbral mínimo; checklist y criterios de aceptación |
| 4 — Repositorios | Una rama y un Pull Request por etapa revisado por el otro integrante, Conventional Commits, tags por etapa, `.gitattributes` y `.gitignore` |
| 5 — Habilidades blandas e IA | Review cruzado con feedback constructivo, decisiones acordadas entre integrantes y uso de IA declarado con su grado de intervención |

## 7. Conclusiones

### Conclusión grupal

El trabajo mostró que mejorar la calidad interna de un sistema es un proceso que se puede planificar y verificar: primero se diagnostica, después se construye una red de seguridad, se mejora en pasos pequeños justificados por principios y se revisa con criterios explícitos. La versión final hace lo mismo que la inicial para el usuario, salvo tres correcciones justificadas (CAP-16, CR-01 y CR-03), pero ahora puede modificarse, probarse y extenderse con un costo y un riesgo mucho menores.

### Conclusión individual — Grela, Ariel

Me tocaron el refactoring, las pruebas y las correcciones del code review (etapas 4 a 7), sobre el diagnóstico que había hecho Matías. Lo que más me llevo es el orden en que hay que trabajar: antes de tocar una línea de código hubo que congelar el comportamiento con las pruebas de caracterización. Esa red de seguridad no fue un trámite: detectó una regresión en R-08 (un registro fallido consumía un id) que revisando el código a simple vista no habría encontrado.

También aprendí a trabajar con Git como herramienta de equipo y no solo para guardar archivos: al principio no entendía para qué servían los tags, y terminé usándolos junto con una rama y un Pull Request por etapa, merge commit para no perder la historia de cada refactoring y commits que separan los cambios de comportamiento (`fix:`) de los refactorings. El review cruzado me mostró el valor de que otro mire el trabajo con criterios explícitos: el checklist de Matías encontró problemas que las 78 pruebas no marcaban (CR-01 y CR-03).

Usé IA con un grado de intervención alto para generar código, pruebas y documentación, y eso me obligó a revisar cada resultado en lugar de darlo por bueno: la regresión de R-08 apareció justamente en código generado y la detectaron las pruebas. Por eso el uso de IA está declarado en el anexo con los pedidos reales. La conclusión que saco es que la IA acelera el trabajo, pero las decisiones, la verificación y la responsabilidad sobre lo que se entrega siguen siendo del equipo.

### Conclusión individual — Higa, Matías

Me tocó la base y el cierre del trabajo: la selección y descripción del sistema, el análisis de calidad externa e interna y el diagnóstico de deuda técnica (etapas 1 a 3), el README, y en la Etapa 7 el armado del checklist y el review del código y las pruebas, además de las correcciones del repositorio (CR-04, CR-05 y CR-06). Lo que más me llevo del diagnóstico es que “funciona” no alcanza: el sistema base cumplía todo lo que veía el usuario, y sin embargo encontrar los 13 code smells con evidencia, ubicación e impacto me mostró cuánto costaba cada cambio.

El code review me enseñó que revisar no es opinar. El review también funcionó en sentido inverso: Ariel encontró que mi primera corrección del README seguía incompleta (CR-04), lo que me mostró el valor de que otro verifique el trabajo propio. Además, un problema de entorno (una tilde en la ruta de mi carpeta impedía que Gradle encontrara las clases de test, mientras en la máquina de Ariel todo pasaba) me dejó claro que “en mi máquina funciona” no es una verificación.

Usé IA (Claude) principalmente para planificar la Etapa 7, armar la estructura del checklist y revisar el código en busca de observaciones. Cada observación la verifiqué en el código y en la ejecución antes de registrarla, y varias decisiones (los IDs, la severidad, qué aceptar como deuda) las ajustamos con Ariel. Mi conclusión es que la IA acelera el análisis, pero el criterio para decidir qué es un problema y qué se corrige tiene que ser nuestro.
