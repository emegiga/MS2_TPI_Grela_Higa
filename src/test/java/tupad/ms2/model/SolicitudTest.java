package tupad.ms2.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import tupad.ms2.model.enums.Estado;
import tupad.ms2.model.enums.Prioridad;
import tupad.ms2.model.enums.TipoSolicitud;
import tupad.ms2.model.excepciones.ReglaDeNegocioException;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SolicitudTest {

    private static final LocalDate FECHA = LocalDate.of(2026, 10, 28);
    private static final Estudiante ESTUDIANTE = new Estudiante(1, "Estudiante Prueba", "L9001", "prueba@ejemplo.com");
    private static final Responsable RESPONSABLE = new Responsable(1, "Responsable Prueba", "Alumnado");
    private static final Responsable OTRO_RESPONSABLE = new Responsable(2, "Otro Responsable", "Secretaria Academica");

    private Solicitud solicitud;

    @BeforeEach
    void crearSolicitudPendiente() {
        solicitud = nuevaSolicitud("Constancia de alumno regular");
    }

    private static Solicitud nuevaSolicitud(String descripcion) {
        NuevaSolicitud datos = new NuevaSolicitud("L9001", TipoSolicitud.CONSTANCIA, Prioridad.MEDIA, descripcion);
        return new Solicitud(7, ESTUDIANTE, datos, FECHA);
    }

    @Nested
    @DisplayName("Alta de la solicitud")
    class Alta {

        @Test
        @DisplayName("Nace PENDIENTE, sin responsable y con los datos ingresados")
        void estadoInicial() {
            assertEquals(7, solicitud.getId());
            assertSame(ESTUDIANTE, solicitud.getEstudiante());
            assertEquals(TipoSolicitud.CONSTANCIA, solicitud.getTipo());
            assertEquals(Prioridad.MEDIA, solicitud.getPrioridad());
            assertEquals("Constancia de alumno regular", solicitud.getDescripcion());
            assertEquals(FECHA, solicitud.getFecha());
            assertEquals(Estado.PENDIENTE, solicitud.getEstado());
            assertFalse(solicitud.tieneResponsable());
            assertNull(solicitud.getResponsable());
            assertFalse(solicitud.estaCerrada());
        }

        @ParameterizedTest(name = "descripción \"{0}\"")
        @DisplayName("Rechaza descripciones de menos de 5 caracteres o nulas")
        @NullSource
        @ValueSource(strings = {"", "abc", "abcd"})
        void descripcionMuyCorta(String descripcion) {
            ReglaDeNegocioException ex = assertThrows(ReglaDeNegocioException.class, () -> nuevaSolicitud(descripcion));
            assertEquals("descripcion muy corta", ex.getMessage());
        }

        @Test
        @DisplayName("Acepta una descripción de exactamente 5 caracteres (valor límite)")
        void descripcionEnElLimite() {
            assertDoesNotThrow(() -> nuevaSolicitud("abcde"));
        }
    }

    @Nested
    @DisplayName("Cambio de estado")
    class CambioDeEstado {

        @Test
        @DisplayName("No puede pasar a EN_PROCESO sin responsable")
        void enProcesoExigeResponsable() {
            ReglaDeNegocioException ex = assertThrows(ReglaDeNegocioException.class,
                    () -> solicitud.cambiarEstado(Estado.EN_PROCESO));
            assertEquals("debe asignar responsable primero", ex.getMessage());
            assertEquals(Estado.PENDIENTE, solicitud.getEstado());
        }

        @Test
        @DisplayName("Ciclo completo: PENDIENTE -> EN_PROCESO -> RESUELTA")
        void cicloCompleto() {
            solicitud.asignarResponsable(RESPONSABLE);
            solicitud.cambiarEstado(Estado.EN_PROCESO);
            assertEquals(Estado.EN_PROCESO, solicitud.getEstado());

            solicitud.cambiarEstado(Estado.RESUELTA);
            assertEquals(Estado.RESUELTA, solicitud.getEstado());
            assertTrue(solicitud.estaCerrada());
        }

        @Test
        @DisplayName("Puede rechazarse estando pendiente y sin responsable")
        void rechazarSinResponsable() {
            solicitud.cambiarEstado(Estado.RECHAZADA);
            assertEquals(Estado.RECHAZADA, solicitud.getEstado());
        }

        @Test
        @DisplayName("No puede resolverse sin pasar antes por EN_PROCESO")
        void noSeResuelveDesdePendiente() {
            solicitud.asignarResponsable(RESPONSABLE);
            ReglaDeNegocioException ex = assertThrows(ReglaDeNegocioException.class,
                    () -> solicitud.cambiarEstado(Estado.RESUELTA));
            assertEquals("transicion invalida", ex.getMessage());
        }

        @Test
        @DisplayName("Una solicitud cerrada no admite más cambios de estado")
        void cerradaNoCambia() {
            solicitud.cambiarEstado(Estado.RECHAZADA);
            assertThrows(ReglaDeNegocioException.class, () -> solicitud.cambiarEstado(Estado.EN_PROCESO));
            assertEquals(Estado.RECHAZADA, solicitud.getEstado());
        }
    }

    @Nested
    @DisplayName("Asignación de responsable")
    class AsignacionDeResponsable {

        @Test
        @DisplayName("Se puede asignar y reemplazar el responsable mientras está abierta")
        void asignarYReasignar() {
            solicitud.asignarResponsable(RESPONSABLE);
            assertSame(RESPONSABLE, solicitud.getResponsable());

            solicitud.asignarResponsable(OTRO_RESPONSABLE);
            assertSame(OTRO_RESPONSABLE, solicitud.getResponsable());
        }

        @Test
        @DisplayName("No se puede asignar responsable a una solicitud cerrada")
        void cerradaNoAdmiteResponsable() {
            solicitud.cambiarEstado(Estado.RECHAZADA);
            ReglaDeNegocioException ex = assertThrows(ReglaDeNegocioException.class,
                    () -> solicitud.asignarResponsable(RESPONSABLE));
            assertEquals("la solicitud esta cerrada", ex.getMessage());
            assertFalse(solicitud.tieneResponsable());
        }
    }
}
