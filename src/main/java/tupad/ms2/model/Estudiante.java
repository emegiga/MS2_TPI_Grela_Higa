package tupad.ms2.model;

public class Estudiante {
    private final String nombre;
    private final String legajo;
    private final String email;

    public Estudiante(String nombre, String legajo, String email) {
        this.nombre = nombre;
        this.legajo = legajo;
        this.email = email;
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
