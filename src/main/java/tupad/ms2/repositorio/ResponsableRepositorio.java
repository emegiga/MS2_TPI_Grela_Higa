package tupad.ms2.repositorio;

import tupad.ms2.model.Responsable;

import java.util.List;
import java.util.Optional;

/**
 * Consulta de responsables. Es de solo lectura porque el alta de responsables está fuera del alcance.
 */
public interface ResponsableRepositorio {

    Optional<Responsable> buscarPorId(int id);

    List<Responsable> listarTodos();
}
