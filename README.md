# Sistema de Gestión de Solicitudes Académicas

> **Metodología de Sistemas II - Trabajo Práctico Integrador (TPI)**

| Dato | Valor |
|---|---|
| Comisión | `1` |
| Integrantes | `Grela, Ariel` · `Higa, Matias`|
| Fecha de entrega | `A definir` |
| Versión actual | `etapa-docs` (Etapa 7 - Code review) |

---

## 📌 Descripción

Sistema que permite registrar solicitudes académicas de estudiantes, clasificarlas por **tipo**, **prioridad** y **estado**, asignarles un **responsable/área**, consultarlas y generar un reporte básico.



> **Versión refactorizada.** El comportamiento externo se conserva respecto de `estado-inicial` (verificado con 19 escenarios golden) y la estructura interna se reorganizó aplicando SOLID, Clean Code y los refactorings documentados en el informe.

---

## 🎯 Alcance

**Incluye:**
- Alta de solicitudes asociadas a un estudiante de un listado precargado.
- Cambio de estado de una solicitud.
- Asignación de responsable/área.
- Búsqueda y filtrado de solicitudes.
- Reporte básico de solicitudes.

**No incluye:**
- Alta, baja y modificación de estudiantes.
- Autenticación de usuarios ni manejo de roles/permisos.
- Interfaz gráfica.
- Notificaciones (correo, mensajería).

---

## 🧩 Entidades principales

| Entidad | Descripción |
|---|---|
| `Solicitud` | Pedido académico con tipo, prioridad, estado, fecha y descripción. |
| `Estudiante` | Usuario que realiza la solicitud. |
| `Responsable` / `Area` | Persona o sector asignado a resolverla. |
| `Estado` | Situación de la solicitud (ej.: Pendiente, En proceso, Resuelta, Rechazada). |

---

## ⚙️ Funcionalidades

| # | Operación | Descripción |
|---|---|---|
| 1 | Registrar solicitud | Crea una nueva solicitud vinculada a un estudiante seleccionado del listado. |
| 2 | Cambiar estado | Actualiza el estado de una solicitud existente. |
| 3 | Asignar responsable | Asocia un responsable/área a la solicitud. |
| 4 | Buscar / filtrar | Consulta por estado, tipo, prioridad o legajo del estudiante. |
| 5 | Reporte básico | Lista las solicitudes con totales por estado y por prioridad. |

---

## 🛠️ Tecnologías

- **Lenguaje:** Java 21
- **Build:** Gradle 9 (wrapper incluido)
- **Pruebas:** JUnit 6 · JaCoCo (cobertura mínima 80 %)
- **Persistencia:** en memoria, mediante repositorios
- **Interfaz:** consola
- **Control de versiones:** Git + GitHub

---

## 🚀 Instalación y uso

### Requisitos previos
JDK 21. No hace falta instalar Gradle: se usa el wrapper incluido.

### Comandos
```bash
git clone https://github.com/emegiga/MS2_TPI_Grela_Higa.git
cd MS2_TPI_Grela_Higa
gradlew run      # ejecuta el sistema (Linux/Mac: ./gradlew run)
gradlew test     # ejecuta las pruebas
gradlew check    # pruebas + verificación de cobertura mínima (80 %)
```

El reporte de cobertura queda en `build/reports/jacoco/test/html/index.html`.


---

## 📁 Estructura del repositorio

```
MS2_TPI_Grela_Higa/
├── src/
│   ├── main/java/tupad/ms2/
│   │   ├── Main.java                          # Punto de entrada: arma las dependencias y arranca el menú
│   │   ├── model/                             # Dominio: entidades y reglas de negocio
│   │   │   ├── Solicitud.java                 # Ciclo de vida, validaciones y asignación de responsable
│   │   │   ├── Estudiante.java
│   │   │   ├── Responsable.java
│   │   │   ├── NuevaSolicitud.java            # Parameter Object con los datos de alta
│   │   │   ├── enums/
│   │   │   │   ├── Estado.java                # Estados y transiciones permitidas
│   │   │   │   ├── Prioridad.java
│   │   │   │   └── TipoSolicitud.java
│   │   │   └── excepciones/
│   │   │       └── ReglaDeNegocioException.java
│   │   ├── servicio/                          # Casos de uso
│   │   │   ├── GestorSolicitudes.java         # Registrar, cambiar estado, asignar, buscar, reporte
│   │   │   ├── FiltrosSolicitud.java          # Criterios de búsqueda (estado, tipo, legajo, prioridad)
│   │   │   └── ResumenSolicitudes.java        # Datos calculados del reporte
│   │   ├── repositorio/                       # Persistencia (interfaces + implementación en memoria)
│   │   │   ├── SolicitudRepositorio.java
│   │   │   ├── SolicitudRepositorioEnMemoria.java
│   │   │   ├── EstudianteRepositorio.java
│   │   │   ├── EstudianteRepositorioEnMemoria.java
│   │   │   ├── ResponsableRepositorio.java
│   │   │   └── ResponsableRepositorioEnMemoria.java
│   │   ├── datos/
│   │   │   └── DatosIniciales.java            # Datos sintéticos precargados
│   │   └── ui/                                # Interfaz de consola
│   │       ├── MenuConsola.java               # Menú y opciones
│   │       ├── EntradaConsola.java            # Lectura y escritura por consola
│   │       └── FormatoSolicitud.java          # Formato de texto de los listados
│   └── test/
│       ├── java/tupad/ms2/
│       │   ├── CaracterizacionConsolaTest.java    # Tests golden master (comportamiento externo)
│       │   ├── EjecutorConsola.java               # Ejecuta el sistema con entradas simuladas
│       │   ├── model/                             # EstadoTest, PrioridadTest, SolicitudTest
│       │   ├── servicio/                          # GestorSolicitudesTest
│       │   ├── repositorio/                       # RepositoriosEnMemoriaTest
│       │   └── ui/                                # EntradaConsolaTest
│       └── resources/golden/                      # 19 salidas (E01 a E19)
├── docs/
│   ├── TPI_MS2_Grela_Higa.pdf                 # Informe
│   ├── Anexo_TPI_MS2_Grela_Higa.pdf           # Anexo de evidencias
│   └── checklist.md                           # Checklist de code review (Etapa 7)
├── gradle/wrapper/                            # Gradle Wrapper
├── build.gradle                               # Dependencias, JaCoCo y cobertura mínima
├── settings.gradle
├── gradlew / gradlew.bat
├── .gitignore
├── .gitattributes                             # Finales de línea consistentes (CR-06)
└── README.md
```


---

## 🏷️ Versionado y tags

| Tag | Descripción | Estado |
|---|---|---|
| `estado-inicial` | Sistema base con problemas detectables | ✅ Entrega 1 |
| `etapa-diagnostico` | Diagnóstico de calidad, deuda técnica y code smells | ✅ Entrega 2 |
| `etapa-refactoring` | Aplicación de SOLID, Clean Code y refactorings | ✅ Entrega 3 |
| `etapa-pruebas` | Casos de prueba y verificación antes/después | ✅ Entrega 4 |
| `etapa-docs` | Code review y documentación final | ⏳ En curso |
| `estado-refactorizado` | Versión final mejorada | ⏳ Pendiente |

---

## 🔒 Sobre los datos

Todos los datos utilizados son **sintéticos o anonimizados**. No se emplean datos personales reales.

---

## 🤖 Uso de IA

La evidencia de los intercambios relevantes se encuentra en el anexo del informe (`docs/Anexo_TPI_MS2_Grela_Higa.pdf`).

---

> TUPaD - UTN
