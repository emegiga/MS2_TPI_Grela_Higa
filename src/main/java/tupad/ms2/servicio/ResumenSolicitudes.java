package tupad.ms2.servicio;

import tupad.ms2.model.Solicitud;
import tupad.ms2.model.enums.Estado;
import tupad.ms2.model.enums.Prioridad;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * Datos del reporte: las solicitudes y sus totales por estado y por prioridad.
 * Solo calcula; cómo se muestra lo decide la capa de presentación.
 */
public final class ResumenSolicitudes {

    private final List<Solicitud> solicitudes;
    private final Map<Estado, Integer> totalPorEstado = new EnumMap<>(Estado.class);
    private final Map<Prioridad, Integer> totalPorPrioridad = new EnumMap<>(Prioridad.class);

    public ResumenSolicitudes(List<Solicitud> solicitudes) {
        this.solicitudes = List.copyOf(solicitudes);
        for (Estado estado : Estado.values()) {
            totalPorEstado.put(estado, 0);
        }
        for (Prioridad prioridad : Prioridad.values()) {
            totalPorPrioridad.put(prioridad, 0);
        }
        for (Solicitud solicitud : this.solicitudes) {
            totalPorEstado.merge(solicitud.getEstado(), 1, Integer::sum);
            totalPorPrioridad.merge(solicitud.getPrioridad(), 1, Integer::sum);
        }
    }

    public List<Solicitud> getSolicitudes() {
        return solicitudes;
    }

    public int getTotal() {
        return solicitudes.size();
    }

    public int getTotal(Estado estado) {
        return totalPorEstado.get(estado);
    }

    public int getTotal(Prioridad prioridad) {
        return totalPorPrioridad.get(prioridad);
    }
}
