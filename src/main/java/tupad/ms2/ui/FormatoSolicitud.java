package tupad.ms2.ui;

import tupad.ms2.model.Estudiante;
import tupad.ms2.model.Solicitud;

/**
 * Formatos de texto con los que la consola muestra estudiantes y solicitudes.
 * Cada pantalla tiene su formato; todos se definen en un único lugar.
 */
final class FormatoSolicitud {

    private static final String SIN_RESPONSABLE = "SIN ASIGNAR";

    private FormatoSolicitud() {
    }

    // Opción del listado de estudiantes al registrar: "L1001 - Agustin Di Maria (agustin87@gmail.com)"
    static String estudianteConEmail(Estudiante estudiante) {
        return estudiante.getLegajo() + " - " + estudiante.getNombre() + " (" + estudiante.getEmail() + ")";
    }

    // Opción del listado de estudiantes al buscar por legajo: "L1001 - Agustin Di Maria"
    static String estudiante(Estudiante estudiante) {
        return estudiante.getLegajo() + " - " + estudiante.getNombre();
    }

    // Listado previo a elegir una solicitud (opciones 2 y 3)
    static String lineaListado(Solicitud solicitud) {
        return "#" + solicitud.getId() + " | " + solicitud.getEstudiante().getNombre() + " | " + solicitud.getTipo()
                + " | Prioridad " + solicitud.getPrioridad().getCodigo() + " | " + solicitud.getEstado()
                + " | " + responsable(solicitud);
    }

    // Resultado de una búsqueda (opción 4)
    static String lineaBusqueda(Solicitud solicitud) {
        return "#" + solicitud.getId() + " | " + solicitud.getEstudiante().getNombre() + " | " + solicitud.getTipo()
                + " | Prioridad: " + solicitud.getPrioridad().getCodigo() + " | " + solicitud.getEstado();
    }

    // Detalle del reporte (opción 5)
    static String lineaReporte(Solicitud solicitud) {
        return "#" + solicitud.getId() + " | " + solicitud.getFecha() + " | " + solicitud.getEstudiante().getNombre()
                + " | " + solicitud.getTipo() + " | Prioridad: " + solicitud.getPrioridad().getCodigo()
                + " | " + solicitud.getEstado() + " | " + responsable(solicitud);
    }

    static String responsable(Solicitud solicitud) {
        return solicitud.tieneResponsable() ? solicitud.getResponsable().getNombreConArea() : SIN_RESPONSABLE;
    }
}
