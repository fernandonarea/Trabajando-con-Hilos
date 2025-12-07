package org.example;

public class GestorVuelos {
    private Avion avion;
    private InterfazAeropuerto ui;
    private boolean modoSeguro = true; // Por defecto arranca seguro

    public GestorVuelos(Avion avion) {
        this.avion = avion;
    }

    public void setUI(InterfazAeropuerto ui) {
        this.ui = ui;
    }

    // Método para cambiar el modo desde el botón
    public void setModoSeguro(boolean seguro) {
        this.modoSeguro = seguro;
        log("SISTEMA CAMBIADO A MODO: " + (seguro ? "SEGURO (Concurrente)" : "INSEGURO (Caos)"));
    }

    public boolean isModoSeguro() { return modoSeguro; }

    private void log(String msg) {
        if (ui != null) ui.agregarLog(msg);
        else System.out.println(msg);
    }

    // --- LÓGICA DE RESERVA ---
    public boolean reservar(int numAsiento, String cliente) {
        Asiento asiento = avion.getAsiento(numAsiento);
        if (asiento == null) return false;

        if (modoSeguro) {
            synchronized (this) {
                log("[Seguro] " + cliente + " intenta reservar A" + numAsiento);
                while (asiento.estaOcupado()) {
                    try {
                        log("... " + cliente + " EN ESPERA (Wait) por A" + numAsiento);
                        wait();
                    } catch (InterruptedException e) { return false; }
                }
                // Simulación proceso
                try { Thread.sleep(100); } catch (Exception e) {}

                asiento.reservar(cliente);
                log("ÉXITO: A" + numAsiento + " reservado a " + cliente);
                actualizarUI(numAsiento, true, cliente);
                return true;
            }
        } else {
            // =================================================
            // ☠️ CAMINO B: MODO INSEGURO (SIN SYNC Y CON ERROR)
            // =================================================
            // Nota: No hay 'synchronized' aquí. Entran todos a la vez.
            log("[Inseguro] " + cliente + " leyendo estado de A" + numAsiento);

            if (!asiento.estaOcupado()) {
                // FORZAMOS EL ERROR DE CARRERA
                // Dormimos el hilo justo antes de escribir para que otro se meta
                try { Thread.sleep(200); } catch (Exception e) {}

                asiento.reservar(cliente); // <-- AQUÍ OCURRE LA DOBLE VENTA

                log("DOBLE VENTA DETECTADA: A" + numAsiento + " asignado a " + cliente);
                actualizarUI(numAsiento, true, cliente);
                return true;
            } else {
                log("ERROR: " + cliente + " rebotó (Asiento ocupado).");
                return false;
            }
        }
    }

    // --- LÓGICA DE CANCELACIÓN ---
    public boolean cancelar(int numAsiento, String solicitante) {
        // En cancelación, usamos synchronized siempre para no corromper la memoria,
        // pero la lógica de 'notify' es lo importante.
        synchronized(this) {
            Asiento asiento = avion.getAsiento(numAsiento);

            if (asiento != null && asiento.estaOcupado()) {
                // Validación básica de nombre
                if (!asiento.getPasajero().equalsIgnoreCase(solicitante)) {
                    log("DENEGADO: " + solicitante + " no puede cancelar el asiento de " + asiento.getPasajero());
                    return false;
                }

                String exDueño = asiento.getPasajero();
                asiento.liberar();
                log("CANCELADO: A" + numAsiento + " liberado por " + solicitante);
                actualizarUI(numAsiento, false, "");

                // IMPORTANTE: Despertar a los hilos dormidos (solo útil en modo seguro)
                notifyAll();
                return true;
            }
        }
        return false;
    }

    private void actualizarUI(int num, boolean ocupado, String cliente) {
        if (ui != null) ui.actualizarBoton(num, ocupado, cliente);
    }
}