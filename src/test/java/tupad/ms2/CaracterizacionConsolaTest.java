package tupad.ms2;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Pruebas de caracterizacion (golden master) del comportamiento externo del sistema.
 * <p>
 * Cada escenario tipea una secuencia de entradas en la consola y compara TODO lo que el
 * sistema imprime contra un archivo aprobado en src/test/resources/golden/. Los archivos
 * se generaron ejecutando la version estado-inicial, por lo que documentan su
 * comportamiento. Mientras estas pruebas sigan en verde, el refactoring no altero lo que
 * ve el usuario.
 * <p>
 * Excepcion: E07, E08 y E18 se regeneraron con la correccion CR-01 (el menu solo ofrece
 * las transiciones de estado validas). El diff de ese commit muestra exactamente que cambio.
 * <p>
 * Para regenerar los archivos: -Dgolden.regenerar=true (y revisar el diff antes de commitear)
 */
class CaracterizacionConsolaTest {

    private static final Path CARPETA_GOLDEN = Path.of("src", "test", "resources", "golden");
    private static final String MARCA_FECHA = "{HOY}";

    static Stream<Arguments> escenarios() {
        return Stream.of(
                escenario("E01_registrar_solicitud_valida",
                        "1", "5", "1", "Constancia de alumno regular para beca", "1", "5", "0"),
                escenario("E02_registrar_descripcion_corta",
                        "1", "1", "2", "abc", "2", "0"),
                escenario("E03_registrar_prioridad_fuera_de_rango",
                        "1", "1", "3", "Reclamo por nota de final", "7", "0"),
                escenario("E04_registrar_prioridad_y_descripcion_invalidas",
                        "1", "2", "4", "abc", "9", "0"),
                escenario("E05_registrar_cancelado",
                        "1", "99", "1", "1", "x", "0"),
                escenario("E06_rechazar_solicitud_sin_responsable",
                        "2", "1", "1", "5", "0"),
                escenario("E07_resolver_y_modificar_solicitud_cerrada",
                        "2", "2", "1", "2", "2", "3", "2", "0"),
                escenario("E08_solo_ofrece_transiciones_validas",
                        "3", "1", "3", "2", "1", "1", "2", "1", "3", "0"),
                escenario("E09_solicitud_inexistente",
                        "2", "99", "3", "abc", "2", "", "0"),
                escenario("E10_asignar_y_reasignar_responsable",
                        "3", "3", "3", "3", "2", "2", "3", "2", "9", "5", "0"),
                escenario("E11_buscar_por_estado",
                        "4", "1", "1", "4", "1", "2", "4", "1", "3", "4", "1", "4", "0"),
                escenario("E12_buscar_por_tipo",
                        "4", "2", "1", "4", "2", "2", "4", "2", "3", "4", "2", "4", "0"),
                escenario("E13_buscar_por_legajo",
                        "4", "3", "1", "4", "3", "2", "4", "3", "20", "0"),
                escenario("E14_buscar_por_prioridad",
                        "4", "4", "1", "4", "4", "2", "4", "4", "3", "0"),
                escenario("E15_buscar_con_opciones_invalidas",
                        "4", "9", "4", "1", "9", "4", "3", "21", "4", "4", "0", "0"),
                escenario("E16_reporte_con_datos_precargados",
                        "5", "0"),
                escenario("E17_menu_opcion_invalida",
                        "x", "", "7", "0"),
                escenario("E18_ciclo_de_vida_completo",
                        "1", "10", "3", "Nota mal cargada en el acta", "1",
                        "3", "4", "3",
                        "2", "4", "1",
                        "2", "4", "1",
                        "4", "1", "3",
                        "5", "0"),
                escenario("E19_registro_fallido_no_consume_id",
                        "1", "3", "1", "abc", "2",
                        "1", "3", "1", "Constancia para tramite de pasantia", "2",
                        "5", "0")
        );
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("escenarios")
    @DisplayName("El comportamiento por consola coincide con el golden master")
    void comportamientoExternoSeMantiene(String nombre, String[] entradas) throws IOException {
        String salidaObtenida = reemplazarFechaDeHoy(EjecutorConsola.ejecutar(entradas));

        if (Boolean.getBoolean("golden.regenerar")) {
            Files.createDirectories(CARPETA_GOLDEN);
            Files.writeString(CARPETA_GOLDEN.resolve(nombre + ".txt"), salidaObtenida, StandardCharsets.UTF_8);
        }

        assertEquals(leerGolden(nombre), salidaObtenida);
    }

    /**
     * Test de regresion del defecto CAP-16. En estado-inicial una prioridad no numerica lanzaba
     * NumberFormatException y cerraba el programa (perdiendo los datos en memoria). Es uno de los dos
     * cambios de comportamiento intencionales del trabajo (el otro es CR-01, ver E08): ahora se
     * rechaza como cualquier prioridad invalida, no consume id y el usuario sigue en el menu.
     */
    @Test
    @DisplayName("REGRESION CAP-16: una prioridad no numerica se rechaza sin cerrar el programa")
    void prioridadNoNumericaSeRechazaSinCerrarElPrograma() {
        String salida = assertDoesNotThrow(() -> EjecutorConsola.ejecutar(
                "1", "5", "1", "Constancia de alumno regular", "ALTA",
                "1", "5", "1", "Constancia de alumno regular", "1",
                "0"));

        assertTrue(salida.contains("Prioridad (1=alta, 2=media, 3=baja): Error: prioridad invalida\n"));
        assertTrue(salida.contains("Solicitud registrada con id 4\n"));
    }

    private static Arguments escenario(String nombre, String... entradas) {
        return Arguments.of(nombre, entradas);
    }

    private static String reemplazarFechaDeHoy(String salida) {
        return salida.replace(LocalDate.now().toString(), MARCA_FECHA);
    }

    private static String leerGolden(String nombre) throws IOException {
        try (InputStream golden = CaracterizacionConsolaTest.class.getResourceAsStream("/golden/" + nombre + ".txt")) {
            assertNotNull(golden, "Falta el archivo golden " + nombre + ".txt");
            return EjecutorConsola.normalizarFinesDeLinea(new String(golden.readAllBytes(), StandardCharsets.UTF_8));
        }
    }
}
