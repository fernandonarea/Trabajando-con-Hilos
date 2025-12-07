package org.example;

public class Asiento {
    private int id;
    private boolean ocupado;
    private String pasajero;

    public Asiento(int id) {
        this.id = id;
        this.ocupado = false;
        this.pasajero = "";
    }

    public int getId() { return id; }
    public boolean estaOcupado() { return ocupado; }
    public String getPasajero() { return pasajero; }

    public void reservar(String nombre) {
        this.ocupado = true;
        this.pasajero = nombre;
    }

    public void liberar() {
        this.ocupado = false;
        this.pasajero = "";
    }

    // Validar si el nombre coincide para cancelar
    public boolean esDueño(String nombre) {
        return this.ocupado && this.pasajero.equalsIgnoreCase(nombre);
    }
}
