package tupad.ms2;

import tupad.ms2.datos.DatosIniciales;
import tupad.ms2.model.Estudiante;
import tupad.ms2.model.NuevaSolicitud;
import tupad.ms2.model.Responsable;
import tupad.ms2.model.Solicitud;
import tupad.ms2.model.enums.Estado;
import tupad.ms2.model.enums.Prioridad;
import tupad.ms2.model.enums.TipoSolicitud;
import tupad.ms2.model.excepciones.ReglaDeNegocioException;
import tupad.ms2.repositorio.EstudianteRepositorioEnMemoria;
import tupad.ms2.repositorio.ResponsableRepositorioEnMemoria;
import tupad.ms2.repositorio.SolicitudRepositorioEnMemoria;
import tupad.ms2.servicio.GestorSolicitudes;

import java.time.Clock;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        GestorSolicitudes gestor = new GestorSolicitudes(
                new SolicitudRepositorioEnMemoria(),
                new EstudianteRepositorioEnMemoria(DatosIniciales.estudiantes()),
                new ResponsableRepositorioEnMemoria(DatosIniciales.responsables()),
                Clock.systemDefaultZone());
        DatosIniciales.cargarSolicitudesDeEjemplo(gestor);
        Scanner scanner = new Scanner(System.in);
        boolean salir = false;

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
        if (gestor.getEstudiantes().isEmpty()) {
            System.out.println("No hay estudiantes cargados.");
            return;
        }
        Estudiante estudiante = elegirEstudiante(scanner, gestor);
        if (estudiante == null) {
            System.out.println("Registro cancelado.");
            return;
        }
        TipoSolicitud[] tipos = TipoSolicitud.values();
        int indiceTipo = elegir(scanner, "Tipo:", nombres(tipos));
        if (indiceTipo == -1) {
            System.out.println("Registro cancelado.");
            return;
        }
        System.out.print("Descripcion: ");
        String descripcion = scanner.nextLine();
        System.out.print("Prioridad (1=alta, 2=media, 3=baja): ");
        int codigoPrioridad = Integer.parseInt(scanner.nextLine());
        try {
            Prioridad prioridad = Prioridad.desdeCodigo(codigoPrioridad);
            NuevaSolicitud datos = new NuevaSolicitud(estudiante.getLegajo(), tipos[indiceTipo], prioridad, descripcion);
            Solicitud registrada = gestor.registrarSolicitud(datos);
            System.out.println("Solicitud registrada con id " + registrada.getId());
        } catch (ReglaDeNegocioException ex) {
            mostrarError(ex);
        }
    }

    private static Estudiante elegirEstudiante(Scanner scanner, GestorSolicitudes gestor) {
        String[] opcionesEstudiantes = new String[gestor.getEstudiantes().size()];
        for (int i = 0; i < opcionesEstudiantes.length; i++) {
            Estudiante estudiante = gestor.getEstudiantes().get(i);
            opcionesEstudiantes[i] = estudiante.getLegajo() + " - " + estudiante.getNombre() + " (" + estudiante.getEmail() + ")";
        }
        int indiceEstudiante = elegir(scanner, "Estudiante:", opcionesEstudiantes);
        return indiceEstudiante == -1 ? null : gestor.getEstudiantes().get(indiceEstudiante);
    }

    private static void cambiarEstado(Scanner scanner, GestorSolicitudes gestor) {
        Solicitud solicitud = elegirSolicitudAbierta(scanner, gestor);
        if (solicitud == null) {
            return;
        }
        Estado[] estados;
        if (!solicitud.tieneResponsable()) {
            System.out.println("La solicitud #" + solicitud.getId() + " no tiene responsable asignado. "
                    + "Solo puede rechazarse; para otros estados asignelo desde la opcion 3.");
            estados = new Estado[]{Estado.RECHAZADA};
        } else {
            estados = new Estado[]{Estado.EN_PROCESO, Estado.RESUELTA, Estado.RECHAZADA};
        }
        int indiceEstado = elegir(scanner, "Nuevo estado:", nombres(estados));
        if (indiceEstado == -1) {
            return;
        }
        try {
            Solicitud actualizada = gestor.cambiarEstado(solicitud.getId(), estados[indiceEstado]);
            System.out.println("Estado actualizado a " + actualizada.getEstado());
        } catch (ReglaDeNegocioException ex) {
            mostrarError(ex);
        }
    }

    private static void asignarResponsable(Scanner scanner, GestorSolicitudes gestor) {
        Solicitud solicitud = elegirSolicitudAbierta(scanner, gestor);
        if (solicitud == null) {
            return;
        }
        if (solicitud.tieneResponsable()) {
            System.out.println("Responsable actual: " + solicitud.getResponsable().getNombreConArea()
                    + ". Si elegis otro, se reemplaza.");
        }
        String[] opcionesResponsables = new String[gestor.getResponsables().size()];
        for (int i = 0; i < opcionesResponsables.length; i++) {
            opcionesResponsables[i] = gestor.getResponsables().get(i).getNombreConArea();
        }
        int indiceResponsable = elegir(scanner, "Responsable:", opcionesResponsables);
        if (indiceResponsable == -1) {
            return;
        }
        Responsable elegido = gestor.getResponsables().get(indiceResponsable);
        try {
            gestor.asignarResponsable(solicitud.getId(), elegido.getId());
            System.out.println("Responsable asignado: " + elegido.getNombre());
        } catch (ReglaDeNegocioException ex) {
            mostrarError(ex);
        }
    }

    // Lista las solicitudes, pide un id y devuelve la solicitud si existe y no esta cerrada.
    // Informa el motivo y devuelve null en cualquier otro caso.
    private static Solicitud elegirSolicitudAbierta(Scanner scanner, GestorSolicitudes gestor) {
        if (gestor.getSolicitudes().isEmpty()) {
            System.out.println("No hay solicitudes cargadas.");
            return null;
        }
        listarSolicitudes(gestor);
        Solicitud solicitud = pedirSolicitud(scanner, gestor);
        if (solicitud == null) {
            System.out.println("Error: solicitud no encontrada");
            return null;
        }
        if (solicitud.estaCerrada()) {
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
        String[] nombresEstados = nombres(Estado.values());
        int indiceEstado = elegir(scanner, "Estado:", nombresEstados);
        return indiceEstado == -1 ? null : nombresEstados[indiceEstado];
    }

    private static String elegirValorTipo(Scanner scanner) {
        String[] tipos = nombres(TipoSolicitud.values());
        int indiceTipo = elegir(scanner, "Tipo:", tipos);
        return indiceTipo == -1 ? null : tipos[indiceTipo];
    }

    private static String elegirValorLegajo(Scanner scanner, GestorSolicitudes gestor) {
        if (gestor.getEstudiantes().isEmpty()) {
            System.out.println("No hay estudiantes cargados.");
            return null;
        }
        String[] opcionesLegajos = new String[gestor.getEstudiantes().size()];
        for (int i = 0; i < opcionesLegajos.length; i++) {
            opcionesLegajos[i] = gestor.getEstudiantes().get(i).getLegajo() + " - " + gestor.getEstudiantes().get(i).getNombre();
        }
        int indiceEstudiante = elegir(scanner, "Estudiante:", opcionesLegajos);
        return indiceEstudiante == -1 ? null : gestor.getEstudiantes().get(indiceEstudiante).getLegajo();
    }

    private static String elegirValorPrioridad(Scanner scanner) {
        Prioridad[] prioridades = Prioridad.values();
        String[] etiquetas = new String[prioridades.length];
        for (int i = 0; i < prioridades.length; i++) {
            etiquetas[i] = prioridades[i].getEtiqueta();
        }
        int indicePrioridad = elegir(scanner, "Prioridad:", etiquetas);
        return indicePrioridad == -1 ? null : String.valueOf(prioridades[indicePrioridad].getCodigo());
    }

    private static void mostrarResultados(List<Solicitud> resultados) {
        for (Solicitud solicitud : resultados) {
            System.out.println("#" + solicitud.getId() + " | " + solicitud.getEstudiante().getNombre() + " | " + solicitud.getTipo()
                    + " | Prioridad: " + solicitud.getPrioridad().getCodigo() + " | " + solicitud.getEstado());
        }
        System.out.println("Resultados: " + resultados.size());
    }

    private static void mostrarError(ReglaDeNegocioException ex) {
        System.out.println("Error: " + ex.getMessage());
    }

    // Pide un ID y devuelve la solicitud correspondiente, o null si no existe o no es un numero
    private static Solicitud pedirSolicitud(Scanner scanner, GestorSolicitudes gestor) {
        System.out.print("Id solicitud: ");
        try {
            int id = Integer.parseInt(scanner.nextLine().trim());
            return gestor.buscarSolicitudPorId(id).orElse(null);
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private static String[] nombres(Enum<?>[] valores) {
        String[] nombres = new String[valores.length];
        for (int i = 0; i < valores.length; i++) {
            nombres[i] = valores[i].name();
        }
        return nombres;
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
        for (Solicitud solicitud : gestor.getSolicitudes()) {
            String responsable = solicitud.tieneResponsable() ? solicitud.getResponsable().getNombreConArea() : "SIN ASIGNAR";
            System.out.println("#" + solicitud.getId() + " | " + solicitud.getEstudiante().getNombre() + " | " + solicitud.getTipo()
                    + " | Prioridad " + solicitud.getPrioridad().getCodigo() + " | " + solicitud.getEstado() + " | " + responsable);
        }
    }
}
