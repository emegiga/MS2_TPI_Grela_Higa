package tupad.ms2.model;

import tupad.ms2.model.enums.Prioridad;
import tupad.ms2.model.enums.TipoSolicitud;

/**
 * Datos que ingresa el usuario para registrar una solicitud (Parameter Object).
 * Agrupa en un solo objeto lo que antes viajaba como cuatro parámetros sueltos,
 * dos de ellos del mismo tipo y fáciles de intercambiar por error.
 */
public record NuevaSolicitud(String legajo, TipoSolicitud tipo, Prioridad prioridad, String descripcion) {
}
