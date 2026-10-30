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
        GestorSolicitudes gestor = new GestorSolicitudes();
        Scanner scanner = new Scanner(System.in);
        boolean salir = false;

        cargarDatosEjemplo(gestor);

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
            String opcion = scanner.nextLine();

            if (opcion.equals("1")) {
                if (gestor.estudiantes.isEmpty()) {
                    System.out.println("No hay estudiantes cargados.");
                } else {
                    String[] opcionesEstudiantes = new String[gestor.estudiantes.size()];
                    for (int i = 0; i < opcionesEstudiantes.length; i++) {
                        Estudiante estudiante = gestor.estudiantes.get(i);
                        opcionesEstudiantes[i] = estudiante.legajo + " - " + estudiante.nombre + " (" + estudiante.email + ")";
                    }
                    int indiceEstudiante = elegir(scanner, "Estudiante:", opcionesEstudiantes);

                    if (indiceEstudiante == -1) {
                        System.out.println("Registro cancelado.");
                    } else {
                        Estudiante estudiante = gestor.estudiantes.get(indiceEstudiante);

                        String[] tipos = {"CONSTANCIA", "EQUIVALENCIA", "RECLAMO", "OTRO"};
                        int indiceTipo = elegir(scanner, "Tipo:", tipos);
                        if (indiceTipo == -1) {
                            System.out.println("Registro cancelado.");
                        } else {
                            System.out.print("Descripcion: ");
                            String descripcion = scanner.nextLine();
                            System.out.print("Prioridad (1=alta, 2=media, 3=baja): ");
                            int prioridad = Integer.parseInt(scanner.nextLine());
                            gestor.registrarSolicitud(estudiante.legajo, tipos[indiceTipo], descripcion, prioridad);
                        }
                    }
                }

            } else if (opcion.equals("2")) {
                if (gestor.solicitudes.isEmpty()) {
                    System.out.println("No hay solicitudes cargadas.");
                } else {
                    listarSolicitudes(gestor);
                    Solicitud solicitud = pedirSolicitud(scanner, gestor);

                    if (solicitud == null) {
                        System.out.println("Error: solicitud no encontrada");
                    } else if (solicitud.estado == Estado.RESUELTA || solicitud.estado == Estado.RECHAZADA) {
                        System.out.println("Error: la solicitud esta cerrada");
                    } else {
                        String[] estados;
                        if (solicitud.responsable == null) {
                            System.out.println("La solicitud #" + solicitud.id + " no tiene responsable asignado. "
                                    + "Solo puede rechazarse; para otros estados asignelo desde la opcion 3.");
                            estados = new String[]{"RECHAZADA"};
                        } else {
                            estados = new String[]{"EN_PROCESO", "RESUELTA", "RECHAZADA"};
                        }
                        int indiceEstado = elegir(scanner, "Nuevo estado:", estados);
                        if (indiceEstado != -1) {
                            gestor.cambiarEstado(solicitud.id, estados[indiceEstado]);
                        }
                    }
                }

            } else if (opcion.equals("3")) {
                if (gestor.solicitudes.isEmpty()) {
                    System.out.println("No hay solicitudes cargadas.");
                } else {
                    listarSolicitudes(gestor);
                    Solicitud solicitud = pedirSolicitud(scanner, gestor);

                    if (solicitud == null) {
                        System.out.println("Error: solicitud no encontrada");
                    } else if (solicitud.estado == Estado.RESUELTA || solicitud.estado == Estado.RECHAZADA) {
                        System.out.println("Error: la solicitud esta cerrada");
                    } else {
                        if (solicitud.responsable != null) {
                            System.out.println("Responsable actual: " + solicitud.responsable.nombre + " ("
                                    + solicitud.responsable.area + "). Si elegis otro, se reemplaza.");
                        }

                        String[] opcionesResponsables = new String[gestor.responsables.size()];
                        for (int i = 0; i < opcionesResponsables.length; i++) {
                            Responsable responsable = gestor.responsables.get(i);
                            opcionesResponsables[i] = responsable.nombre + " (" + responsable.area + ")";
                        }

                        int indiceResponsable = elegir(scanner, "Responsable:", opcionesResponsables);
                        if (indiceResponsable != -1) {
                            Responsable elegido = gestor.responsables.get(indiceResponsable);
                            gestor.asignarResponsable(solicitud.id, elegido.id);
                        }
                    }
                }

            } else if (opcion.equals("4")) {
                String[] criterios = {"estado", "tipo", "legajo", "prioridad"};
                int indiceCriterio = elegir(scanner, "Buscar por:", new String[]{"Estado", "Tipo", "Legajo", "Prioridad"});
                String valor = null;

                if (indiceCriterio == 0) {
                    Estado[] estados = Estado.values();
                    String[] nombresEstados = new String[estados.length];
                    for (int i = 0; i < estados.length; i++) {
                        nombresEstados[i] = estados[i].toString();
                    }
                    int indiceEstado = elegir(scanner, "Estado:", nombresEstados);
                    if (indiceEstado != -1) valor = nombresEstados[indiceEstado];

                } else if (indiceCriterio == 1) {
                    String[] tipos = {"CONSTANCIA", "EQUIVALENCIA", "RECLAMO", "OTRO"};
                    int indiceTipo = elegir(scanner, "Tipo:", tipos);
                    if (indiceTipo != -1) valor = tipos[indiceTipo];

                } else if (indiceCriterio == 2) {
                    if (gestor.estudiantes.isEmpty()) {
                        System.out.println("No hay estudiantes cargados.");
                    } else {
                        String[] opcionesLegajos = new String[gestor.estudiantes.size()];
                        for (int i = 0; i < opcionesLegajos.length; i++) {
                            opcionesLegajos[i] = gestor.estudiantes.get(i).legajo + " - " + gestor.estudiantes.get(i).nombre;
                        }
                        int indiceEstudiante = elegir(scanner, "Estudiante:", opcionesLegajos);
                        if (indiceEstudiante != -1) valor = gestor.estudiantes.get(indiceEstudiante).legajo;
                    }

                } else if (indiceCriterio == 3) {
                    int indicePrioridad = elegir(scanner, "Prioridad:", new String[]{"Alta", "Media", "Baja"});
                    if (indicePrioridad != -1) valor = String.valueOf(indicePrioridad + 1);
                }

                if (valor != null) {
                    List<Solicitud> resultados = gestor.buscar(criterios[indiceCriterio], valor);
                    for (Solicitud solicitud : resultados) {
                        System.out.println("#" + solicitud.id + " | " + solicitud.estudiante.nombre + " | " + solicitud.tipo
                                + " | Prioridad: " + solicitud.prioridad + " | " + solicitud.estado);
                    }
                    System.out.println("Resultados: " + resultados.size());
                }

            } else if (opcion.equals("5")) {
                gestor.reporte();
            } else if (opcion.equals("0")) {
                salir = true;
            } else {
                System.out.println("Opcion invalida");
            }
        }
        scanner.close();
    }

    // Precarga de solicitudes al iniciar, sin mostrar los mensajes del gestor (estudiantes y responsables ya vienen cargados desde GestorSolicitudes)
    private static void cargarDatosEjemplo(GestorSolicitudes gestor) {
        PrintStream salidaOriginal = System.out;
        System.setOut(new PrintStream(OutputStream.nullOutputStream()));
        try {
            gestor.registrarSolicitud("L1001", "CONSTANCIA", "Constancia de alumno regular", 2);
            gestor.registrarSolicitud("L1002", "RECLAMO", "Error en nota del parcial", 1);
            gestor.registrarSolicitud("L1001", "EQUIVALENCIA", "Equivalencia de Matematica I", 3);
            gestor.asignarResponsable(2, 1);   // Maria Ortega Sánchez (Alumnado)
            gestor.cambiarEstado(2, "EN_PROCESO");
        } finally {
            System.setOut(salidaOriginal);
        }
    }

    // Pide un ID y devuelve la solicitud correspondiente, o null si no existe o no es un numero
    private static Solicitud pedirSolicitud(Scanner scanner, GestorSolicitudes gestor) {
        System.out.print("Id solicitud: ");
        try {
            int id = Integer.parseInt(scanner.nextLine().trim());
            for (Solicitud candidata : gestor.solicitudes) {
                if (candidata.id == id) {
                    return candidata;
                }
            }
        } catch (NumberFormatException ex) {
            // cae al return null
        }
        return null;
    }

    // Muestra una lista numerada y devuelve el indice elegido (0-based), o -1 si es invalido
    private static int elegir(Scanner scanner, String titulo, String[] opciones) {
        System.out.println(titulo);
        for (int i = 0; i < opciones.length; i++) {
            System.out.println((i + 1) + ". " + opciones[i]);
        }
        System.out.print("Opcion: ");
        try {
            int numero = Integer.parseInt(scanner.nextLine());
            if (numero >= 1 && numero <= opciones.length) {
                return numero - 1;
            }
        } catch (NumberFormatException ex) {
            // cae al mensaje de abajo
        }
        System.out.println("Opcion invalida");
        return -1;
    }

    // Lista todas las solicitudes con su estado y responsable
    private static void listarSolicitudes(GestorSolicitudes gestor) {
        System.out.println("--- Solicitudes ---");
        for (Solicitud solicitud : gestor.solicitudes) {
            String responsable = (solicitud.responsable == null) ? "SIN ASIGNAR"
                    : solicitud.responsable.nombre + " (" + solicitud.responsable.area + ")";
            System.out.println("#" + solicitud.id + " | " + solicitud.estudiante.nombre + " | " + solicitud.tipo
                    + " | Prioridad " + solicitud.prioridad + " | " + solicitud.estado + " | " + responsable);
        }
    }
}
