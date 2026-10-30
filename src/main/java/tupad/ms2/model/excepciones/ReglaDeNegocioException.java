package tupad.ms2.model.excepciones;

/**
 * Se lanza cuando una operación viola una regla del dominio (dato inválido,
 * transición de estado no permitida, entidad inexistente, etc.).
 * El mensaje describe el motivo y la capa de presentación decide cómo mostrarlo.
 */
public class ReglaDeNegocioException extends RuntimeException {

    public ReglaDeNegocioException(String mensaje) {
        super(mensaje);
    }
}
