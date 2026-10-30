package tupad.ms2.repositorio;

import tupad.ms2.model.Solicitud;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Persistencia en memoria: los datos se pierden al cerrar el programa (alcance del TPI).
 */
public class SolicitudRepositorioEnMemoria implements SolicitudRepositorio {

    private final List<Solicitud> solicitudes = new ArrayList<>();
    private int proximoId = 1;

    // Consulta el próximo id sin consumirlo: si la solicitud no llega a guardarse
    // (por ejemplo, porque viola una regla), el id queda disponible.
    @Override
    public int siguienteId() {
        return proximoId;
    }

    @Override
    public void guardar(Solicitud solicitud) {
        solicitudes.add(solicitud);
        proximoId = Math.max(proximoId, solicitud.getId() + 1);
    }

    @Override
    public Optional<Solicitud> buscarPorId(int id) {
        return solicitudes.stream()
                .filter(solicitud -> solicitud.getId() == id)
                .findFirst();
    }

    @Override
    public List<Solicitud> listarTodas() {
        return Collections.unmodifiableList(solicitudes);
    }
}
