package tupad.ms2;

import tupad.ms2.datos.DatosIniciales;
import tupad.ms2.repositorio.EstudianteRepositorioEnMemoria;
import tupad.ms2.repositorio.ResponsableRepositorioEnMemoria;
import tupad.ms2.repositorio.SolicitudRepositorioEnMemoria;
import tupad.ms2.servicio.GestorSolicitudes;
import tupad.ms2.ui.EntradaConsola;
import tupad.ms2.ui.MenuConsola;

import java.time.Clock;
import java.util.Scanner;

/**
 * Punto de entrada: arma las piezas del sistema (composition root) y arranca el menú.
 * Es el único lugar que conoce las implementaciones concretas.
 */
public class Main {
    public static void main(String[] args) {
        GestorSolicitudes gestor = new GestorSolicitudes(
                new SolicitudRepositorioEnMemoria(),
                new EstudianteRepositorioEnMemoria(DatosIniciales.estudiantes()),
                new ResponsableRepositorioEnMemoria(DatosIniciales.responsables()),
                Clock.systemDefaultZone());
        DatosIniciales.cargarSolicitudesDeEjemplo(gestor);

        Scanner scanner = new Scanner(System.in);
        new MenuConsola(gestor, new EntradaConsola(scanner, System.out)).iniciar();
        scanner.close();
    }
}
