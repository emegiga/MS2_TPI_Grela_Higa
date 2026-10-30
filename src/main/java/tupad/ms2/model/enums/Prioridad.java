package tupad.ms2.model.enums;

import tupad.ms2.model.excepciones.ReglaDeNegocioException;

/**
 * Prioridad de una solicitud. El código numérico (1, 2, 3) es el que ingresa y ve el usuario.
 */
public enum Prioridad {
    ALTA(1, "Alta"),
    MEDIA(2, "Media"),
    BAJA(3, "Baja");

    private final int codigo;
    private final String etiqueta;

    Prioridad(int codigo, String etiqueta) {
        this.codigo = codigo;
        this.etiqueta = etiqueta;
    }

    public int getCodigo() {
        return codigo;
    }

    public String getEtiqueta() {
        return etiqueta;
    }

    public static Prioridad desdeCodigo(int codigo) {
        for (Prioridad prioridad : values()) {
            if (prioridad.codigo == codigo) {
                return prioridad;
            }
        }
        throw new ReglaDeNegocioException("prioridad invalida");
    }
}
