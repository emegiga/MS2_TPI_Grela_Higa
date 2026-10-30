package tupad.ms2.servicio;

import tupad.ms2.model.Solicitud;
import tupad.ms2.model.enums.Estado;
import tupad.ms2.model.enums.Prioridad;
import tupad.ms2.model.enums.TipoSolicitud;

import java.util.function.Predicate;

/**
 * Criterios de búsqueda como objetos intercambiables (estilo Strategy). Agregar un criterio nuevo
 * (por ejemplo, por responsable) es agregar un método acá, sin modificar GestorSolicitudes.buscar().
 */
public final class FiltrosSolicitud {

    private FiltrosSolicitud() {
    }

    public static Predicate<Solicitud> porEstado(Estado estado) {
        return solicitud -> solicitud.getEstado() == estado;
    }

    public static Predicate<Solicitud> porTipo(TipoSolicitud tipo) {
        return solicitud -> solicitud.getTipo() == tipo;
    }

    public static Predicate<Solicitud> porLegajo(String legajo) {
        return solicitud -> solicitud.getEstudiante().getLegajo().equals(legajo);
    }

    public static Predicate<Solicitud> porPrioridad(Prioridad prioridad) {
        return solicitud -> solicitud.getPrioridad() == prioridad;
    }
}
