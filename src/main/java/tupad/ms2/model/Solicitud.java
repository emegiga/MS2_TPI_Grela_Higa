package tupad.ms2.model;

import tupad.ms2.model.enums.Estado;
import tupad.ms2.model.enums.Prioridad;
import tupad.ms2.model.enums.TipoSolicitud;
import tupad.ms2.model.excepciones.ReglaDeNegocioException;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

/**
 * Pedido académico de un estudiante. Los datos de alta son inmutables; el estado y el
 * responsable solo cambian a través de métodos que validan las reglas del dominio.
 */
public class Solicitud {
    private static final int LARGO_MINIMO_DESCRIPCION = 5;

    private final int id;
    private final Estudiante estudiante;
    private final TipoSolicitud tipo;
    private final Prioridad prioridad;
    private final String descripcion;
    private final LocalDate fecha;
    private Responsable responsable;
    private Estado estado;

    public Solicitud(int id, Estudiante estudiante, NuevaSolicitud datos, LocalDate fecha) {
        // Los espacios no cuentan: "     " no es una descripción
        if (datos.descripcion() == null || datos.descripcion().strip().length() < LARGO_MINIMO_DESCRIPCION) {
            throw new ReglaDeNegocioException("descripcion muy corta");
        }
        this.id = id;
        this.estudiante = estudiante;
        this.tipo = datos.tipo();
        this.prioridad = datos.prioridad();
        this.descripcion = datos.descripcion();
        this.fecha = fecha;
        this.estado = Estado.PENDIENTE;
    }

    public boolean estaCerrada() {
        return estado.esCerrado();
    }

    public boolean tieneResponsable() {
        return responsable != null;
    }

    /**
     * Estados a los que puede pasar ahora, según el ciclo de vida y si tiene responsable.
     * La interfaz ofrece solo estos, así no muestra opciones que el dominio después rechaza.
     */
    public List<Estado> estadosPosibles() {
        return Arrays.stream(Estado.values())
                .filter(destino -> estado.permiteTransicionA(destino))
                .filter(destino -> !destino.requiereResponsable() || tieneResponsable())
                .toList();
    }

    public void cambiarEstado(Estado nuevoEstado) {
        if (!estado.permiteTransicionA(nuevoEstado)) {
            throw new ReglaDeNegocioException("transicion invalida");
        }
        if (nuevoEstado.requiereResponsable() && !tieneResponsable()) {
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

    public int getId() {
        return id;
    }

    public Estudiante getEstudiante() {
        return estudiante;
    }

    public TipoSolicitud getTipo() {
        return tipo;
    }

    public Prioridad getPrioridad() {
        return prioridad;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public Responsable getResponsable() {
        return responsable;
    }

    public Estado getEstado() {
        return estado;
    }
}
