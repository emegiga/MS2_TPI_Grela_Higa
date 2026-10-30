package tupad.ms2.repositorio;

import tupad.ms2.model.Responsable;

import java.util.List;
import java.util.Optional;

public class ResponsableRepositorioEnMemoria implements ResponsableRepositorio {

    private final List<Responsable> responsables;

    public ResponsableRepositorioEnMemoria(List<Responsable> responsables) {
        this.responsables = List.copyOf(responsables);
    }

    @Override
    public Optional<Responsable> buscarPorId(int id) {
        return responsables.stream()
                .filter(responsable -> responsable.getId() == id)
                .findFirst();
    }

    @Override
    public List<Responsable> listarTodos() {
        return responsables;
    }
}
