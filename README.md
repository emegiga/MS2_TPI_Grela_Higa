# Sistema de Gestión de Solicitudes Académicas

> **Metodología de Sistemas II - Trabajo Práctico Integrador (TPI)**

| Dato | Valor |
|---|---|
| Comisión | `1` |
| Integrantes | `Grela, Ariel` · `Higa, Matias`|
| Fecha de entrega | `A definir` |
| Versión actual | `estado-inicial` (Etapa 1 - Sistema base) |

---

## 📌 Descripción

Sistema que permite registrar solicitudes académicas de estudiantes, clasificarlas por **tipo**, **prioridad** y **estado**, asignarles un **responsable/área**, consultarlas y generar un reporte básico.



> ⚠️ Esta versión corresponde al **sistema base (estado inicial)**. Funciona correctamente desde la mirada del usuario, pero **contiene de forma deliberada problemas de diseño e implementación** (code smells, alto acoplamiento, responsabilidades mezcladas, etc.) que serán diagnosticados y corregidos mediante refactoring seguro en las etapas siguientes del TPI.

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
| 4 | Buscar / filtrar | Consulta por estado, tipo, prioridad, estudiante o responsable. |
| 5 | Reporte básico | Lista las solicitudes con totales por estado. |

---

## 🛠️ Tecnologías

- **Lenguaje:** `Python 3.12 (o superior) / Java 21 `
- **Persistencia:** `a completar`
- **Interfaz:** Consola
- **Control de versiones:** Git + GitHub

---

## 🚀 Instalación y uso
`a completar`

### Requisitos previos
`a completar`


---

## 📁 Estructura del repositorio

```
A completar en entrega final
```


---

## 🏷️ Versionado y tags

| Tag | Descripción | Estado |
|---|---|---|
| `estado-inicial` | Sistema base con problemas detectables | ✅ Entrega 1 |
| `etapa-diagnostico` | Diagnóstico de calidad, deuda técnica y code smells | ⏳ Pendiente |
| `etapa-refactoring` | Aplicación de SOLID, Clean Code y refactorings | ⏳ Pendiente |
| `etapa-pruebas` | Casos de prueba y verificación antes/después | ⏳ Pendiente |
| `etapa-docs` | Code review y documentación final | ⏳ Pendiente |
| `estado-refactorizado` | Versión final mejorada | ⏳ Pendiente |

---

## 🔒 Sobre los datos

Todos los datos utilizados son **sintéticos o anonimizados**. No se emplean datos personales reales.

---

## 🤖 Uso de IA

La evidencia de los intercambios relevantes se encuentra en `docs/` y en el anexo del informe.

---

> TUPaD - UTN