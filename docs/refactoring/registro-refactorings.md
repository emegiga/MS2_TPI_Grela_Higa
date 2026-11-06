# Etapas 4 y 5 — Registro de refactorings

Cada refactoring es **un commit** en la rama `refactor/etapa-4-5`. Después de cada uno se ejecutó la
suite de caracterización completa (ver [plan de pruebas](../pruebas/plan-de-pruebas.md)); ningún
refactoring modificó un archivo golden.

## 1. Resumen

| ID | Técnica (catálogo de Fowler) | Code smell atacado (Etapa 3) | Principio | Commit |
|---|---|---|---|---|
| — | Red de seguridad: tests de caracterización | Precondición del refactoring seguro | — | `5296c7f` |
| R-01 | Rename Variable / Rename Field | CS-11 Nombres poco descriptivos | Clean Code | `1f9a335` |
| **R-02** | **Extract Method** | CS-01 Long Method, CS-05 Duplicated Code | SRP, Clean Code | `63e2d99` |
| R-03 | Replace Error Code with Exception | CS-03 Lógica acoplada a la consola, CS-04 Manejo de errores inconsistente | SRP | `fcc6dba` |
| **R-04** | **Move Method** (reubicación de responsabilidad) | CS-10 Data Class, CS-08, CS-05 | SRP, Tell don't ask | `422ae57` |
| R-05 | Replace Type Code with Enum | CS-07 Primitive Obsession, CS-06 Shotgun Surgery | OCP | `2476657` |
| **R-06** | **Introduce Parameter Object** | CS-13 Long Parameter List | Clean Code | `06b53b5` |
| R-07 | Encapsulate Field / Encapsulate Collection | CS-09 Inappropriate Intimacy, CS-10 | Encapsulamiento | `69454ef` |
| — | Test E19 (hueco detectado en la red de seguridad) | — | — | `588f7c0` |
| **R-08** | **Extract Class** + inyección de dependencias | CS-02 Large Class, CS-12 Persistencia mezclada con el dominio | SRP, ISP, DIP | `3501183` |
| R-09 | Replace Conditional with Polymorphism | CS-08 Switch Statements, CS-03 | OCP (estilo Strategy) | `018a61e` |
| R-10 | Extract Class (interfaz de consola) | CS-01, CS-02 y CS-05 en `Main` | SRP, OCP (estilo Command) | `124f9c0` |
| FIX | Corrección del defecto CAP-16 | Defecto visible de la Etapa 2 | — | `bfccc62` |
| CR | Correcciones del code review (Etapa 7) | CR-01 a CR-13 | — | Ver la [sección 4](#4-correcciones-del-code-review-etapa-7) y `docs/checklist.md` |

Los cuatro que exige la consigna están en negrita: **Extract Method** (R-02), **Move Method** (R-04),
**Introduce Parameter Object** (R-06) y **Extract Class** (R-08). Los 13 code smells del diagnóstico
quedan cubiertos por al menos un refactoring.

## 2. Antes / después de los refactorings principales

### R-02 · Extract Method — `Main.main()` (CS-01)

**Antes** (`estado-inicial`): un único método de 162 líneas con 5 niveles de anidamiento.

```java
while (!salir) {
    System.out.println("1. Registrar solicitud");
    // ... menú ...
    String op = sc.nextLine();
    if (op.equals("1")) {
        if (g.estudiantes.isEmpty()) { ... } else {
            // 25 líneas para elegir estudiante, tipo, descripción y prioridad
        }
    } else if (op.equals("2")) {
        // 25 líneas: listar, pedir id, validar cerrada, elegir estado...
    } else if (op.equals("3")) {
        // 30 líneas: casi la misma secuencia que la opción 2
    } else if (op.equals("4")) {
        // 45 líneas: un bloque por criterio de búsqueda
    } ...
}
```

**Después** (R-02): el bucle delega en un método por operación, y la secuencia repetida de las
opciones 2 y 3 se extrae a `elegirSolicitudAbierta()`.

```java
while (!salir) {
    mostrarMenu();
    String opcion = scanner.nextLine();
    if (opcion.equals("1")) {
        registrarSolicitud(scanner, gestor);
    } else if (opcion.equals("2")) {
        cambiarEstado(scanner, gestor);
    } ...
}
```

**Beneficio:** cada operación se lee, modifica y prueba por separado. En R-10 estos métodos se
movieron a `MenuConsola`, donde el `if/else` se reemplazó por una lista de opciones.

### R-04 · Move Method — reglas de transición (CS-10, CS-08)

**Antes:** `Solicitud` era una clase de datos; las reglas estaban en `GestorSolicitudes.cambiarEstado()`
(43 líneas de `if` anidados comparando textos) y la condición "está cerrada" se repetía en `Main` (x2)
y en el gestor.

```java
// GestorSolicitudes (estado-inicial)
if (nuevoEstado.equals("EN_PROCESO")) {
    if (s.estado == Estado.PENDIENTE) {
        if (s.resp == null) {
            System.out.println("Error: debe asignar responsable primero");
            return false;
        }
        s.estado = Estado.EN_PROCESO;
    } else {
        System.out.println("Error: transicion invalida");
        return false;
    }
} else if (nuevoEstado.equals("RESUELTA")) { ... } else if ... // y así por cada estado

// Main (estado-inicial), dos veces
} else if (sol.estado == Estado.RESUELTA || sol.estado == Estado.RECHAZADA) {
```

**Después:** cada regla vive en la clase que tiene los datos.

```java
// Estado: la tabla de transiciones. El switch es exhaustivo: un estado nuevo no compila
// hasta que se definen sus transiciones.
public boolean permiteTransicionA(Estado destino) {
    return switch (this) {
        case PENDIENTE -> destino == EN_PROCESO || destino == RECHAZADA;
        case EN_PROCESO -> destino == RESUELTA || destino == RECHAZADA;
        case RESUELTA, RECHAZADA -> false;
    };
}

// Solicitud
public void cambiarEstado(Estado nuevoEstado) {
    if (!estado.permiteTransicionA(nuevoEstado)) {
        throw new ReglaDeNegocioException("transicion invalida");
    }
    if (nuevoEstado == Estado.EN_PROCESO && !tieneResponsable()) {
        throw new ReglaDeNegocioException("debe asignar responsable primero");
    }
    estado = nuevoEstado;
}

// GestorSolicitudes: solo coordina
public Solicitud cambiarEstado(int id, Estado nuevoEstado) {
    Solicitud solicitud = obtenerSolicitud(id);
    solicitud.cambiarEstado(nuevoEstado);
    return solicitud;
}
```

En el mismo commit, el formato `nombre + " (" + area + ")"` (4 copias) se movió a
`Responsable.getNombreConArea()`.

### R-06 · Introduce Parameter Object (CS-13)

**Antes:** cuatro parámetros sueltos, dos `String` contiguos que se podían intercambiar sin error de
compilación; el constructor de `Solicitud` recibía seis.

```java
public int registrarSolicitud(String legajo, String tipo, String desc, int prioridad)
public Solicitud(int id, Estudiante est, String tipo, int prioridad, String desc, String fecha)
```

**Después:**

```java
public record NuevaSolicitud(String legajo, TipoSolicitud tipo, Prioridad prioridad, String descripcion) { }

public Solicitud registrarSolicitud(NuevaSolicitud datos)
public Solicitud(int id, Estudiante estudiante, NuevaSolicitud datos, LocalDate fecha)
```

**Beneficio:** un dato nuevo de la solicitud se agrega al record sin cambiar la firma de todos los
llamadores.

### R-08 · Extract Class e inyección de dependencias (CS-02, CS-12)

**Antes:** `GestorSolicitudes` guardaba las listas públicas, cargaba los 20 estudiantes y 3
responsables en su constructor, generaba los ids, validaba, buscaba, calculaba e imprimía el reporte
y tomaba la fecha del sistema.

```java
public class GestorSolicitudes {
    public List<Solicitud> solicitudes = new ArrayList<>();
    public List<Estudiante> estudiantes = new ArrayList<>();
    public List<Responsable> responsables = new ArrayList<>();
    int contador = 1;

    public GestorSolicitudes() {
        cargarEstudiantes();
        cargarResponsables();
    }
    ...
    new Solicitud(contador, e, tipo, prioridad, desc, LocalDate.now().toString());
```

**Después:** el gestor solo tiene los casos de uso y recibe todo lo que necesita.

```java
public GestorSolicitudes(SolicitudRepositorio solicitudes, EstudianteRepositorio estudiantes,
                         ResponsableRepositorio responsables, Clock reloj)

// Main (composition root)
GestorSolicitudes gestor = new GestorSolicitudes(
        new SolicitudRepositorioEnMemoria(),
        new EstudianteRepositorioEnMemoria(DatosIniciales.estudiantes()),
        new ResponsableRepositorioEnMemoria(DatosIniciales.responsables()),
        Clock.systemDefaultZone());
```

**Beneficio:** pasar a archivo o base de datos es agregar otra implementación de repositorio (DIP); las
pruebas usan repositorios propios y un reloj fijo. Tres interfaces chicas en lugar de un repositorio
general (ISP).

**Lo que detectó la red de seguridad:** la primera versión hacía que `siguienteId()` consumiera el id
aunque la solicitud no llegara a guardarse. Se agregó el escenario E19 (golden generado sobre
`estado-inicial`), falló con `#5` en lugar de `#4`, y se corrigió antes de confirmar el commit.

## 3. Indicadores antes / después

Medidos sobre `src/main/java` con un script propio (llaves balanceadas por método) y `git grep`.

| Indicador | `estado-inicial` | `estado-refactorizado` |
|---|---|---|
| Método más largo | `Main.main()`: 162 líneas | `MenuConsola.registrarSolicitud()`: 26 líneas (sin contar `DatosIniciales.estudiantes()`, 29 líneas, que es una tabla de datos) |
| Métodos de más de 30 líneas | 4 | 0 |
| Anidamiento máximo dentro de un método | 5 niveles | 2 niveles |
| Sentencias `if` | 65 | 17 |
| Atributos `public` | 18 | 0 |
| `System.out` en el gestor (lógica de negocio) | 23 | 0 |
| Copias de la lista de tipos válidos | 3 (Main ×2, gestor) | 1 (`TipoSolicitud`) |
| Copias del formato "nombre (área)" | 4 | 1 (`Responsable.getNombreConArea`) |
| Recorridos para buscar una solicitud por id | 3 | 1 (repositorio) |
| Archivos de código fuente (clases, interfaces, enums, records) | 6 | 22 |
| Pruebas automatizadas | 0 | 88 (cobertura 96,8 % de líneas y 96,8 % de ramas) |

Hay más clases y más líneas totales (de 465 a 774 líneas de código, sin contar comentarios ni líneas
en blanco), pero cada clase es pequeña, con una sola responsabilidad, y la mayor parte del crecimiento
son interfaces, enums y métodos cortos con nombre.

## 4. Correcciones del code review (Etapa 7)

Cada observación del [checklist](../checklist.md) se corrigió en un commit propio, con
`gradlew check` en verde antes de confirmarlo. Se identifican por el ID en el mensaje del commit
(`git log --grep "CR-01"`).

| Observación | Commit | Qué se hizo | Goldens |
|---|---|---|---|
| CR-01 | `fix(CR-01)` | `Solicitud.estadosPosibles()` arma las opciones con `Estado.values()` filtrado por `permiteTransicionA()` y por la regla del responsable (`Estado.requiereResponsable()`); el menú solo muestra esa lista | E07, E08 y E18 regenerados (cambio intencional) |
| CR-02 | `refactor(CR-02)` | `Estado` tiene su etiqueta plural y el reporte recorre `Estado.values()` y `Prioridad.values()` | Sin cambios |
| CR-03 | `fix(CR-03)` | La descripción se valida con `strip()`: los espacios no cuentan para el largo mínimo | Sin cambios |
| CR-07 | `refactor(CR-07)` | Extract Method `leerPrioridad()` y constantes para los mensajes repetidos | Sin cambios |
| CR-10 | `refactor(CR-10)` | Los comentarios que contaban la historia del código explican el diseño actual | Sin cambios |
| CR-12 | `refactor(CR-12)` | `asignarResponsable()` devuelve `void`; se eliminan `Estudiante.getId()` (con su campo) y `Responsable.getArea()`; `<p>` en el Javadoc | Sin cambios |
| CR-13 | `docs(CR-13)` | Esta documentación técnica en `docs/` (pruebas, evidencias, refactorings y decisiones) | — |

CR-04 (README), CR-05 (`.idea/`) y CR-06 (`.gitattributes`) los corrigió Matías. CR-08, CR-09 y CR-11
se aceptaron como deuda: ver [ADR-006](../decisiones/ADR-006-deudas-tecnicas-aceptadas.md).
