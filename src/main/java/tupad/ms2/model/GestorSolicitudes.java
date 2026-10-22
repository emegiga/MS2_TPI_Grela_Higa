package tupad.ms2.model;

import tupad.ms2.model.enums.Estado;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class GestorSolicitudes {

    public List<Solicitud> solicitudes = new ArrayList<>();
    public List<Estudiante> estudiantes = new ArrayList<>();
    public List<Responsable> responsables = new ArrayList<>();
    int contador = 1;

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
    public int registrarSolicitud(String legajo, String tipo, String desc, int prioridad) {
        Estudiante e = null;
        for (Estudiante x : estudiantes) {
            if (x.legajo.equals(legajo)) {
                e = x;
            }
        }
        if (e == null) {
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
        if (desc == null || desc.length() < 5) {
            System.out.println("Error: descripcion muy corta");
            return -1;
        }

        Solicitud s = new Solicitud(contador, e, tipo, prioridad, desc, LocalDate.now().toString());
        contador++;
        solicitudes.add(s);
        System.out.println("Solicitud registrada con id " + s.id);
        return s.id;
    }

    // Cambiar estado de solicitud
    public boolean cambiarEstado(int id, String nuevoEstado) {
        Solicitud s = null;
        for (Solicitud x : solicitudes) {
            if (x.id == id) {
                s = x;
            }
        }
        if (s == null) {
            System.out.println("Error: solicitud no encontrada");
            return false;
        }
        if (nuevoEstado.equals("EN_PROCESO")) {
            if (s.estado == Estado.PENDIENTE) {
                if (s.resp == null) {
                    System.out.println("Error: debe asignar responsable primero");
                    return false;
                }
                s.estado = Estado.EN_PROCESO;
            } else {
                System.out.println("Error: transicion invalida");
                return false;
            }
        } else if (nuevoEstado.equals("RESUELTA")) {
            if (s.estado == Estado.EN_PROCESO) {
                s.estado = Estado.RESUELTA;
            } else {
                System.out.println("Error: transicion invalida");
                return false;
            }
        } else if (nuevoEstado.equals("RECHAZADA")) {
            if (s.estado == Estado.PENDIENTE || s.estado == Estado.EN_PROCESO) {
                s.estado = Estado.RECHAZADA;
            } else {
                System.out.println("Error: transicion invalida");
                return false;
            }
        } else {
            System.out.println("Error: estado desconocido");
            return false;
        }
        System.out.println("Estado actualizado a " + s.estado);
        return true;
    }

    // Asignar responsable (del listado) a solicitud
    public boolean asignarResponsable(int id, int idResponsable) {
        Solicitud s = null;
        for (Solicitud x : solicitudes) {
            if (x.id == id) {
                s = x;
            }
        }
        if (s == null) {
            System.out.println("Error: solicitud no encontrada");
            return false;
        }
        if (s.estado == Estado.RESUELTA || s.estado == Estado.RECHAZADA) {
            System.out.println("Error: la solicitud esta cerrada");
            return false;
        }
        Responsable r = null;
        for (Responsable x : responsables) {
            if (x.id == idResponsable) {
                r = x;
            }
        }
        if (r == null) {
            System.out.println("Error: responsable no encontrado");
            return false;
        }
        s.resp = r;
        System.out.println("Responsable asignado: " + r.nombre);
        return true;
    }

    // Buscar / Filtrar
    public List<Solicitud> buscar(String criterio, String valor) {
        List<Solicitud> res = new ArrayList<>();
        if (criterio.equals("estado")) {
            for (Solicitud s : solicitudes) {
                if (s.estado.toString().equalsIgnoreCase(valor)) {
                    res.add(s);
                }
            }
        } else if (criterio.equals("tipo")) {
            for (Solicitud s : solicitudes) {
                if (s.tipo.equalsIgnoreCase(valor)) {
                    res.add(s);
                }
            }
        } else if (criterio.equals("legajo")) {
            for (Solicitud s : solicitudes) {
                if (s.est.legajo.equals(valor)) {
                    res.add(s);
                }
            }
        } else if (criterio.equals("prioridad")) {
            for (Solicitud s : solicitudes) {
                if (String.valueOf(s.prioridad).equals(valor)) {
                    res.add(s);
                }
            }
        } else {
            System.out.println("Criterio no soportado");
        }
        return res;
    }

    // Reporte
    public void reporte() {
        int pend = 0, proc = 0, res = 0, rech = 0;
        int alta = 0, media = 0, baja = 0;
        System.out.println("===== REPORTE DE SOLICITUDES =====");
        for (Solicitud s : solicitudes) {
            String r = (s.resp == null) ? "SIN ASIGNAR" : s.resp.nombre + " (" + s.resp.area + ")";
            System.out.println("#" + s.id + " | " + s.fecha + " | " + s.est.nombre + " | " + s.tipo
                    + " | Prioridad: " + s.prioridad + " | " + s.estado + " | " + r);
            if (s.estado == Estado.PENDIENTE) pend++;
            if (s.estado == Estado.EN_PROCESO) proc++;
            if (s.estado == Estado.RESUELTA) res++;
            if (s.estado == Estado.RECHAZADA) rech++;
            if (s.prioridad == 1) alta++;
            if (s.prioridad == 2) media++;
            if (s.prioridad == 3) baja++;
        }
        System.out.println("-----------------------------------");
        System.out.println("Total: " + solicitudes.size());
        System.out.println("Pendientes: " + pend + " | En proceso: " + proc
                + " | Resueltas: " + res + " | Rechazadas: " + rech);
        System.out.println("Alta: " + alta + " | Media: " + media + " | Baja: " + baja);
    }
}