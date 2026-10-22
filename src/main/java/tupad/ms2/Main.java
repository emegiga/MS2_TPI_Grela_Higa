package tupad.ms2;

import tupad.ms2.model.Estudiante;
import tupad.ms2.model.GestorSolicitudes;
import tupad.ms2.model.Responsable;
import tupad.ms2.model.Solicitud;
import tupad.ms2.model.enums.Estado;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        GestorSolicitudes g = new GestorSolicitudes();
        Scanner sc = new Scanner(System.in);
        boolean salir = false;

        cargarDatosEjemplo(g);

        while (!salir) {
            System.out.println();
            System.out.println("######################## GESTION DE SOLICITUDES ACADEMICAS ########################");
            System.out.println("1. Registrar solicitud");
            System.out.println("2. Cambiar estado");
            System.out.println("3. Asignar responsable");
            System.out.println("4. Buscar / filtrar");
            System.out.println("5. Reporte");
            System.out.println("0. Salir");
            System.out.print("Opcion: ");
            String op = sc.nextLine();

            if (op.equals("1")) {
                if (g.estudiantes.isEmpty()) {
                    System.out.println("No hay estudiantes cargados.");
                } else {
                    String[] ests = new String[g.estudiantes.size()];
                    for (int i = 0; i < ests.length; i++) {
                        Estudiante x = g.estudiantes.get(i);
                        ests[i] = x.legajo + " - " + x.nombre + " (" + x.email + ")";
                    }
                    int sEst = elegir(sc, "Estudiante:", ests);

                    if (sEst == -1) {
                        System.out.println("Registro cancelado.");
                    } else {
                        Estudiante est = g.estudiantes.get(sEst);

                        String[] tipos = {"CONSTANCIA", "EQUIVALENCIA", "RECLAMO", "OTRO"};
                        int t = elegir(sc, "Tipo:", tipos);
                        if (t == -1) {
                            System.out.println("Registro cancelado.");
                        } else {
                            System.out.print("Descripcion: ");
                            String d = sc.nextLine();
                            System.out.print("Prioridad (1=alta, 2=media, 3=baja): ");
                            int p = Integer.parseInt(sc.nextLine());
                            g.registrarSolicitud(est.legajo, tipos[t], d, p);
                        }
                    }
                }

            } else if (op.equals("2")) {
                if (g.solicitudes.isEmpty()) {
                    System.out.println("No hay solicitudes cargadas.");
                } else {
                    listarSolicitudes(g);
                    Solicitud sol = pedirSolicitud(sc, g);

                    if (sol == null) {
                        System.out.println("Error: solicitud no encontrada");
                    } else if (sol.estado == Estado.RESUELTA || sol.estado == Estado.RECHAZADA) {
                        System.out.println("Error: la solicitud esta cerrada");
                    } else {
                        String[] estados;
                        if (sol.resp == null) {
                            System.out.println("La solicitud #" + sol.id + " no tiene responsable asignado. "
                                    + "Solo puede rechazarse; para otros estados asignelo desde la opcion 3.");
                            estados = new String[]{"RECHAZADA"};
                        } else {
                            estados = new String[]{"EN_PROCESO", "RESUELTA", "RECHAZADA"};
                        }
                        int e = elegir(sc, "Nuevo estado:", estados);
                        if (e != -1) {
                            g.cambiarEstado(sol.id, estados[e]);
                        }
                    }
                }

            } else if (op.equals("3")) {
                if (g.solicitudes.isEmpty()) {
                    System.out.println("No hay solicitudes cargadas.");
                } else {
                    listarSolicitudes(g);
                    Solicitud sol = pedirSolicitud(sc, g);

                    if (sol == null) {
                        System.out.println("Error: solicitud no encontrada");
                    } else if (sol.estado == Estado.RESUELTA || sol.estado == Estado.RECHAZADA) {
                        System.out.println("Error: la solicitud esta cerrada");
                    } else {
                        if (sol.resp != null) {
                            System.out.println("Responsable actual: " + sol.resp.nombre + " (" + sol.resp.area
                                    + "). Si elegis otro, se reemplaza.");
                        }

                        String[] ops = new String[g.responsables.size()];
                        for (int i = 0; i < ops.length; i++) {
                            Responsable resp = g.responsables.get(i);
                            ops[i] = resp.nombre + " (" + resp.area + ")";
                        }

                        int sel = elegir(sc, "Responsable:", ops);
                        if (sel != -1) {
                            Responsable elegido = g.responsables.get(sel);
                            g.asignarResponsable(sol.id, elegido.id);
                        }
                    }
                }

            } else if (op.equals("4")) {
                String[] criterios = {"estado", "tipo", "legajo", "prioridad"};
                int c = elegir(sc, "Buscar por:", new String[]{"Estado", "Tipo", "Legajo", "Prioridad"});
                String valor = null;

                if (c == 0) {
                    Estado[] es = Estado.values();
                    String[] nombres = new String[es.length];
                    for (int i = 0; i < es.length; i++) {
                        nombres[i] = es[i].toString();
                    }
                    int sel = elegir(sc, "Estado:", nombres);
                    if (sel != -1) valor = nombres[sel];

                } else if (c == 1) {
                    String[] tipos = {"CONSTANCIA", "EQUIVALENCIA", "RECLAMO", "OTRO"};
                    int sel = elegir(sc, "Tipo:", tipos);
                    if (sel != -1) valor = tipos[sel];

                } else if (c == 2) {
                    if (g.estudiantes.isEmpty()) {
                        System.out.println("No hay estudiantes cargados.");
                    } else {
                        String[] legs = new String[g.estudiantes.size()];
                        for (int i = 0; i < legs.length; i++) {
                            legs[i] = g.estudiantes.get(i).legajo + " - " + g.estudiantes.get(i).nombre;
                        }
                        int sel = elegir(sc, "Estudiante:", legs);
                        if (sel != -1) valor = g.estudiantes.get(sel).legajo;
                    }

                } else if (c == 3) {
                    int sel = elegir(sc, "Prioridad:", new String[]{"Alta", "Media", "Baja"});
                    if (sel != -1) valor = String.valueOf(sel + 1);
                }

                if (valor != null) {
                    List<Solicitud> r = g.buscar(criterios[c], valor);
                    for (Solicitud s : r) {
                        System.out.println("#" + s.id + " | " + s.est.nombre + " | " + s.tipo
                                + " | Prioridad: " + s.prioridad + " | " + s.estado);
                    }
                    System.out.println("Resultados: " + r.size());
                }

            } else if (op.equals("5")) {
                g.reporte();
            } else if (op.equals("0")) {
                salir = true;
            } else {
                System.out.println("Opcion invalida");
            }
        }
        sc.close();
    }

    // Precarga de solicitudes al iniciar, sin mostrar los mensajes del gestor (estudiantes y responsables ya vienen cargados desde GestorSolicitudes)
    private static void cargarDatosEjemplo(GestorSolicitudes g) {
        PrintStream original = System.out;
        System.setOut(new PrintStream(OutputStream.nullOutputStream()));
        try {
            g.registrarSolicitud("L1001", "CONSTANCIA", "Constancia de alumno regular", 2);
            g.registrarSolicitud("L1002", "RECLAMO", "Error en nota del parcial", 1);
            g.registrarSolicitud("L1001", "EQUIVALENCIA", "Equivalencia de Matematica I", 3);
            g.asignarResponsable(2, 1);   // Maria Lopez (Alumnado)
            g.cambiarEstado(2, "EN_PROCESO");
        } finally {
            System.setOut(original);
        }
    }

    // Pide un ID y devuelve la solicitud correspondiente, o null si no existe o no es un numero
    private static Solicitud pedirSolicitud(Scanner sc, GestorSolicitudes g) {
        System.out.print("Id solicitud: ");
        try {
            int id = Integer.parseInt(sc.nextLine().trim());
            for (Solicitud x : g.solicitudes) {
                if (x.id == id) {
                    return x;
                }
            }
        } catch (NumberFormatException ex) {
            // cae al return null
        }
        return null;
    }

    // Muestra una lista numerada y devuelve el indice elegido (0-based), o -1 si es invalido
    private static int elegir(Scanner sc, String titulo, String[] opciones) {
        System.out.println(titulo);
        for (int i = 0; i < opciones.length; i++) {
            System.out.println((i + 1) + ". " + opciones[i]);
        }
        System.out.print("Opcion: ");
        try {
            int n = Integer.parseInt(sc.nextLine());
            if (n >= 1 && n <= opciones.length) {
                return n - 1;
            }
        } catch (NumberFormatException ex) {
            // cae al mensaje de abajo
        }
        System.out.println("Opcion invalida");
        return -1;
    }

    // Lista todas las solicitudes con su estado y responsable
    private static void listarSolicitudes(GestorSolicitudes g) {
        System.out.println("--- Solicitudes ---");
        for (Solicitud s : g.solicitudes) {
            String r = (s.resp == null) ? "SIN ASIGNAR" : s.resp.nombre + " (" + s.resp.area + ")";
            System.out.println("#" + s.id + " | " + s.est.nombre + " | " + s.tipo
                    + " | Prioridad " + s.prioridad + " | " + s.estado + " | " + r);
        }
    }
}