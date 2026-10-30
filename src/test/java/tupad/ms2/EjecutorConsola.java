package tupad.ms2;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

/**
 * Ejecuta el sistema completo (Main.main) como lo usaria una persona por consola:
 * le "tipea" una secuencia de entradas y devuelve todo lo que el programa imprimio.
 * Es la base de las pruebas de caracterizacion (golden master).
 */
final class EjecutorConsola {

    private EjecutorConsola() {
    }

    static String ejecutar(String... entradas) {
        String teclado = String.join("\n", entradas) + "\n";
        InputStream entradaOriginal = System.in;
        PrintStream salidaOriginal = System.out;
        ByteArrayOutputStream salidaCapturada = new ByteArrayOutputStream();
        try {
            System.setIn(new ByteArrayInputStream(teclado.getBytes(StandardCharsets.UTF_8)));
            System.setOut(new PrintStream(salidaCapturada, true, StandardCharsets.UTF_8));
            Main.main(new String[0]);
        } finally {
            System.setIn(entradaOriginal);
            System.setOut(salidaOriginal);
        }
        return normalizarFinesDeLinea(salidaCapturada.toString(StandardCharsets.UTF_8));
    }

    static String normalizarFinesDeLinea(String texto) {
        return texto.replace("\r\n", "\n");
    }
}
