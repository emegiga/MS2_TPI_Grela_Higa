package tupad.ms2.model;

import tupad.ms2.model.enums.Estado;
import tupad.ms2.model.enums.Prioridad;
import tupad.ms2.model.excepciones.ReglaDeNegocioException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GestorSolicitudes {

    private final List<Solicitud> solicitudes = new ArrayList<>();
    private final List<Estudiante> estudiantes = new ArrayList<>();
    private final List<Responsable> responsables = new ArrayList<>();
    private int proximoId = 1;

    public GestorSolicitudes() {
        cargarEstudiantes();
        cargarResponsables();
    }

    // Listado de estudiantes
    private void cargarEstudiantes() {
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
        for (int i = 0; i < datos.length; i++) {
            estudiantes.add(new Estudiante(i + 1, datos[i][0], datos[i][1], datos[i][2]));
        }
    }

    // Listado de responsables
    private void cargarResponsables() {
        String[][] datos = {
                {"Maria Ortega Sánchez", "Alumnado"},
                {"Jorge Tchami", "Alumnado"},
                {"Laura Gorosito", "Secretaria Academica"}
        };
        for (int i = 0; i < datos.length; i++) {
            responsables.add(new Responsable(i + 1, datos[i][0], datos[i][1]));
        }
    }

    // Registrar solicitud. Devuelve la solicitud creada.
    public Solicitud registrarSolicitud(NuevaSolicitud datos) {
        Estudiante estudiante = buscarEstudiantePorLegajo(datos.legajo());
        if (estudiante == null) {
            throw new ReglaDeNegocioException("estudiante no encontrado");
        }
        Solicitud solicitud = new Solicitud(proximoId, estudiante, datos, LocalDate.now().toString());
        proximoId++;
        solicitudes.add(solicitud);
        return solicitud;
    }

    // Cambiar estado de solicitud. Devuelve la solicitud actualizada.
    public Solicitud cambiarEstado(int id, Estado nuevoEstado) {
        Solicitud solicitud = obtenerSolicitud(id);
        solicitud.cambiarEstado(nuevoEstado);
        return solicitud;
    }

    // Asignar responsable (del listado) a solicitud. Devuelve la solicitud actualizada.
    public Solicitud asignarResponsable(int id, int idResponsable) {
        Solicitud solicitud = obtenerSolicitud(id);
        Responsable responsable = buscarResponsablePorId(idResponsable);
        if (responsable == null) {
            throw new ReglaDeNegocioException("responsable no encontrado");
        }
        solicitud.asignarResponsable(responsable);
        return solicitud;
    }

    // Buscar / Filtrar
    public List<Solicitud> buscar(String criterio, String valor) {
        List<Solicitud> resultado = new ArrayList<>();
        if (criterio.equals("estado")) {
            for (Solicitud solicitud : solicitudes) {
                if (solicitud.getEstado().toString().equalsIgnoreCase(valor)) {
                    resultado.add(solicitud);
                }
            }
        } else if (criterio.equals("tipo")) {
            for (Solicitud solicitud : solicitudes) {
                if (solicitud.getTipo().name().equalsIgnoreCase(valor)) {
                    resultado.add(solicitud);
                }
            }
        } else if (criterio.equals("legajo")) {
            for (Solicitud solicitud : solicitudes) {
                if (solicitud.getEstudiante().getLegajo().equals(valor)) {
                    resultado.add(solicitud);
                }
            }
        } else if (criterio.equals("prioridad")) {
            for (Solicitud solicitud : solicitudes) {
                if (String.valueOf(solicitud.getPrioridad().getCodigo()).equals(valor)) {
                    resultado.add(solicitud);
                }
            }
        } else {
            throw new IllegalArgumentException("criterio no soportado: " + criterio);
        }
        return resultado;
    }

    // Reporte
    public void reporte() {
        int pendientes = 0, enProceso = 0, resueltas = 0, rechazadas = 0;
        int alta = 0, media = 0, baja = 0;
        System.out.println("===== REPORTE DE SOLICITUDES =====");
        for (Solicitud solicitud : solicitudes) {
            String responsable = solicitud.tieneResponsable() ? solicitud.getResponsable().getNombreConArea() : "SIN ASIGNAR";
            System.out.println("#" + solicitud.getId() + " | " + solicitud.getFecha() + " | " + solicitud.getEstudiante().getNombre()
                    + " | " + solicitud.getTipo() + " | Prioridad: " + solicitud.getPrioridad().getCodigo() + " | " + solicitud.getEstado()
                    + " | " + responsable);
            if (solicitud.getEstado() == Estado.PENDIENTE) pendientes++;
            if (solicitud.getEstado() == Estado.EN_PROCESO) enProceso++;
            if (solicitud.getEstado() == Estado.RESUELTA) resueltas++;
            if (solicitud.getEstado() == Estado.RECHAZADA) rechazadas++;
            if (solicitud.getPrioridad() == Prioridad.ALTA) alta++;
            if (solicitud.getPrioridad() == Prioridad.MEDIA) media++;
            if (solicitud.getPrioridad() == Prioridad.BAJA) baja++;
        }
        System.out.println("-----------------------------------");
        System.out.println("Total: " + solicitudes.size());
        System.out.println("Pendientes: " + pendientes + " | En proceso: " + enProceso
                + " | Resueltas: " + resueltas + " | Rechazadas: " + rechazadas);
        System.out.println("Alta: " + alta + " | Media: " + media + " | Baja: " + baja);
    }

    // Vistas de solo lectura: nadie fuera del gestor puede agregar o quitar elementos
    public List<Solicitud> getSolicitudes() {
        return Collections.unmodifiableList(solicitudes);
    }

    public List<Estudiante> getEstudiantes() {
        return Collections.unmodifiableList(estudiantes);
    }

    public List<Responsable> getResponsables() {
        return Collections.unmodifiableList(responsables);
    }

    private Solicitud obtenerSolicitud(int id) {
        Solicitud solicitud = buscarSolicitudPorId(id);
        if (solicitud == null) {
            throw new ReglaDeNegocioException("solicitud no encontrada");
        }
        return solicitud;
    }

    // Devuelve la solicitud con ese id, o null si no existe
    public Solicitud buscarSolicitudPorId(int id) {
        for (Solicitud candidata : solicitudes) {
            if (candidata.getId() == id) {
                return candidata;
            }
        }
        return null;
    }

    private Estudiante buscarEstudiantePorLegajo(String legajo) {
        for (Estudiante candidato : estudiantes) {
            if (candidato.getLegajo().equals(legajo)) {
                return candidato;
            }
        }
        return null;
    }

    private Responsable buscarResponsablePorId(int idResponsable) {
        for (Responsable candidato : responsables) {
            if (candidato.getId() == idResponsable) {
                return candidato;
            }
        }
        return null;
    }
}
