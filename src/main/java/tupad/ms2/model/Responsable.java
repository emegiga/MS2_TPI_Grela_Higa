package tupad.ms2.model;

public class Responsable {
    private final int id;
    private final String nombre;
    private final String area;

    public Responsable(int id, String nombre, String area) {
        this.id = id;
        this.nombre = nombre;
        this.area = area;
    }

    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    // Formato con el que se muestra al responsable en toda la aplicacion, ej: "Laura Gorosito (Secretaria Academica)"
    public String getNombreConArea() {
        return nombre + " (" + area + ")";
    }
}
