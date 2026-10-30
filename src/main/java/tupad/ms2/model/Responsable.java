package tupad.ms2.model;

public class Responsable {
    public int id;
    public String nombre;
    public String area;

    public Responsable(int id, String nombre, String area) {
        this.id = id;
        this.nombre = nombre;
        this.area = area;
    }

    // Formato con el que se muestra al responsable en toda la aplicacion, ej: "Laura Gorosito (Secretaria Academica)"
    public String getNombreConArea() {
        return nombre + " (" + area + ")";
    }
}
