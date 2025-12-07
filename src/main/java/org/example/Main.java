package org.example;

// Archivo: Main.java

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Avion avion = new Avion();
            GestorVuelos gestor = new GestorVuelos(avion);
            InterfazAeropuerto ventana = new InterfazAeropuerto(gestor);
            ventana.setVisible(true);
        });
    }
}