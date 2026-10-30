package tupad.ms2.model;

import tupad.ms2.model.enums.Estado;

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

    // Registrar solicitud
    public int registrarSolicitud(String legajo, String tipo, String descripcion, int prioridad) {
        Estudiante estudiante = null;
        for (Estudiante candidato : estudiantes) {
            if (candidato.legajo.equals(legajo)) {
                estudiante = candidato;
            }
        }
        if (estudiante == null) {
            System.out.println("Error: estudiante no encontrado");
            return -1;
        }
        if (!tipo.equals("CONSTANCIA") && !tipo.equals("EQUIVALENCIA")
                && !tipo.equals("RECLAMO") && !tipo.equals("OTRO")) {
            System.out.println("Error: tipo invalido");
            return -1;
        }
        if (prioridad < 1 || prioridad > 3) {
            System.out.println("Error: prioridad invalida");
            return -1;
        }
        if (descripcion == null || descripcion.length() < 5) {
            System.out.println("Error: descripcion muy corta");
            return -1;
        }

        Solicitud solicitud = new Solicitud(proximoId, estudiante, tipo, prioridad, descripcion, LocalDate.now().toString());
        proximoId++;
        solicitudes.add(solicitud);
        System.out.println("Solicitud registrada con id " + solicitud.id);
        return solicitud.id;
    }

    // Cambiar estado de solicitud
    public boolean cambiarEstado(int id, String nuevoEstado) {
        Solicitud solicitud = null;
        for (Solicitud candidata : solicitudes) {
            if (candidata.id == id) {
                solicitud = candidata;
            }
        }
        if (solicitud == null) {
            System.out.println("Error: solicitud no encontrada");
            return false;
        }
        if (nuevoEstado.equals("EN_PROCESO")) {
            if (solicitud.estado == Estado.PENDIENTE) {
                if (solicitud.responsable == null) {
                    System.out.println("Error: debe asignar responsable primero");
                    return false;
                }
                solicitud.estado = Estado.EN_PROCESO;
            } else {
                System.out.println("Error: transicion invalida");
                return false;
            }
        } else if (nuevoEstado.equals("RESUELTA")) {
            if (solicitud.estado == Estado.EN_PROCESO) {
                solicitud.estado = Estado.RESUELTA;
            } else {
                System.out.println("Error: transicion invalida");
                return false;
            }
        } else if (nuevoEstado.equals("RECHAZADA")) {
            if (solicitud.estado == Estado.PENDIENTE || solicitud.estado == Estado.EN_PROCESO) {
                solicitud.estado = Estado.RECHAZADA;
            } else {
                System.out.println("Error: transicion invalida");
                return false;
            }
        } else {
            System.out.println("Error: estado desconocido");
            return false;
        }
        System.out.println("Estado actualizado a " + solicitud.estado);
        return true;
    }

    // Asignar responsable (del listado) a solicitud
    public boolean asignarResponsable(int id, int idResponsable) {
        Solicitud solicitud = null;
        for (Solicitud candidata : solicitudes) {
            if (candidata.id == id) {
                solicitud = candidata;
            }
        }
        if (solicitud == null) {
            System.out.println("Error: solicitud no encontrada");
            return false;
        }
        if (solicitud.estado == Estado.RESUELTA || solicitud.estado == Estado.RECHAZADA) {
            System.out.println("Error: la solicitud esta cerrada");
            return false;
        }
        Responsable responsable = null;
        for (Responsable candidato : responsables) {
            if (candidato.id == idResponsable) {
                responsable = candidato;
            }
        }
        if (responsable == null) {
            System.out.println("Error: responsable no encontrado");
            return false;
        }
        solicitud.responsable = responsable;
        System.out.println("Responsable asignado: " + responsable.nombre);
        return true;
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
                if (solicitud.tipo.equalsIgnoreCase(valor)) {
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
                if (String.valueOf(solicitud.prioridad).equals(valor)) {
                    resultado.add(solicitud);
                }
            }
        } else {
            System.out.println("Criterio no soportado");
        }
        return resultado;
    }

    // Reporte
    public void reporte() {
        int pendientes = 0, enProceso = 0, resueltas = 0, rechazadas = 0;
        int alta = 0, media = 0, baja = 0;
        System.out.println("===== REPORTE DE SOLICITUDES =====");
        for (Solicitud solicitud : solicitudes) {
            String responsable = (solicitud.responsable == null) ? "SIN ASIGNAR"
                    : solicitud.responsable.nombre + " (" + solicitud.responsable.area + ")";
            System.out.println("#" + solicitud.id + " | " + solicitud.fecha + " | " + solicitud.estudiante.nombre
                    + " | " + solicitud.tipo + " | Prioridad: " + solicitud.prioridad + " | " + solicitud.estado
                    + " | " + responsable);
            if (solicitud.estado == Estado.PENDIENTE) pendientes++;
            if (solicitud.estado == Estado.EN_PROCESO) enProceso++;
            if (solicitud.estado == Estado.RESUELTA) resueltas++;
            if (solicitud.estado == Estado.RECHAZADA) rechazadas++;
            if (solicitud.prioridad == 1) alta++;
            if (solicitud.prioridad == 2) media++;
            if (solicitud.prioridad == 3) baja++;
        }
        System.out.println("-----------------------------------");
        System.out.println("Total: " + solicitudes.size());
        System.out.println("Pendientes: " + pendientes + " | En proceso: " + enProceso
                + " | Resueltas: " + resueltas + " | Rechazadas: " + rechazadas);
        System.out.println("Alta: " + alta + " | Media: " + media + " | Baja: " + baja);
    }
}
