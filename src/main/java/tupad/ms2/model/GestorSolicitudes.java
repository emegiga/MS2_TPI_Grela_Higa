package tupad.ms2.model;

import tupad.ms2.model.enums.Estado;
import tupad.ms2.model.enums.Prioridad;
import tupad.ms2.model.excepciones.ReglaDeNegocioException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class GestorSolicitudes {

    public List<Solicitud> solicitudes = new ArrayList<>();
    public List<Estudiante> estudiantes = new ArrayList<>();
    public List<Responsable> responsables = new ArrayList<>();
    int proximoId = 1;

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
        if (datos.descripcion() == null || datos.descripcion().length() < 5) {
            throw new ReglaDeNegocioException("descripcion muy corta");
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
                if (solicitud.estado.toString().equalsIgnoreCase(valor)) {
                    resultado.add(solicitud);
                }
            }
        } else if (criterio.equals("tipo")) {
            for (Solicitud solicitud : solicitudes) {
                if (solicitud.tipo.name().equalsIgnoreCase(valor)) {
                    resultado.add(solicitud);
                }
            }
        } else if (criterio.equals("legajo")) {
            for (Solicitud solicitud : solicitudes) {
                if (solicitud.estudiante.legajo.equals(valor)) {
                    resultado.add(solicitud);
                }
            }
        } else if (criterio.equals("prioridad")) {
            for (Solicitud solicitud : solicitudes) {
                if (String.valueOf(solicitud.prioridad.getCodigo()).equals(valor)) {
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
            String responsable = solicitud.tieneResponsable() ? solicitud.responsable.getNombreConArea() : "SIN ASIGNAR";
            System.out.println("#" + solicitud.id + " | " + solicitud.fecha + " | " + solicitud.estudiante.nombre
                    + " | " + solicitud.tipo + " | Prioridad: " + solicitud.prioridad.getCodigo() + " | " + solicitud.estado
                    + " | " + responsable);
            if (solicitud.estado == Estado.PENDIENTE) pendientes++;
            if (solicitud.estado == Estado.EN_PROCESO) enProceso++;
            if (solicitud.estado == Estado.RESUELTA) resueltas++;
            if (solicitud.estado == Estado.RECHAZADA) rechazadas++;
            if (solicitud.prioridad == Prioridad.ALTA) alta++;
            if (solicitud.prioridad == Prioridad.MEDIA) media++;
            if (solicitud.prioridad == Prioridad.BAJA) baja++;
        }
        System.out.println("-----------------------------------");
        System.out.println("Total: " + solicitudes.size());
        System.out.println("Pendientes: " + pendientes + " | En proceso: " + enProceso
                + " | Resueltas: " + resueltas + " | Rechazadas: " + rechazadas);
        System.out.println("Alta: " + alta + " | Media: " + media + " | Baja: " + baja);
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
            if (candidata.id == id) {
                return candidata;
            }
        }
        return null;
    }

    private Estudiante buscarEstudiantePorLegajo(String legajo) {
        for (Estudiante candidato : estudiantes) {
            if (candidato.legajo.equals(legajo)) {
                return candidato;
            }
        }
        return null;
    }

    private Responsable buscarResponsablePorId(int idResponsable) {
        for (Responsable candidato : responsables) {
            if (candidato.id == idResponsable) {
                return candidato;
            }
        }
        return null;
    }
}
