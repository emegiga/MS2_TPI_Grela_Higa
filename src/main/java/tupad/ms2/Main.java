package tupad.ms2;

import tupad.ms2.model.Estudiante;
import tupad.ms2.model.GestorSolicitudes;
import tupad.ms2.model.Responsable;
import tupad.ms2.model.Solicitud;
import tupad.ms2.model.enums.Estado;
import tupad.ms2.model.excepciones.ReglaDeNegocioException;

import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        GestorSolicitudes gestor = new GestorSolicitudes();
        Scanner scanner = new Scanner(System.in);
        boolean salir = false;

        cargarDatosEjemplo(gestor);

        while (!salir) {
            mostrarMenu();
            String opcion = scanner.nextLine();

            if (opcion.equals("1")) {
                registrarSolicitud(scanner, gestor);
            } else if (opcion.equals("2")) {
                cambiarEstado(scanner, gestor);
            } else if (opcion.equals("3")) {
                asignarResponsable(scanner, gestor);
            } else if (opcion.equals("4")) {
                buscarSolicitudes(scanner, gestor);
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

    private static void mostrarMenu() {
        System.out.println();
        System.out.println("######################## GESTION DE SOLICITUDES ACADEMICAS ########################");
        System.out.println("1. Registrar solicitud");
        System.out.println("2. Cambiar estado");
        System.out.println("3. Asignar responsable");
        System.out.println("4. Buscar / filtrar");
        System.out.println("5. Reporte");
        System.out.println("0. Salir");
        System.out.print("Opcion: ");
    }

    private static void registrarSolicitud(Scanner scanner, GestorSolicitudes gestor) {
        if (gestor.estudiantes.isEmpty()) {
            System.out.println("No hay estudiantes cargados.");
            return;
        }
        Estudiante estudiante = elegirEstudiante(scanner, gestor);
        if (estudiante == null) {
            System.out.println("Registro cancelado.");
            return;
        }
        String[] tipos = {"CONSTANCIA", "EQUIVALENCIA", "RECLAMO", "OTRO"};
        int indiceTipo = elegir(scanner, "Tipo:", tipos);
        if (indiceTipo == -1) {
            System.out.println("Registro cancelado.");
            return;
        }
        System.out.print("Descripcion: ");
        String descripcion = scanner.nextLine();
        System.out.print("Prioridad (1=alta, 2=media, 3=baja): ");
        int prioridad = Integer.parseInt(scanner.nextLine());
        try {
            Solicitud registrada = gestor.registrarSolicitud(estudiante.legajo, tipos[indiceTipo], descripcion, prioridad);
            System.out.println("Solicitud registrada con id " + registrada.id);
        } catch (ReglaDeNegocioException ex) {
            mostrarError(ex);
        }
    }

    private static Estudiante elegirEstudiante(Scanner scanner, GestorSolicitudes gestor) {
        String[] opcionesEstudiantes = new String[gestor.estudiantes.size()];
        for (int i = 0; i < opcionesEstudiantes.length; i++) {
            Estudiante estudiante = gestor.estudiantes.get(i);
            opcionesEstudiantes[i] = estudiante.legajo + " - " + estudiante.nombre + " (" + estudiante.email + ")";
        }
        int indiceEstudiante = elegir(scanner, "Estudiante:", opcionesEstudiantes);
        return indiceEstudiante == -1 ? null : gestor.estudiantes.get(indiceEstudiante);
    }

    private static void cambiarEstado(Scanner scanner, GestorSolicitudes gestor) {
        Solicitud solicitud = elegirSolicitudAbierta(scanner, gestor);
        if (solicitud == null) {
            return;
        }
        String[] estados;
        if (solicitud.responsable == null) {
            System.out.println("La solicitud #" + solicitud.id + " no tiene responsable asignado. "
                    + "Solo puede rechazarse; para otros estados asignelo desde la opcion 3.");
            estados = new String[]{"RECHAZADA"};
        } else {
            estados = new String[]{"EN_PROCESO", "RESUELTA", "RECHAZADA"};
        }
        int indiceEstado = elegir(scanner, "Nuevo estado:", estados);
        if (indiceEstado == -1) {
            return;
        }
        try {
            Solicitud actualizada = gestor.cambiarEstado(solicitud.id, estados[indiceEstado]);
            System.out.println("Estado actualizado a " + actualizada.estado);
        } catch (ReglaDeNegocioException ex) {
            mostrarError(ex);
        }
    }

    private static void asignarResponsable(Scanner scanner, GestorSolicitudes gestor) {
        Solicitud solicitud = elegirSolicitudAbierta(scanner, gestor);
        if (solicitud == null) {
            return;
        }
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
        if (indiceResponsable == -1) {
            return;
        }
        Responsable elegido = gestor.responsables.get(indiceResponsable);
        try {
            gestor.asignarResponsable(solicitud.id, elegido.id);
            System.out.println("Responsable asignado: " + elegido.nombre);
        } catch (ReglaDeNegocioException ex) {
            mostrarError(ex);
        }
    }

    // Lista las solicitudes, pide un id y devuelve la solicitud si existe y no esta cerrada.
    // Informa el motivo y devuelve null en cualquier otro caso.
    private static Solicitud elegirSolicitudAbierta(Scanner scanner, GestorSolicitudes gestor) {
        if (gestor.solicitudes.isEmpty()) {
            System.out.println("No hay solicitudes cargadas.");
            return null;
        }
        listarSolicitudes(gestor);
        Solicitud solicitud = pedirSolicitud(scanner, gestor);
        if (solicitud == null) {
            System.out.println("Error: solicitud no encontrada");
            return null;
        }
        if (solicitud.estado == Estado.RESUELTA || solicitud.estado == Estado.RECHAZADA) {
            System.out.println("Error: la solicitud esta cerrada");
            return null;
        }
        return solicitud;
    }

    private static void buscarSolicitudes(Scanner scanner, GestorSolicitudes gestor) {
        String[] criterios = {"estado", "tipo", "legajo", "prioridad"};
        int indiceCriterio = elegir(scanner, "Buscar por:", new String[]{"Estado", "Tipo", "Legajo", "Prioridad"});
        String valor = null;

        if (indiceCriterio == 0) {
            valor = elegirValorEstado(scanner);
        } else if (indiceCriterio == 1) {
            valor = elegirValorTipo(scanner);
        } else if (indiceCriterio == 2) {
            valor = elegirValorLegajo(scanner, gestor);
        } else if (indiceCriterio == 3) {
            valor = elegirValorPrioridad(scanner);
        }

        if (valor != null) {
            mostrarResultados(gestor.buscar(criterios[indiceCriterio], valor));
        }
    }

    private static String elegirValorEstado(Scanner scanner) {
        Estado[] estados = Estado.values();
        String[] nombresEstados = new String[estados.length];
        for (int i = 0; i < estados.length; i++) {
            nombresEstados[i] = estados[i].toString();
        }
        int indiceEstado = elegir(scanner, "Estado:", nombresEstados);
        return indiceEstado == -1 ? null : nombresEstados[indiceEstado];
    }

    private static String elegirValorTipo(Scanner scanner) {
        String[] tipos = {"CONSTANCIA", "EQUIVALENCIA", "RECLAMO", "OTRO"};
        int indiceTipo = elegir(scanner, "Tipo:", tipos);
        return indiceTipo == -1 ? null : tipos[indiceTipo];
    }

    private static String elegirValorLegajo(Scanner scanner, GestorSolicitudes gestor) {
        if (gestor.estudiantes.isEmpty()) {
            System.out.println("No hay estudiantes cargados.");
            return null;
        }
        String[] opcionesLegajos = new String[gestor.estudiantes.size()];
        for (int i = 0; i < opcionesLegajos.length; i++) {
            opcionesLegajos[i] = gestor.estudiantes.get(i).legajo + " - " + gestor.estudiantes.get(i).nombre;
        }
        int indiceEstudiante = elegir(scanner, "Estudiante:", opcionesLegajos);
        return indiceEstudiante == -1 ? null : gestor.estudiantes.get(indiceEstudiante).legajo;
    }

    private static String elegirValorPrioridad(Scanner scanner) {
        int indicePrioridad = elegir(scanner, "Prioridad:", new String[]{"Alta", "Media", "Baja"});
        return indicePrioridad == -1 ? null : String.valueOf(indicePrioridad + 1);
    }

    private static void mostrarResultados(List<Solicitud> resultados) {
        for (Solicitud solicitud : resultados) {
            System.out.println("#" + solicitud.id + " | " + solicitud.estudiante.nombre + " | " + solicitud.tipo
                    + " | Prioridad: " + solicitud.prioridad + " | " + solicitud.estado);
        }
        System.out.println("Resultados: " + resultados.size());
    }

    // Precarga de solicitudes al iniciar (estudiantes y responsables ya vienen cargados desde GestorSolicitudes).
    // Como el gestor ya no imprime, no hace falta silenciar la salida estandar.
    private static void cargarDatosEjemplo(GestorSolicitudes gestor) {
        gestor.registrarSolicitud("L1001", "CONSTANCIA", "Constancia de alumno regular", 2);
        gestor.registrarSolicitud("L1002", "RECLAMO", "Error en nota del parcial", 1);
        gestor.registrarSolicitud("L1001", "EQUIVALENCIA", "Equivalencia de Matematica I", 3);
        gestor.asignarResponsable(2, 1);   // Maria Ortega Sánchez (Alumnado)
        gestor.cambiarEstado(2, "EN_PROCESO");
    }

    private static void mostrarError(ReglaDeNegocioException ex) {
        System.out.println("Error: " + ex.getMessage());
    }

    // Pide un ID y devuelve la solicitud correspondiente, o null si no existe o no es un numero
    private static Solicitud pedirSolicitud(Scanner scanner, GestorSolicitudes gestor) {
        System.out.print("Id solicitud: ");
        try {
            int id = Integer.parseInt(scanner.nextLine().trim());
            return gestor.buscarSolicitudPorId(id);
        } catch (NumberFormatException ex) {
            return null;
        }
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
            // un texto no numerico se trata igual que un numero fuera de rango
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
