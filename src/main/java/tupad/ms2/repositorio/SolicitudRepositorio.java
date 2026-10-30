package tupad.ms2.repositorio;

import tupad.ms2.model.Solicitud;

import java.util.List;
import java.util.Optional;

/**
 * Abstracción de la persistencia de solicitudes. El gestor depende de esta interfaz y no de
 * una lista concreta, por lo que cambiar a archivo o base de datos no obliga a modificarlo (DIP).
 */
public interface SolicitudRepositorio {

    /** Id que recibirá la próxima solicitud guardada. Consultarlo no lo reserva. */
    int siguienteId();

    void guardar(Solicitud solicitud);

    Optional<Solicitud> buscarPorId(int id);

    List<Solicitud> listarTodas();
}
