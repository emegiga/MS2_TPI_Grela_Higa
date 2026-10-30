package tupad.ms2.repositorio;

import tupad.ms2.model.Estudiante;

import java.util.List;
import java.util.Optional;

/**
 * Consulta de estudiantes. Es de solo lectura porque el alta de estudiantes está fuera del alcance.
 */
public interface EstudianteRepositorio {

    Optional<Estudiante> buscarPorLegajo(String legajo);

    List<Estudiante> listarTodos();
}
