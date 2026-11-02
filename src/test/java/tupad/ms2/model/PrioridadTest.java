package tupad.ms2.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import tupad.ms2.model.enums.Prioridad;
import tupad.ms2.model.excepciones.ReglaDeNegocioException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PrioridadTest {

    @Test
    @DisplayName("Los códigos 1, 2 y 3 corresponden a Alta, Media y Baja")
    void codigosValidos() {
        assertEquals(Prioridad.ALTA, Prioridad.desdeCodigo(1));
        assertEquals(Prioridad.MEDIA, Prioridad.desdeCodigo(2));
        assertEquals(Prioridad.BAJA, Prioridad.desdeCodigo(3));
        assertEquals("Alta", Prioridad.ALTA.getEtiqueta());
        assertEquals(3, Prioridad.BAJA.getCodigo());
    }

    @ParameterizedTest(name = "código {0}")
    @DisplayName("Un código fuera de rango es una prioridad inválida")
    @ValueSource(ints = {0, 4, -1, 99})
    void codigosInvalidos(int codigo) {
        ReglaDeNegocioException ex = assertThrows(ReglaDeNegocioException.class, () -> Prioridad.desdeCodigo(codigo));
        assertEquals("prioridad invalida", ex.getMessage());
    }
}
