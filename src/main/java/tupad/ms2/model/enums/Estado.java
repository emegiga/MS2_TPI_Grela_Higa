package tupad.ms2.model.enums;

public enum Estado {
    PENDIENTE,
    EN_PROCESO,
    RESUELTA,
    RECHAZADA;

    /**
     * Reglas del ciclo de vida de una solicitud:
     * PENDIENTE -> EN_PROCESO | RECHAZADA, EN_PROCESO -> RESUELTA | RECHAZADA.
     * Los estados cerrados no admiten transiciones.
     * El switch es exhaustivo: si se agrega un estado, el compilador obliga a definir sus transiciones.
     */
    public boolean permiteTransicionA(Estado destino) {
        return switch (this) {
            case PENDIENTE -> destino == EN_PROCESO || destino == RECHAZADA;
            case EN_PROCESO -> destino == RESUELTA || destino == RECHAZADA;
            case RESUELTA, RECHAZADA -> false;
        };
    }

    public boolean esCerrado() {
        return this == RESUELTA || this == RECHAZADA;
    }
}
