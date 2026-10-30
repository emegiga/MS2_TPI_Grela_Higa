package tupad.ms2.servicio;

import tupad.ms2.model.Estudiante;
import tupad.ms2.model.NuevaSolicitud;
import tupad.ms2.model.Responsable;
import tupad.ms2.model.Solicitud;
import tupad.ms2.model.enums.Estado;
import tupad.ms2.model.enums.Prioridad;
import tupad.ms2.model.excepciones.ReglaDeNegocioException;
import tupad.ms2.repositorio.EstudianteRepositorio;
import tupad.ms2.repositorio.ResponsableRepositorio;
import tupad.ms2.repositorio.SolicitudRepositorio;

import java.time.Clock;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Casos de uso del sistema. Coordina repositorios y entidades; las reglas viven en el dominio.
 * Todas sus dependencias se reciben por constructor (inyección de dependencias), por lo que en
 * las pruebas se pueden usar repositorios vacíos y un reloj fijo.
 */
public class GestorSolicitudes {

    private final SolicitudRepositorio solicitudes;
    private final EstudianteRepositorio estudiantes;
    private final ResponsableRepositorio responsables;
    private final Clock reloj;

    public GestorSolicitudes(SolicitudRepositorio solicitudes, EstudianteRepositorio estudiantes,
                             ResponsableRepositorio responsables, Clock reloj) {
        this.solicitudes = solicitudes;
        this.estudiantes = estudiantes;
        this.responsables = responsables;
        this.reloj = reloj;
    }

    // Registrar solicitud. Devuelve la solicitud creada.
    public Solicitud registrarSolicitud(NuevaSolicitud datos) {
        Estudiante estudiante = estudiantes.buscarPorLegajo(datos.legajo())
                .orElseThrow(() -> new ReglaDeNegocioException("estudiante no encontrado"));
        Solicitud solicitud = new Solicitud(solicitudes.siguienteId(), estudiante, datos, LocalDate.now(reloj));
        solicitudes.guardar(solicitud);
        return solicitud;
    }

    // Cambiar estado de solicitud. Devuelve la solicitud actualizada.
    public Solicitud cambiarEstado(int id, Estado nuevoEstado) {
        Solicitud solicitud = obtenerSolicitud(id);
        solicitud.cambiarEstado(nuevoEstado);
        return solicitud;
    }

    // Asignar responsable (del listado) a solicitud. Devuelve la solicitud actualizada.
    public Solicitud asignarResponsable(int id, int idResponsable) {
        Solicitud solicitud = obtenerSolicitud(id);
        Responsable responsable = responsables.buscarPorId(idResponsable)
                .orElseThrow(() -> new ReglaDeNegocioException("responsable no encontrado"));
        solicitud.asignarResponsable(responsable);
        return solicitud;
    }

    // Buscar / Filtrar
    public List<Solicitud> buscar(String criterio, String valor) {
        List<Solicitud> resultado = new ArrayList<>();
        if (criterio.equals("estado")) {
            for (Solicitud solicitud : solicitudes.listarTodas()) {
                if (solicitud.getEstado().toString().equalsIgnoreCase(valor)) {
                    resultado.add(solicitud);
                }
            }
        } else if (criterio.equals("tipo")) {
            for (Solicitud solicitud : solicitudes.listarTodas()) {
                if (solicitud.getTipo().name().equalsIgnoreCase(valor)) {
                    resultado.add(solicitud);
                }
            }
        } else if (criterio.equals("legajo")) {
            for (Solicitud solicitud : solicitudes.listarTodas()) {
                if (solicitud.getEstudiante().getLegajo().equals(valor)) {
                    resultado.add(solicitud);
                }
            }
        } else if (criterio.equals("prioridad")) {
            for (Solicitud solicitud : solicitudes.listarTodas()) {
                if (String.valueOf(solicitud.getPrioridad().getCodigo()).equals(valor)) {
                    resultado.add(solicitud);
                }
            }
        } else {
            throw new IllegalArgumentException("criterio no soportado: " + criterio);
        }
        return resultado;
    }

    // Reporte
    public void reporte() {
        List<Solicitud> todas = solicitudes.listarTodas();
        int pendientes = 0, enProceso = 0, resueltas = 0, rechazadas = 0;
        int alta = 0, media = 0, baja = 0;
        System.out.println("===== REPORTE DE SOLICITUDES =====");
        for (Solicitud solicitud : todas) {
            String responsable = solicitud.tieneResponsable() ? solicitud.getResponsable().getNombreConArea() : "SIN ASIGNAR";
            System.out.println("#" + solicitud.getId() + " | " + solicitud.getFecha() + " | " + solicitud.getEstudiante().getNombre()
                    + " | " + solicitud.getTipo() + " | Prioridad: " + solicitud.getPrioridad().getCodigo() + " | " + solicitud.getEstado()
                    + " | " + responsable);
            if (solicitud.getEstado() == Estado.PENDIENTE) pendientes++;
            if (solicitud.getEstado() == Estado.EN_PROCESO) enProceso++;
            if (solicitud.getEstado() == Estado.RESUELTA) resueltas++;
            if (solicitud.getEstado() == Estado.RECHAZADA) rechazadas++;
            if (solicitud.getPrioridad() == Prioridad.ALTA) alta++;
            if (solicitud.getPrioridad() == Prioridad.MEDIA) media++;
            if (solicitud.getPrioridad() == Prioridad.BAJA) baja++;
        }
        System.out.println("-----------------------------------");
        System.out.println("Total: " + todas.size());
        System.out.println("Pendientes: " + pendientes + " | En proceso: " + enProceso
                + " | Resueltas: " + resueltas + " | Rechazadas: " + rechazadas);
        System.out.println("Alta: " + alta + " | Media: " + media + " | Baja: " + baja);
    }

    public List<Solicitud> getSolicitudes() {
        return solicitudes.listarTodas();
    }

    public List<Estudiante> getEstudiantes() {
        return estudiantes.listarTodos();
    }

    public List<Responsable> getResponsables() {
        return responsables.listarTodos();
    }

    public Optional<Solicitud> buscarSolicitudPorId(int id) {
        return solicitudes.buscarPorId(id);
    }

    private Solicitud obtenerSolicitud(int id) {
        return solicitudes.buscarPorId(id)
                .orElseThrow(() -> new ReglaDeNegocioException("solicitud no encontrada"));
    }
}
