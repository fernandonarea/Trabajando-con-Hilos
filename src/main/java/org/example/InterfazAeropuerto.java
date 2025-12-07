package org.example;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class InterfazAeropuerto extends JFrame {
    private GestorVuelos gestor;
    private JButton[] botonesAsientos;
    private JTextArea areaLog;
    private JToggleButton btnSwitchModo; // <--- EL BOTÓN NUEVO

    public InterfazAeropuerto(GestorVuelos gestor) {
        this.gestor = gestor;
        this.gestor.setUI(this);

        setTitle("Sistema de Reservas - Panel de Control");
        setSize(1100, 800);
        setLayout(new BorderLayout(5, 5));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // --- PANEL SUPERIOR (HEADER) ---
        JPanel panelHeader = new JPanel(new BorderLayout());
        panelHeader.setBorder(new EmptyBorder(5, 5, 5, 5));

        // 1. Título
        JLabel lblTitulo = new JLabel(" Sistema de Reservas Aéreas", SwingConstants.LEFT);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 20));
        panelHeader.add(lblTitulo, BorderLayout.WEST);

        // 2. BOTÓN SWITCH DE MODOS (NUEVO)
        btnSwitchModo = new JToggleButton("MODO: SEGURO (PROTEGIDO)");
        btnSwitchModo.setBackground(new Color(46, 204, 113)); // Verde Esmeralda
        btnSwitchModo.setForeground(Color.WHITE);
        btnSwitchModo.setFont(new Font("Arial", Font.BOLD, 14));
        btnSwitchModo.setPreferredSize(new Dimension(300, 40));
        btnSwitchModo.setSelected(true); // Empieza presionado (Seguro)

        btnSwitchModo.addActionListener(e -> {
            if (btnSwitchModo.isSelected()) {
                gestor.setModoSeguro(true);
                btnSwitchModo.setText("MODO: SEGURO (PROTEGIDO)");
                btnSwitchModo.setBackground(new Color(46, 204, 113)); // Verde
            } else {
                gestor.setModoSeguro(false);
                btnSwitchModo.setText("MODO: INSEGURO (CAOS)");
                btnSwitchModo.setBackground(new Color(231, 76, 60)); // Rojo
            }
        });
        panelHeader.add(btnSwitchModo, BorderLayout.EAST);

        add(panelHeader, BorderLayout.NORTH);

        // --- PANEL CENTRAL (VENTANILLAS Y AVIÓN) ---
        JPanel panelCentral = new JPanel(new BorderLayout());

        // A. Las 3 Ventanillas
        JPanel panelVentanillas = new JPanel(new GridLayout(1, 3, 10, 0));
        panelVentanillas.setBorder(new EmptyBorder(10, 10, 10, 10));
        panelVentanillas.add(crearPanelVentanilla(1, new Color(220, 240, 255)));
        panelVentanillas.add(crearPanelVentanilla(2, new Color(255, 240, 220)));
        panelVentanillas.add(crearPanelVentanilla(3, new Color(255, 220, 240)));
        panelCentral.add(panelVentanillas, BorderLayout.NORTH);

        // B. El Mapa de Asientos
        JPanel panelAvion = new JPanel(new GridLayout(5, 6, 8, 8));
        panelAvion.setBorder(BorderFactory.createTitledBorder("Mapa de Asientos"));
        botonesAsientos = new JButton[30];
        for (int i = 0; i < 30; i++) {
            int id = i + 1;
            botonesAsientos[i] = new JButton("A" + id);
            botonesAsientos[i].setBackground(new Color(144, 238, 144));
            botonesAsientos[i].setFont(new Font("Arial", Font.BOLD, 14));
            panelAvion.add(botonesAsientos[i]);
        }
        panelCentral.add(panelAvion, BorderLayout.CENTER);

        add(panelCentral, BorderLayout.CENTER);

        // --- PANEL SUR (LOGS Y SIMULACIÓN) ---
        JPanel panelSur = new JPanel(new BorderLayout());

        areaLog = new JTextArea(8, 40);
        areaLog.setEditable(false);
        areaLog.setBackground(Color.BLACK);
        areaLog.setForeground(Color.GREEN);
        panelSur.add(new JScrollPane(areaLog), BorderLayout.CENTER);

        JButton btnSimular = new JButton("EJECUTAR SIMULACIÓN MASIVA (3 Hilos -> Asiento 5)");
        btnSimular.setBackground(new Color(75, 0, 130));
        btnSimular.setForeground(Color.WHITE);
        btnSimular.setFont(new Font("Arial", Font.BOLD, 16));
        btnSimular.addActionListener(e -> ejecutarSimulacion());
        panelSur.add(btnSimular, BorderLayout.SOUTH);

        add(panelSur, BorderLayout.SOUTH);
    }

    private void ejecutarSimulacion() {
        int asiento = 5;
        agregarLog("\n=== LANZANDO SIMULACIÓN ===");
        new Thread(() -> gestor.reservar(asiento, "BOT_RAPIDO")).start();
        new Thread(() -> gestor.reservar(asiento, "BOT_LENTO")).start();
        new Thread(() -> gestor.reservar(asiento, "BOT_MEDIO")).start();
    }

    private JPanel crearPanelVentanilla(int numero, Color colorFondo) {
        JPanel panel = new JPanel(new GridLayout(5, 1, 5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("VENTANILLA " + numero));
        panel.setBackground(colorFondo);

        JTextField txtNombre = new JTextField();
        JSpinner spinAsiento = new JSpinner(new SpinnerNumberModel(1, 1, 30, 1));

        JButton btnReservar = new JButton("RESERVAR");
        btnReservar.setBackground(new Color(34, 139, 34));
        btnReservar.setForeground(Color.WHITE);

        JButton btnCancelar = new JButton("CANCELAR");
        btnCancelar.setBackground(new Color(178, 34, 34));
        btnCancelar.setForeground(Color.WHITE);

        btnReservar.addActionListener(e -> {
            String nombre = txtNombre.getText().trim();
            int asiento = (int) spinAsiento.getValue();
            if(!nombre.isEmpty()) new Thread(() -> gestor.reservar(asiento, nombre)).start();
        });

        btnCancelar.addActionListener(e -> {
            String nombre = txtNombre.getText().trim();
            int asiento = (int) spinAsiento.getValue();
            if(!nombre.isEmpty()) new Thread(() -> gestor.cancelar(asiento, nombre)).start();
        });

        panel.add(new JLabel("Pasajero:"));
        panel.add(txtNombre);
        panel.add(new JLabel("Asiento:"));
        panel.add(spinAsiento);

        JPanel pBtns = new JPanel(new GridLayout(1, 2, 5, 0));
        pBtns.add(btnReservar);
        pBtns.add(btnCancelar);
        panel.add(pBtns);

        return panel;
    }

    public void agregarLog(String mensaje) {
        SwingUtilities.invokeLater(() -> {
            String hora = new SimpleDateFormat("HH:mm:ss").format(new Date());
            areaLog.append("[" + hora + "] " + mensaje + "\n");
            areaLog.setCaretPosition(areaLog.getDocument().getLength());
        });
    }

    public void actualizarBoton(int asiento, boolean ocupado, String cliente) {
        SwingUtilities.invokeLater(() -> {
            JButton btn = botonesAsientos[asiento - 1];
            if (ocupado) {
                btn.setBackground(Color.RED);
                btn.setText("<html>A" + asiento + "<br>" + cliente + "</html>");
            } else {
                btn.setBackground(new Color(144, 238, 144));
                btn.setText("A" + asiento);
            }
        });
    }
}