package tupad.ms2.model;

import tupad.ms2.model.enums.Estado;

public class Solicitud {
    public int id;
    public Estudiante estudiante;
    public Responsable responsable;
    public String tipo;        // CONSTANCIA, EQUIVALENCIA, RECLAMO, OTRO
    public int prioridad;      // 1 = alta, 2 = media, 3 = baja
    public Estado estado;
    public String descripcion;
    public String fecha;

    public Solicitud(int id, Estudiante estudiante, String tipo, int prioridad, String descripcion, String fecha) {
        this.id = id;
        this.estudiante = estudiante;
        this.tipo = tipo;
        this.prioridad = prioridad;
        this.descripcion = descripcion;
        this.fecha = fecha;
        this.estado = Estado.PENDIENTE;
    }
}
