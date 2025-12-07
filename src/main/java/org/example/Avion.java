package org.example;

import java.util.ArrayList;
import java.util.List;

public class Avion {
    private List<Asiento> asientos;

    public Avion() {
        asientos = new ArrayList<>();
        for (int i = 1; i <= 30; i++) {
            asientos.add(new Asiento(i));
        }
    }

    public Asiento getAsiento(int numero) {
        if (numero < 1 || numero > 30) return null;
        return asientos.get(numero - 1);
    }
}
