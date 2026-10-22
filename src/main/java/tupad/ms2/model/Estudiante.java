package tupad.ms2.model;

public class Estudiante {
    public int id;
    public String nombre;
    public String legajo;
    public String email;

    public Estudiante(int id, String nombre, String legajo, String email) {
        this.id = id;
        this.nombre = nombre;
        this.legajo = legajo;
        this.email = email;
    }
}
