package tupad.ms2.ui;

import tupad.ms2.model.Estudiante;
import tupad.ms2.model.NuevaSolicitud;
import tupad.ms2.model.Responsable;
import tupad.ms2.model.Solicitud;
import tupad.ms2.model.enums.Estado;
import tupad.ms2.model.enums.Prioridad;
import tupad.ms2.model.enums.TipoSolicitud;
import tupad.ms2.model.excepciones.ReglaDeNegocioException;
import tupad.ms2.servicio.FiltrosSolicitud;
import tupad.ms2.servicio.GestorSolicitudes;
import tupad.ms2.servicio.ResumenSolicitudes;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Menú principal por consola. Traduce lo que elige el usuario en llamadas al GestorSolicitudes
 * y muestra el resultado. No contiene reglas de negocio.
 */
public class MenuConsola {

    private static final String TITULO =
            "######################## GESTION DE SOLICITUDES ACADEMICAS ########################";
    private static final String OPCION_SALIR = "0";

    /** Cada opción del menú es un objeto con su acción (estilo Command): agregar una es sumar una entrada. */
    private record OpcionMenu(String clave, String titulo, Runnable accion) {
    }

    /** Criterio de búsqueda: su título en el menú y cómo pedirle al usuario el valor a filtrar. */
    private record CriterioBusqueda(String titulo, Supplier<Optional<Predicate<Solicitud>>> elegirFiltro) {
    }

    private final GestorSolicitudes gestor;
    private final EntradaConsola consola;
    private final List<OpcionMenu> opciones;
    private final List<CriterioBusqueda> criteriosBusqueda;

    public MenuConsola(GestorSolicitudes gestor, EntradaConsola consola) {
        this.gestor = gestor;
        this.consola = consola;
        this.opciones = List.of(
                new OpcionMenu("1", "Registrar solicitud", this::registrarSolicitud),
                new OpcionMenu("2", "Cambiar estado", this::cambiarEstado),
                new OpcionMenu("3", "Asignar responsable", this::asignarResponsable),
                new OpcionMenu("4", "Buscar / filtrar", this::buscarSolicitudes),
                new OpcionMenu("5", "Reporte", this::mostrarReporte));
        this.criteriosBusqueda = List.of(
                new CriterioBusqueda("Estado", this::elegirFiltroEstado),
                new CriterioBusqueda("Tipo", this::elegirFiltroTipo),
                new CriterioBusqueda("Legajo", this::elegirFiltroLegajo),
                new CriterioBusqueda("Prioridad", this::elegirFiltroPrioridad));
    }

    public void iniciar() {
        String opcionElegida = mostrarMenuYLeerOpcion();
        while (!opcionElegida.equals(OPCION_SALIR)) {
            ejecutar(opcionElegida);
            opcionElegida = mostrarMenuYLeerOpcion();
        }
    }

    private String mostrarMenuYLeerOpcion() {
        consola.mostrar("");
        consola.mostrar(TITULO);
        for (OpcionMenu opcion : opciones) {
            consola.mostrar(opcion.clave() + ". " + opcion.titulo());
        }
        consola.mostrar(OPCION_SALIR + ". Salir");
        return consola.leerLinea("Opcion: ");
    }

    private void ejecutar(String clave) {
        opciones.stream()
                .filter(opcion -> opcion.clave().equals(clave))
                .findFirst()
                .ifPresentOrElse(opcion -> opcion.accion().run(), () -> consola.mostrar("Opcion invalida"));
    }

    // ---------- 1. Registrar solicitud ----------

    private void registrarSolicitud() {
        List<Estudiante> estudiantes = gestor.getEstudiantes();
        if (estudiantes.isEmpty()) {
            consola.mostrar("No hay estudiantes cargados.");
            return;
        }
        Optional<Estudiante> estudiante = consola.elegir("Estudiante:", estudiantes, FormatoSolicitud::estudianteConEmail);
        if (estudiante.isEmpty()) {
            consola.mostrar("Registro cancelado.");
            return;
        }
        Optional<TipoSolicitud> tipo = consola.elegir("Tipo:", List.of(TipoSolicitud.values()), Enum::name);
        if (tipo.isEmpty()) {
            consola.mostrar("Registro cancelado.");
            return;
        }
        String descripcion = consola.leerLinea("Descripcion: ");
        int codigoPrioridad = Integer.parseInt(consola.leerLinea("Prioridad (1=alta, 2=media, 3=baja): "));
        try {
            Prioridad prioridad = Prioridad.desdeCodigo(codigoPrioridad);
            NuevaSolicitud datos = new NuevaSolicitud(estudiante.get().getLegajo(), tipo.get(), prioridad, descripcion);
            Solicitud registrada = gestor.registrarSolicitud(datos);
            consola.mostrar("Solicitud registrada con id " + registrada.getId());
        } catch (ReglaDeNegocioException ex) {
            mostrarError(ex);
        }
    }

    // ---------- 2. Cambiar estado ----------

    private void cambiarEstado() {
        Optional<Solicitud> elegida = elegirSolicitudAbierta();
        if (elegida.isEmpty()) {
            return;
        }
        Solicitud solicitud = elegida.get();
        List<Estado> estadosPosibles;
        if (solicitud.tieneResponsable()) {
            estadosPosibles = List.of(Estado.EN_PROCESO, Estado.RESUELTA, Estado.RECHAZADA);
        } else {
            consola.mostrar("La solicitud #" + solicitud.getId() + " no tiene responsable asignado. "
                    + "Solo puede rechazarse; para otros estados asignelo desde la opcion 3.");
            estadosPosibles = List.of(Estado.RECHAZADA);
        }
        consola.elegir("Nuevo estado:", estadosPosibles, Enum::name).ifPresent(nuevoEstado -> {
            try {
                Solicitud actualizada = gestor.cambiarEstado(solicitud.getId(), nuevoEstado);
                consola.mostrar("Estado actualizado a " + actualizada.getEstado());
            } catch (ReglaDeNegocioException ex) {
                mostrarError(ex);
            }
        });
    }

    // ---------- 3. Asignar responsable ----------

    private void asignarResponsable() {
        Optional<Solicitud> elegida = elegirSolicitudAbierta();
        if (elegida.isEmpty()) {
            return;
        }
        Solicitud solicitud = elegida.get();
        if (solicitud.tieneResponsable()) {
            consola.mostrar("Responsable actual: " + solicitud.getResponsable().getNombreConArea()
                    + ". Si elegis otro, se reemplaza.");
        }
        consola.elegir("Responsable:", gestor.getResponsables(), Responsable::getNombreConArea).ifPresent(responsable -> {
            try {
                gestor.asignarResponsable(solicitud.getId(), responsable.getId());
                consola.mostrar("Responsable asignado: " + responsable.getNombre());
            } catch (ReglaDeNegocioException ex) {
                mostrarError(ex);
            }
        });
    }

    // Lista las solicitudes, pide un id y devuelve la solicitud si existe y no está cerrada.
    // En cualquier otro caso informa el motivo y devuelve vacío.
    private Optional<Solicitud> elegirSolicitudAbierta() {
        List<Solicitud> solicitudes = gestor.getSolicitudes();
        if (solicitudes.isEmpty()) {
            consola.mostrar("No hay solicitudes cargadas.");
            return Optional.empty();
        }
        consola.mostrar("--- Solicitudes ---");
        solicitudes.forEach(solicitud -> consola.mostrar(FormatoSolicitud.lineaListado(solicitud)));

        Optional<Solicitud> solicitud = EntradaConsola.convertirANumero(consola.leerLinea("Id solicitud: ").trim())
                .flatMap(gestor::buscarSolicitudPorId);
        if (solicitud.isEmpty()) {
            consola.mostrar("Error: solicitud no encontrada");
            return Optional.empty();
        }
        if (solicitud.get().estaCerrada()) {
            consola.mostrar("Error: la solicitud esta cerrada");
            return Optional.empty();
        }
        return solicitud;
    }

    // ---------- 4. Buscar / filtrar ----------

    private void buscarSolicitudes() {
        consola.elegir("Buscar por:", criteriosBusqueda, CriterioBusqueda::titulo)
                .flatMap(criterio -> criterio.elegirFiltro().get())
                .ifPresent(filtro -> mostrarResultados(gestor.buscar(filtro)));
    }

    private Optional<Predicate<Solicitud>> elegirFiltroEstado() {
        return consola.elegir("Estado:", List.of(Estado.values()), Enum::name)
                .map(FiltrosSolicitud::porEstado);
    }

    private Optional<Predicate<Solicitud>> elegirFiltroTipo() {
        return consola.elegir("Tipo:", List.of(TipoSolicitud.values()), Enum::name)
                .map(FiltrosSolicitud::porTipo);
    }

    private Optional<Predicate<Solicitud>> elegirFiltroLegajo() {
        List<Estudiante> estudiantes = gestor.getEstudiantes();
        if (estudiantes.isEmpty()) {
            consola.mostrar("No hay estudiantes cargados.");
            return Optional.empty();
        }
        return consola.elegir("Estudiante:", estudiantes, FormatoSolicitud::estudiante)
                .map(estudiante -> FiltrosSolicitud.porLegajo(estudiante.getLegajo()));
    }

    private Optional<Predicate<Solicitud>> elegirFiltroPrioridad() {
        return consola.elegir("Prioridad:", List.of(Prioridad.values()), Prioridad::getEtiqueta)
                .map(FiltrosSolicitud::porPrioridad);
    }

    private void mostrarResultados(List<Solicitud> resultados) {
        resultados.forEach(solicitud -> consola.mostrar(FormatoSolicitud.lineaBusqueda(solicitud)));
        consola.mostrar("Resultados: " + resultados.size());
    }

    // ---------- 5. Reporte ----------

    private void mostrarReporte() {
        ResumenSolicitudes resumen = gestor.generarReporte();
        consola.mostrar("===== REPORTE DE SOLICITUDES =====");
        resumen.getSolicitudes().forEach(solicitud -> consola.mostrar(FormatoSolicitud.lineaReporte(solicitud)));
        consola.mostrar("-----------------------------------");
        consola.mostrar("Total: " + resumen.getTotal());
        consola.mostrar("Pendientes: " + resumen.getTotal(Estado.PENDIENTE)
                + " | En proceso: " + resumen.getTotal(Estado.EN_PROCESO)
                + " | Resueltas: " + resumen.getTotal(Estado.RESUELTA)
                + " | Rechazadas: " + resumen.getTotal(Estado.RECHAZADA));
        consola.mostrar("Alta: " + resumen.getTotal(Prioridad.ALTA)
                + " | Media: " + resumen.getTotal(Prioridad.MEDIA)
                + " | Baja: " + resumen.getTotal(Prioridad.BAJA));
    }

    private void mostrarError(ReglaDeNegocioException ex) {
        consola.mostrar("Error: " + ex.getMessage());
    }
}
