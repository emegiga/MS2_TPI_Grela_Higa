package tupad.ms2.datos;

import tupad.ms2.model.Estudiante;
import tupad.ms2.model.NuevaSolicitud;
import tupad.ms2.model.Responsable;
import tupad.ms2.model.enums.Estado;
import tupad.ms2.model.enums.Prioridad;
import tupad.ms2.model.enums.TipoSolicitud;
import tupad.ms2.servicio.GestorSolicitudes;

import java.util.ArrayList;
import java.util.List;

/**
 * Datos de ejemplo con los que arranca el sistema. Todos son sintéticos (no corresponden a personas reales).
 * Están en una clase aparte para que Main solo arme el sistema y el gestor no dependa de datos fijos.
 */
public final class DatosIniciales {

    private DatosIniciales() {
    }

    public static List<Estudiante> estudiantes() {
        String[][] datos = {
                {"Agustin Di Maria", "L1001", "agustin87@gmail.com"},
                {"Lucia Kempes", "L1002", "lucia.kempes@outlook.com"},
                {"Tomas Maradona", "L1003", "tomas.mdz@hotmail.com"},
                {"Valentina Passarella", "L1004", "vale.passarella@yahoo.com"},
                {"Nicolas Fillol", "L1005", "nfillol23@gmail.com"},
                {"Camila Batistuta", "L1006", "camila.batistuta@outlook.com"},
                {"Franco Houseman", "L1007", "fr.houseman@gmail.com"},
                {"Martina Ardiles", "L1008", "martina.ardiles@hotmail.com"},
                {"Santiago Bertoni", "L1009", "sbert0ni@yahoo.com"},
                {"Julieta Valdano", "L1010", "julieta.valdano@gmail.com"},
                {"Mateo Ruggeri", "L1011", "mateo.ruggeri@outlook.com"},
                {"Florencia Gallego", "L1012", "flor.gallego@hotmail.com"},
                {"Benjamin Tarantini", "L1013", "benja.tarantini@gmail.com"},
                {"Sofia Houseman", "L1014", "sofia.h77@yahoo.com"},
                {"Facundo Rattin", "L1015", "facundo.rattin@outlook.com"},
                {"Micaela Sivori", "L1016", "mica.sivori@gmail.com"},
                {"Ignacio Labruna", "L1017", "ignacio.labruna@hotmail.com"},
                {"Paula Ayala", "L1018", "paula.ayala@yahoo.com"},
                {"Agustin Caniggia", "L1019", "agus.caniggia@gmail.com"},
                {"Julian Brown", "L1020", "julian.brown@outlook.com"}
        };
        List<Estudiante> estudiantes = new ArrayList<>();
        for (String[] estudiante : datos) {
            estudiantes.add(new Estudiante(estudiante[0], estudiante[1], estudiante[2]));
        }
        return estudiantes;
    }

    public static List<Responsable> responsables() {
        return List.of(
                new Responsable(1, "Maria Ortega Sánchez", "Alumnado"),
                new Responsable(2, "Jorge Tchami", "Alumnado"),
                new Responsable(3, "Laura Gorosito", "Secretaria Academica"));
    }

    // Solicitudes precargadas: #1 y #3 pendientes sin responsable, #2 en proceso con responsable.
    public static void cargarSolicitudesDeEjemplo(GestorSolicitudes gestor) {
        gestor.registrarSolicitud(new NuevaSolicitud("L1001", TipoSolicitud.CONSTANCIA, Prioridad.MEDIA, "Constancia de alumno regular"));
        gestor.registrarSolicitud(new NuevaSolicitud("L1002", TipoSolicitud.RECLAMO, Prioridad.ALTA, "Error en nota del parcial"));
        gestor.registrarSolicitud(new NuevaSolicitud("L1001", TipoSolicitud.EQUIVALENCIA, Prioridad.BAJA, "Equivalencia de Matematica I"));
        gestor.asignarResponsable(2, 1);   // Maria Ortega Sánchez (Alumnado)
        gestor.cambiarEstado(2, Estado.EN_PROCESO);
    }
}
