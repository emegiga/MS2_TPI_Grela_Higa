package tupad.ms2.ui;

import java.io.PrintStream;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.function.Function;

/**
 * Lectura y escritura por consola. Es la única clase que conoce el Scanner y el PrintStream.
 */
public class EntradaConsola {

    private final Scanner scanner;
    private final PrintStream salida;

    public EntradaConsola(Scanner scanner, PrintStream salida) {
        this.scanner = scanner;
        this.salida = salida;
    }

    public void mostrar(String linea) {
        salida.println(linea);
    }

    // Muestra el mensaje en la misma línea en la que se va a escribir la respuesta
    public String leerLinea(String mensaje) {
        salida.print(mensaje);
        return scanner.nextLine();
    }

    /**
     * Muestra las opciones numeradas desde 1 y devuelve la elegida. Si la respuesta no es un número
     * de la lista informa "Opcion invalida" y devuelve vacío (el llamador decide si cancela).
     */
    public <T> Optional<T> elegir(String titulo, List<T> opciones, Function<T, String> descripcion) {
        mostrar(titulo);
        for (int i = 0; i < opciones.size(); i++) {
            mostrar((i + 1) + ". " + descripcion.apply(opciones.get(i)));
        }
        Optional<Integer> numero = convertirANumero(leerLinea("Opcion: "));
        if (numero.isPresent() && numero.get() >= 1 && numero.get() <= opciones.size()) {
            return Optional.of(opciones.get(numero.get() - 1));
        }
        mostrar("Opcion invalida");
        return Optional.empty();
    }

    public static Optional<Integer> convertirANumero(String texto) {
        try {
            return Optional.of(Integer.parseInt(texto));
        } catch (NumberFormatException ex) {
            return Optional.empty();
        }
    }
}
