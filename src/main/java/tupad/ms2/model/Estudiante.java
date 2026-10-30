package tupad.ms2.model;

public class Estudiante {
    private final int id;
    private final String nombre;
    private final String legajo;
    private final String email;

    public Estudiante(int id, String nombre, String legajo, String email) {
        this.id = id;
        this.nombre = nombre;
        this.legajo = legajo;
        this.email = email;
    }

    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getLegajo() {
        return legajo;
    }

    public String getEmail() {
        return email;
    }
}
