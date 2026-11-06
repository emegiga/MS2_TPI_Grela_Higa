package tupad.ms2.servicio;

import tupad.ms2.model.Estudiante;
import tupad.ms2.model.NuevaSolicitud;
import tupad.ms2.model.Responsable;
import tupad.ms2.model.Solicitud;
import tupad.ms2.model.enums.Estado;
import tupad.ms2.model.excepciones.ReglaDeNegocioException;
import tupad.ms2.repositorio.EstudianteRepositorio;
import tupad.ms2.repositorio.ResponsableRepositorio;
import tupad.ms2.repositorio.SolicitudRepositorio;

import java.time.Clock;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

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

    // Asignar responsable (del listado) a solicitud
    public void asignarResponsable(int id, int idResponsable) {
        Solicitud solicitud = obtenerSolicitud(id);
        Responsable responsable = responsables.buscarPorId(idResponsable)
                .orElseThrow(() -> new ReglaDeNegocioException("responsable no encontrado"));
        solicitud.asignarResponsable(responsable);
    }

    // Buscar / Filtrar: el criterio llega como un filtro ya armado (ver FiltrosSolicitud)
    public List<Solicitud> buscar(Predicate<Solicitud> filtro) {
        return solicitudes.listarTodas().stream()
                .filter(filtro)
                .toList();
    }

    // Reporte: devuelve los datos calculados, sin imprimir nada
    public ResumenSolicitudes generarReporte() {
        return new ResumenSolicitudes(solicitudes.listarTodas());
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
