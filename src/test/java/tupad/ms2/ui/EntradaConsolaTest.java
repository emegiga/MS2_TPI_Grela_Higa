package tupad.ms2.ui;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EntradaConsolaTest {

    private final ByteArrayOutputStream salida = new ByteArrayOutputStream();

    private EntradaConsola consolaQueRecibe(String teclado) {
        return new EntradaConsola(new Scanner(teclado), new PrintStream(salida, true, StandardCharsets.UTF_8));
    }

    private String salidaMostrada() {
        return salida.toString(StandardCharsets.UTF_8).replace("\r\n", "\n");
    }

    @Test
    @DisplayName("elegir() numera las opciones desde 1 y devuelve la elegida")
    void eligeOpcionValida() {
        Optional<String> elegida = consolaQueRecibe("2\n").elegir("Color:", List.of("Rojo", "Verde"), texto -> texto);

        assertEquals(Optional.of("Verde"), elegida);
        assertEquals("Color:\n1. Rojo\n2. Verde\nOpcion: ", salidaMostrada());
    }

    @ParameterizedTest(name = "respuesta \"{0}\"")
    @DisplayName("elegir() informa 'Opcion invalida' y devuelve vacío ante una respuesta fuera de la lista")
    @ValueSource(strings = {"0", "3", "-1", "dos", "", " 1"})
    void rechazaOpcionInvalida(String respuesta) {
        Optional<String> elegida = consolaQueRecibe(respuesta + "\n").elegir("Color:", List.of("Rojo", "Verde"), texto -> texto);

        assertTrue(elegida.isEmpty());
        assertTrue(salidaMostrada().endsWith("Opcion: Opcion invalida\n"));
    }

    @Test
    @DisplayName("leerLinea() muestra el mensaje en la misma línea y devuelve lo tipeado")
    void leeLinea() {
        String leido = consolaQueRecibe("Mi descripcion\n").leerLinea("Descripcion: ");

        assertEquals("Mi descripcion", leido);
        assertEquals("Descripcion: ", salidaMostrada());
    }

    @Test
    @DisplayName("convertirANumero() distingue números de textos")
    void convierteANumero() {
        assertEquals(Optional.of(12), EntradaConsola.convertirANumero("12"));
        assertTrue(EntradaConsola.convertirANumero("ALTA").isEmpty());
        assertTrue(EntradaConsola.convertirANumero("").isEmpty());
    }
}
