package tupad.ms2.model;

import tupad.ms2.model.enums.Prioridad;
import tupad.ms2.model.enums.TipoSolicitud;

/**
 * Datos que ingresa el usuario para registrar una solicitud (Parameter Object).
 * Legajo y descripción son ambos String: como parámetros sueltos serían fáciles de
 * intercambiar por error; con nombre, cada dato queda identificado.
 */
public record NuevaSolicitud(String legajo, TipoSolicitud tipo, Prioridad prioridad, String descripcion) {
}
