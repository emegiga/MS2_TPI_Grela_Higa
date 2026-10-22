package tupad.ms2.model;

import tupad.ms2.model.enums.Estado;

public class Solicitud {
    public int id;
    public Estudiante est;
    public Responsable resp;
    public String tipo;        // CONSTANCIA, EQUIVALENCIA, RECLAMO, OTRO
    public int prioridad;      // 1 = alta, 2 = media, 3 = baja
    public Estado estado;
    public String desc;
    public String fecha;

    public Solicitud(int id, Estudiante est, String tipo, int prioridad, String desc, String fecha) {
        this.id = id;
        this.est = est;
        this.tipo = tipo;
        this.prioridad = prioridad;
        this.desc = desc;
        this.fecha = fecha;
        this.estado = Estado.PENDIENTE;
    }
}
