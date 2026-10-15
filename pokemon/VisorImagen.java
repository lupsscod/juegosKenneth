package pokemon;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VisorImagen extends JFrame {
    private JLabel labelP1, labelP2;
    private Image imagenFondo, imagenPokebola;
    private Map<String, Image> cacheImagenes = new HashMap<>();

    private String nickP1 = "ENTRENADOR 1", nickP2 = "ENTRENADOR 2";
    private List<Pokemon> eqP1, eqP2;
    private int actP1 = 0, actP2 = 0;
    private JPanel panelArena;
    private JFrame frameTabla;

    // Banderas para ocultar las barras de vida durante las animaciones
    private boolean animandoP1 = false;
    private boolean animandoP2 = false;

    public VisorImagen() {
        setTitle("Arena Pokémon");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setAlwaysOnTop(true);
        setFocusableWindowState(false);

        // Centrar pero desplazado a la derecha para dejar espacio a la tabla
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        int arenaX = Math.max(0, (screen.width - (800 + 450)) / 2) + 460;
        int arenaY = (screen.height - 500) / 2;
        setLocation(arenaX, arenaY);

        imagenFondo = cargarImagenSegura("https://i.pinimg.com/474x/43/e7/89/43e78957ab636946acf0498862b67c11.jpg");
        if (imagenFondo == null) imagenFondo = cargarImagenSegura("https://play.pokemonshowdown.com/sprites/gen6bg/forest.png");

        imagenPokebola = cargarImagenSegura("https://thumb.wikimedia.org/wikipedia/commons/thumb/5/51/Pokebola-pokeball-png-0.png/500px-Pokebola-pokeball-png-0.png");

        panelArena = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                if (imagenFondo != null) g.drawImage(imagenFondo, 0, 0, getWidth(), getHeight(), this);
                else { g.setColor(new Color(135, 206, 235)); g.fillRect(0, 0, getWidth(), getHeight()); }

                if (eqP1 == null || eqP2 == null) return;

                // Nombres Entrenadores
                g2.setFont(new Font("Arial", Font.BOLD, 26));
                g2.setColor(Color.BLACK);
                g2.drawString(nickP1.toUpperCase(), 52, 42);
                g2.drawString(nickP2.toUpperCase(), getWidth() - 248, 42);
                g2.setColor(Color.WHITE);
                g2.drawString(nickP1.toUpperCase(), 50, 40);
                g2.drawString(nickP2.toUpperCase(), getWidth() - 250, 40);

                dibujarHUDJugador(g2, eqP1, 50, 70);
                dibujarHUDJugador(g2, eqP2, getWidth() - 250, 70);

                // Solo dibujar la vida si el Pokémon NO se está moviendo (animando)
                if (eqP1.get(actP1).estaVivo() && !animandoP1) {
                    dibujarVidaCuadriculada(g2, eqP1.get(actP1), labelP1.getX() + 75, labelP1.getY() + 20);
                }
                if (eqP2.get(actP2).estaVivo() && !animandoP2) {
                    dibujarVidaCuadriculada(g2, eqP2.get(actP2), labelP2.getX() - 10, labelP2.getY() + 10);
                }
            }
        };
        panelArena.setLayout(null);

        labelP1 = new JLabel("", SwingConstants.CENTER);
        labelP1.setBounds(80, 240, 220, 220);

        labelP2 = new JLabel("", SwingConstants.CENTER);
        labelP2.setBounds(520, 140, 160, 160);

        panelArena.add(labelP1);
        panelArena.add(labelP2);
        setContentPane(panelArena);
    }

    private void dibujarVidaCuadriculada(Graphics2D g, Pokemon p, int x, int y) {
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 12));
        g.drawString("Tipo: " + String.join("/", p.getTipos()).toUpperCase(), x, y - 25);

        String hpTexto = "PS: " + p.getHpActual() + "/" + p.getHpMax();

        g.setColor(new Color(40, 40, 40));
        g.fillRoundRect(x - 2, y - 20, 154, 24, 5, 5);

        g.setColor(Color.DARK_GRAY);
        g.fillRect(x, y - 18, 150, 20);

        double ratio = Math.max(0, (double) p.getHpActual() / p.getHpMax());
        int width = (int) (150 * ratio);

        if (ratio > 0.5) g.setColor(new Color(50, 200, 50));
        else if (ratio > 0.2) g.setColor(new Color(220, 200, 0));
        else g.setColor(new Color(220, 50, 50));

        g.fillRect(x, y - 18, width, 20);

        g.setColor(new Color(0, 0, 0, 150));
        for (int i = 1; i < 15; i++) {
            g.drawLine(x + (10 * i), y - 18, x + (10 * i), y + 2);
        }

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 14));
        g.drawString(hpTexto, x + 35, y - 3);
    }

    public void mostrarTablaTipos() {
        frameTabla = new JFrame("Tabla de Tipos");
        frameTabla.setSize(450, 500);

        // Se ancla exactamente a la izquierda de la Arena
        frameTabla.setLocation(this.getX() - frameTabla.getWidth() - 10, this.getY());
        frameTabla.setAlwaysOnTop(true);
        frameTabla.setFocusableWindowState(false);

        Image imgTipos = cargarImagenSegura("https://i.pinimg.com/originals/ce/c4/b0/cec4b0ca238a0cda8118d09fbbdc9dd4.jpg");
        if(imgTipos == null) imgTipos = cargarImagenSegura("https://img.pokemondb.net/images/typechart.png");

        if (imgTipos != null) {
            JLabel lblTabla = new JLabel(new ImageIcon(imgTipos.getScaledInstance(430, 460, Image.SCALE_SMOOTH)));
            frameTabla.add(new JScrollPane(lblTabla));
        } else {
            frameTabla.add(new JLabel("No se pudo cargar la tabla.", SwingConstants.CENTER));
        }

        frameTabla.setVisible(true);
    }

    private void dibujarHUDJugador(Graphics2D g, List<Pokemon> equipo, int startX, int startY) {
        int x = startX;
        for (int i = 0; i < 3; i++) {
            if (i < equipo.size()) {
                Pokemon p = equipo.get(i);
                if (p.estaVivo()) {
                    if (imagenPokebola != null) g.drawImage(imagenPokebola, x, startY, 30, 30, null);
                    else {
                        g.setColor(Color.WHITE); g.fillOval(x, startY, 30, 30);
                        g.setColor(Color.RED); g.fillArc(x, startY, 30, 30, 0, 180);
                        g.setColor(Color.BLACK); g.drawOval(x, startY, 30, 30);
                        g.drawLine(x, startY+15, x+30, startY+15);
                        g.setColor(Color.WHITE); g.fillOval(x+10, startY+10, 10, 10);
                        g.setColor(Color.BLACK); g.drawOval(x+10, startY+10, 10, 10);
                    }
                    g.setColor(new Color(255, 255, 255, 200));
                    g.fillOval(x + 5, startY + 5, 20, 20);
                    g.setColor(Color.BLACK);
                    g.setFont(new Font("Arial", Font.BOLD, 14));
                    g.drawString(String.valueOf(i + 1), x + 11, startY + 20);

                    Image mini = cacheImagenes.get(p.getSpriteUrl());
                    if (mini != null) g.drawImage(mini, x - 10, startY + 35, 50, 50, null);
                } else {
                    g.setColor(new Color(230, 230, 230));
                    g.fillOval(x, startY, 30, 30);
                    g.setColor(Color.DARK_GRAY);
                    g.drawOval(x, startY, 30, 30);
                    g.setColor(new Color(255, 255, 255, 120));
                    g.fillOval(x + 5, startY + 5, 20, 20);
                    g.setColor(Color.GRAY);
                    g.setFont(new Font("Arial", Font.BOLD, 14));
                    g.drawString(String.valueOf(i + 1), x + 11, startY + 20);
                }
            }
            x += 50;
        }
    }

    public void actualizarEstado(String nP1, List<Pokemon> eq1, String nP2, List<Pokemon> eq2, int act1, int act2) {
        this.nickP1 = nP1; this.eqP1 = eq1;
        this.nickP2 = nP2; this.eqP2 = eq2;
        this.actP1 = act1; this.actP2 = act2;
        for(Pokemon p : eq1) precargarImagen(p.getSpriteUrl());
        for(Pokemon p : eq2) precargarImagen(p.getSpriteUrl());
        if (eq1.get(act1).estaVivo()) configurarLabel(labelP1, eq1.get(act1));
        if (eq2.get(act2).estaVivo()) configurarLabel(labelP2, eq2.get(act2));
        panelArena.repaint();
    }

    private void configurarLabel(JLabel lbl, Pokemon p) {
        Image img = cacheImagenes.get(p.getSpriteUrl());
        if (img != null) lbl.setIcon(new ImageIcon(img.getScaledInstance(lbl.getWidth(), lbl.getHeight(), Image.SCALE_SMOOTH)));
    }

    // ==========================================
    // ANIMACIONES CON CONTROL DE BARRAS DE VIDA
    // ==========================================
    public void animarCambioSalida(boolean esP1) {
        if (esP1) animandoP1 = true; else animandoP2 = true;
        JLabel lbl = esP1 ? labelP1 : labelP2;
        animarTraslacion(lbl, lbl.getX(), getHeight() + 50, 20, esP1);
    }

    public void animarCambioEntrada(boolean esP1) {
        if (esP1) animandoP1 = true; else animandoP2 = true;
        JLabel lbl = esP1 ? labelP1 : labelP2;
        int targetX = esP1 ? 80 : 520;
        int targetY = esP1 ? 240 : 140;
        lbl.setLocation(esP1 ? -300 : getWidth() + 300, targetY);
        animarTraslacion(lbl, targetX, targetY, 20, esP1);
    }

    public void animarMuerte(boolean esP1) {
        if (esP1) animandoP1 = true; else animandoP2 = true;
        JLabel lbl = esP1 ? labelP1 : labelP2;
        animarTraslacion(lbl, lbl.getX(), lbl.getY() + 100, 10, esP1);
        Timer t = new Timer(500, e -> {
            lbl.setIcon(null);
            if (esP1) animandoP1 = false; else animandoP2 = false;
            panelArena.repaint();
        });
        t.setRepeats(false); t.start();
    }

    private void animarTraslacion(JLabel lbl, int targetX, int targetY, int speed, boolean esP1) {
        Timer timer = new Timer(speed, null);
        timer.addActionListener(e -> {
            int cx = lbl.getX(), cy = lbl.getY();
            if (Math.abs(cx - targetX) < 15 && Math.abs(cy - targetY) < 15) {
                lbl.setLocation(targetX, targetY);
                if (esP1) animandoP1 = false; else animandoP2 = false; // Muestra la vida al llegar
                panelArena.repaint();
                timer.stop();
            } else {
                lbl.setLocation(cx + (Integer.compare(targetX, cx) * 15), cy + (Integer.compare(targetY, cy) * 15));
            }
        });
        timer.start();
    }

    public void animarAtaqueElemental(boolean esP1, String tipoMovimiento) {
        String emojiAtaque = obtenerEmojiPorTipo(tipoMovimiento);
        JLabel proyectil = new JLabel(emojiAtaque);
        proyectil.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 40));
        int startX = esP1 ? labelP1.getX() + labelP1.getWidth()/2 : labelP2.getX() + labelP2.getWidth()/2;
        int startY = esP1 ? labelP1.getY() + labelP1.getHeight()/2 : labelP2.getY() + labelP2.getHeight()/2;
        int endX = esP1 ? labelP2.getX() + labelP2.getWidth()/2 : labelP1.getX() + labelP1.getWidth()/2;
        int endY = esP1 ? labelP2.getY() + labelP2.getHeight()/2 : labelP1.getY() + labelP1.getHeight()/2;
        proyectil.setBounds(startX, startY, 50, 50);
        panelArena.add(proyectil);
        panelArena.setComponentZOrder(proyectil, 0);
        Timer timer = new Timer(15, null);
        timer.addActionListener(e -> {
            int cx = proyectil.getX(), cy = proyectil.getY();
            if (Math.abs(cx - endX) < 20 && Math.abs(cy - endY) < 20) {
                panelArena.remove(proyectil);
                panelArena.repaint();
                timer.stop();
            } else {
                proyectil.setLocation(cx + (Integer.compare(endX, cx) * 15), cy + (Integer.compare(endY, cy) * 15));
            }
        });
        timer.start();
    }

    private Image cargarImagenSegura(String urlStr) {
        try {
            Image img = Toolkit.getDefaultToolkit().createImage(new URL(urlStr));
            ImageIcon icon = new ImageIcon(img);
            if (icon.getIconWidth() > 0) return icon.getImage();
        } catch (Exception e) {}
        try {
            HttpURLConnection conn = (HttpURLConnection) new URL(urlStr).openConnection();
            conn.setRequestProperty("User-Agent", "Mozilla/5.0");
            return ImageIO.read(conn.getInputStream());
        } catch (Exception e) { return null; }
    }

    private void precargarImagen(String url) {
        if (!cacheImagenes.containsKey(url)) {
            Image img = cargarImagenSegura(url);
            if (img != null) cacheImagenes.put(url, img);
        }
    }

    private String obtenerEmojiPorTipo(String tipo) {
        switch (tipo.toLowerCase()) {
            case "fire": return "🔥"; case "water": return "💧"; case "grass": return "🌿";
            case "electric": return "⚡"; case "ice": return "❄️"; case "fighting": return "🥊";
            case "poison": return "☠️"; case "ground": return "🪨"; case "flying": return "🌪️";
            case "psychic": return "🔮"; case "bug": return "🐛"; case "rock": return "🪨";
            case "ghost": return "👻"; case "dragon": return "🐉"; default: return "💥";
        }
    }

    public void cerrar() {
        if (frameTabla != null) frameTabla.dispose();
        this.dispose();
    }
}