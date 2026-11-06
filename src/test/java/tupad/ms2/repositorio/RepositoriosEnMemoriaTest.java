package tupad.ms2.repositorio;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tupad.ms2.model.Estudiante;
import tupad.ms2.model.NuevaSolicitud;
import tupad.ms2.model.Responsable;
import tupad.ms2.model.Solicitud;
import tupad.ms2.model.enums.Prioridad;
import tupad.ms2.model.enums.TipoSolicitud;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RepositoriosEnMemoriaTest {

    private static final Estudiante ESTUDIANTE = new Estudiante("Estudiante Prueba", "L0001", "e@ejemplo.com");

    private static Solicitud solicitud(int id) {
        NuevaSolicitud datos = new NuevaSolicitud("L0001", TipoSolicitud.OTRO, Prioridad.MEDIA, "Descripcion de prueba");
        return new Solicitud(id, ESTUDIANTE, datos, LocalDate.of(2026, 1, 1));
    }

    @Test
    @DisplayName("siguienteId() no reserva el id: solo avanza al guardar")
    void siguienteIdAvanzaAlGuardar() {
        SolicitudRepositorio repositorio = new SolicitudRepositorioEnMemoria();

        assertEquals(1, repositorio.siguienteId());
        assertEquals(1, repositorio.siguienteId());
        repositorio.guardar(solicitud(repositorio.siguienteId()));
        assertEquals(2, repositorio.siguienteId());
    }

    @Test
    @DisplayName("Busca solicitudes por id y lista en orden de alta")
    void buscarYListarSolicitudes() {
        SolicitudRepositorio repositorio = new SolicitudRepositorioEnMemoria();
        Solicitud primera = solicitud(1);
        Solicitud segunda = solicitud(2);
        repositorio.guardar(primera);
        repositorio.guardar(segunda);

        assertSame(segunda, repositorio.buscarPorId(2).orElseThrow());
        assertTrue(repositorio.buscarPorId(3).isEmpty());
        assertEquals(List.of(primera, segunda), repositorio.listarTodas());
    }

    @Test
    @DisplayName("El listado de solicitudes es de solo lectura")
    void listadoDeSoloLectura() {
        SolicitudRepositorio repositorio = new SolicitudRepositorioEnMemoria();
        assertThrows(UnsupportedOperationException.class, () -> repositorio.listarTodas().add(solicitud(1)));
    }

    @Test
    @DisplayName("Estudiantes: busca por legajo y no se ve afectado si cambia la lista original")
    void estudiantes() {
        List<Estudiante> origen = new ArrayList<>(List.of(ESTUDIANTE));
        EstudianteRepositorio repositorio = new EstudianteRepositorioEnMemoria(origen);
        origen.clear();

        assertSame(ESTUDIANTE, repositorio.buscarPorLegajo("L0001").orElseThrow());
        assertTrue(repositorio.buscarPorLegajo("L0002").isEmpty());
        assertEquals(1, repositorio.listarTodos().size());
    }

    @Test
    @DisplayName("Responsables: busca por id")
    void responsables() {
        Responsable responsable = new Responsable(5, "Resp Prueba", "Alumnado");
        ResponsableRepositorio repositorio = new ResponsableRepositorioEnMemoria(List.of(responsable));

        assertSame(responsable, repositorio.buscarPorId(5).orElseThrow());
        assertTrue(repositorio.buscarPorId(1).isEmpty());
        assertEquals("Resp Prueba (Alumnado)", responsable.getNombreConArea());
    }
}
