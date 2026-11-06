package tupad.ms2.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import tupad.ms2.model.enums.Estado;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EstadoTest {

    @ParameterizedTest(name = "{0} -> {1}: {2}")
    @DisplayName("La tabla de transiciones respeta el ciclo de vida de la solicitud")
    @CsvSource({
            "PENDIENTE,  PENDIENTE,  false",
            "PENDIENTE,  EN_PROCESO, true",
            "PENDIENTE,  RESUELTA,   false",
            "PENDIENTE,  RECHAZADA,  true",
            "EN_PROCESO, PENDIENTE,  false",
            "EN_PROCESO, EN_PROCESO, false",
            "EN_PROCESO, RESUELTA,   true",
            "EN_PROCESO, RECHAZADA,  true",
            "RESUELTA,   PENDIENTE,  false",
            "RESUELTA,   EN_PROCESO, false",
            "RESUELTA,   RESUELTA,   false",
            "RESUELTA,   RECHAZADA,  false",
            "RECHAZADA,  PENDIENTE,  false",
            "RECHAZADA,  EN_PROCESO, false",
            "RECHAZADA,  RESUELTA,   false",
            "RECHAZADA,  RECHAZADA,  false"
    })
    void transiciones(Estado origen, Estado destino, boolean permitida) {
        assertEquals(permitida, origen.permiteTransicionA(destino));
    }

    @Test
    @DisplayName("Cada estado tiene la etiqueta con la que se totaliza en el reporte")
    void etiquetasDelReporte() {
        assertEquals("Pendientes", Estado.PENDIENTE.getEtiquetaPlural());
        assertEquals("En proceso", Estado.EN_PROCESO.getEtiquetaPlural());
        assertEquals("Resueltas", Estado.RESUELTA.getEtiquetaPlural());
        assertEquals("Rechazadas", Estado.RECHAZADA.getEtiquetaPlural());
    }

    @Test
    @DisplayName("Solo EN_PROCESO requiere responsable")
    void requiereResponsable() {
        assertFalse(Estado.PENDIENTE.requiereResponsable());
        assertTrue(Estado.EN_PROCESO.requiereResponsable());
        assertFalse(Estado.RESUELTA.requiereResponsable());
        assertFalse(Estado.RECHAZADA.requiereResponsable());
    }

    @Test
    @DisplayName("Solo RESUELTA y RECHAZADA son estados cerrados")
    void estadosCerrados() {
        assertFalse(Estado.PENDIENTE.esCerrado());
        assertFalse(Estado.EN_PROCESO.esCerrado());
        assertTrue(Estado.RESUELTA.esCerrado());
        assertTrue(Estado.RECHAZADA.esCerrado());
    }
}
