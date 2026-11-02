package tupad.ms2.servicio;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tupad.ms2.model.Estudiante;
import tupad.ms2.model.NuevaSolicitud;
import tupad.ms2.model.Responsable;
import tupad.ms2.model.Solicitud;
import tupad.ms2.model.enums.Estado;
import tupad.ms2.model.enums.Prioridad;
import tupad.ms2.model.enums.TipoSolicitud;
import tupad.ms2.model.excepciones.ReglaDeNegocioException;
import tupad.ms2.repositorio.EstudianteRepositorioEnMemoria;
import tupad.ms2.repositorio.ResponsableRepositorioEnMemoria;
import tupad.ms2.repositorio.SolicitudRepositorioEnMemoria;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Pruebas del servicio con repositorios propios del test y un reloj fijo.
 * En estado-inicial esto no era posible: el gestor traía sus datos cargados, imprimía por consola
 * y tomaba la fecha del sistema.
 */
class GestorSolicitudesTest {

    private static final ZoneId ZONA = ZoneId.of("America/Argentina/Buenos_Aires");
    private static final Clock RELOJ_FIJO = Clock.fixed(Instant.parse("2026-03-15T12:00:00Z"), ZONA);

    private static final Estudiante ANA = new Estudiante(1, "Ana Prueba", "L0001", "ana@ejemplo.com");
    private static final Estudiante BRUNO = new Estudiante(2, "Bruno Prueba", "L0002", "bruno@ejemplo.com");
    private static final Responsable ALUMNADO = new Responsable(10, "Resp Alumnado", "Alumnado");

    private GestorSolicitudes gestor;

    @BeforeEach
    void crearGestorVacio() {
        gestor = new GestorSolicitudes(
                new SolicitudRepositorioEnMemoria(),
                new EstudianteRepositorioEnMemoria(List.of(ANA, BRUNO)),
                new ResponsableRepositorioEnMemoria(List.of(ALUMNADO)),
                RELOJ_FIJO);
    }

    private Solicitud registrar(String legajo, TipoSolicitud tipo, Prioridad prioridad) {
        return gestor.registrarSolicitud(new NuevaSolicitud(legajo, tipo, prioridad, "Descripcion de prueba"));
    }

    @Test
    @DisplayName("Registrar asigna ids correlativos, la fecha del reloj y el estudiante del legajo")
    void registrar() {
        Solicitud primera = registrar("L0001", TipoSolicitud.CONSTANCIA, Prioridad.ALTA);
        Solicitud segunda = registrar("L0002", TipoSolicitud.RECLAMO, Prioridad.BAJA);

        assertEquals(1, primera.getId());
        assertEquals(2, segunda.getId());
        assertEquals(LocalDate.of(2026, 3, 15), primera.getFecha());
        assertSame(ANA, primera.getEstudiante());
        assertEquals(List.of(primera, segunda), gestor.getSolicitudes());
    }

    @Test
    @DisplayName("Un legajo inexistente se rechaza")
    void estudianteInexistente() {
        ReglaDeNegocioException ex = assertThrows(ReglaDeNegocioException.class,
                () -> registrar("L9999", TipoSolicitud.OTRO, Prioridad.MEDIA));
        assertEquals("estudiante no encontrado", ex.getMessage());
        assertTrue(gestor.getSolicitudes().isEmpty());
    }

    @Test
    @DisplayName("Un registro rechazado no consume id (regla del sistema base, escenario E19)")
    void registroFallidoNoConsumeId() {
        assertThrows(ReglaDeNegocioException.class,
                () -> gestor.registrarSolicitud(new NuevaSolicitud("L0001", TipoSolicitud.OTRO, Prioridad.MEDIA, "abc")));

        assertEquals(1, registrar("L0001", TipoSolicitud.OTRO, Prioridad.MEDIA).getId());
    }

    @Test
    @DisplayName("Asignar responsable y avanzar el estado hasta RESUELTA")
    void asignarYCambiarEstado() {
        Solicitud solicitud = registrar("L0001", TipoSolicitud.RECLAMO, Prioridad.ALTA);

        gestor.asignarResponsable(solicitud.getId(), ALUMNADO.getId());
        gestor.cambiarEstado(solicitud.getId(), Estado.EN_PROCESO);
        Solicitud resuelta = gestor.cambiarEstado(solicitud.getId(), Estado.RESUELTA);

        assertSame(ALUMNADO, resuelta.getResponsable());
        assertEquals(Estado.RESUELTA, resuelta.getEstado());
    }

    @Test
    @DisplayName("Operar sobre una solicitud inexistente informa 'solicitud no encontrada'")
    void solicitudInexistente() {
        ReglaDeNegocioException alCambiar = assertThrows(ReglaDeNegocioException.class,
                () -> gestor.cambiarEstado(42, Estado.RECHAZADA));
        ReglaDeNegocioException alAsignar = assertThrows(ReglaDeNegocioException.class,
                () -> gestor.asignarResponsable(42, ALUMNADO.getId()));

        assertEquals("solicitud no encontrada", alCambiar.getMessage());
        assertEquals("solicitud no encontrada", alAsignar.getMessage());
        assertTrue(gestor.buscarSolicitudPorId(42).isEmpty());
    }

    @Test
    @DisplayName("Asignar un responsable inexistente se rechaza")
    void responsableInexistente() {
        Solicitud solicitud = registrar("L0001", TipoSolicitud.RECLAMO, Prioridad.ALTA);

        ReglaDeNegocioException ex = assertThrows(ReglaDeNegocioException.class,
                () -> gestor.asignarResponsable(solicitud.getId(), 99));
        assertEquals("responsable no encontrado", ex.getMessage());
    }

    @Test
    @DisplayName("Buscar aplica el filtro recibido (estado, tipo, legajo y prioridad)")
    void buscarConFiltros() {
        Solicitud constanciaAna = registrar("L0001", TipoSolicitud.CONSTANCIA, Prioridad.ALTA);
        Solicitud reclamoAna = registrar("L0001", TipoSolicitud.RECLAMO, Prioridad.BAJA);
        Solicitud reclamoBruno = registrar("L0002", TipoSolicitud.RECLAMO, Prioridad.ALTA);
        gestor.cambiarEstado(reclamoBruno.getId(), Estado.RECHAZADA);

        assertEquals(List.of(constanciaAna, reclamoAna), gestor.buscar(FiltrosSolicitud.porEstado(Estado.PENDIENTE)));
        assertEquals(List.of(reclamoAna, reclamoBruno), gestor.buscar(FiltrosSolicitud.porTipo(TipoSolicitud.RECLAMO)));
        assertEquals(List.of(constanciaAna, reclamoAna), gestor.buscar(FiltrosSolicitud.porLegajo("L0001")));
        assertEquals(List.of(constanciaAna, reclamoBruno), gestor.buscar(FiltrosSolicitud.porPrioridad(Prioridad.ALTA)));
        assertTrue(gestor.buscar(FiltrosSolicitud.porTipo(TipoSolicitud.EQUIVALENCIA)).isEmpty());
    }

    @Test
    @DisplayName("El reporte totaliza por estado y por prioridad")
    void reporte() {
        registrar("L0001", TipoSolicitud.CONSTANCIA, Prioridad.ALTA);
        Solicitud enProceso = registrar("L0001", TipoSolicitud.RECLAMO, Prioridad.ALTA);
        registrar("L0002", TipoSolicitud.OTRO, Prioridad.BAJA);
        gestor.asignarResponsable(enProceso.getId(), ALUMNADO.getId());
        gestor.cambiarEstado(enProceso.getId(), Estado.EN_PROCESO);

        ResumenSolicitudes resumen = gestor.generarReporte();

        assertEquals(3, resumen.getTotal());
        assertEquals(2, resumen.getTotal(Estado.PENDIENTE));
        assertEquals(1, resumen.getTotal(Estado.EN_PROCESO));
        assertEquals(0, resumen.getTotal(Estado.RESUELTA));
        assertEquals(0, resumen.getTotal(Estado.RECHAZADA));
        assertEquals(2, resumen.getTotal(Prioridad.ALTA));
        assertEquals(0, resumen.getTotal(Prioridad.MEDIA));
        assertEquals(1, resumen.getTotal(Prioridad.BAJA));
    }

    @Test
    @DisplayName("Sin solicitudes el reporte devuelve todos los totales en cero")
    void reporteVacio() {
        ResumenSolicitudes resumen = gestor.generarReporte();

        assertEquals(0, resumen.getTotal());
        for (Estado estado : Estado.values()) {
            assertEquals(0, resumen.getTotal(estado));
        }
        for (Prioridad prioridad : Prioridad.values()) {
            assertEquals(0, resumen.getTotal(prioridad));
        }
    }
}
