package tupad.ms2.model;

import tupad.ms2.model.enums.Estado;
import tupad.ms2.model.enums.Prioridad;
import tupad.ms2.model.enums.TipoSolicitud;
import tupad.ms2.model.excepciones.ReglaDeNegocioException;

public class Solicitud {
    public int id;
    public Estudiante estudiante;
    public Responsable responsable;
    public TipoSolicitud tipo;
    public Prioridad prioridad;
    public Estado estado;
    public String descripcion;
    public String fecha;

    public Solicitud(int id, Estudiante estudiante, TipoSolicitud tipo, Prioridad prioridad, String descripcion, String fecha) {
        this.id = id;
        this.estudiante = estudiante;
        this.tipo = tipo;
        this.prioridad = prioridad;
        this.descripcion = descripcion;
        this.fecha = fecha;
        this.estado = Estado.PENDIENTE;
    }

    public boolean estaCerrada() {
        return estado.esCerrado();
    }

    public boolean tieneResponsable() {
        return responsable != null;
    }

    public void cambiarEstado(Estado nuevoEstado) {
        if (!estado.permiteTransicionA(nuevoEstado)) {
            throw new ReglaDeNegocioException("transicion invalida");
        }
        if (nuevoEstado == Estado.EN_PROCESO && !tieneResponsable()) {
            throw new ReglaDeNegocioException("debe asignar responsable primero");
        }
        estado = nuevoEstado;
    }

    public void asignarResponsable(Responsable nuevoResponsable) {
        if (estaCerrada()) {
            throw new ReglaDeNegocioException("la solicitud esta cerrada");
        }
        responsable = nuevoResponsable;
    }
}
