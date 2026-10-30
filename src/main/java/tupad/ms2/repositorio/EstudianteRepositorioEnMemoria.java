package tupad.ms2.repositorio;

import tupad.ms2.model.Estudiante;

import java.util.List;
import java.util.Optional;

public class EstudianteRepositorioEnMemoria implements EstudianteRepositorio {

    private final List<Estudiante> estudiantes;

    public EstudianteRepositorioEnMemoria(List<Estudiante> estudiantes) {
        this.estudiantes = List.copyOf(estudiantes);
    }

    @Override
    public Optional<Estudiante> buscarPorLegajo(String legajo) {
        return estudiantes.stream()
                .filter(estudiante -> estudiante.getLegajo().equals(legajo))
                .findFirst();
    }

    @Override
    public List<Estudiante> listarTodos() {
        return estudiantes;
    }
}
