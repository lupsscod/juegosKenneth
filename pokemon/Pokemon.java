package pokemon;

import java.util.ArrayList;
import java.util.List;

public class Pokemon {
    private int id;
    private String nombre;
    private String[] tipos;
    private int hpMax;
    private int hpActual;
    private int velocidad;
    private String spriteUrl;
    private List<Movimiento> movimientos;
    private List<Movimiento> movimientosApendice;

    public Pokemon(int id, String nombre, String[] tipos, int hpMax, int velocidad) {
        this.id = id;
        this.nombre = nombre;
        this.tipos = tipos;
        this.hpMax = hpMax;
        this.hpActual = hpMax;
        this.velocidad = velocidad;
        this.spriteUrl = "https://www.serebii.net/pokemongo/pokemon/" + String.format("%03d", id) + ".png";
        this.movimientos = new ArrayList<>();
        this.movimientosApendice = new ArrayList<>();
    }

    public void agregarMovimientoApendice(Movimiento mov) { this.movimientosApendice.add(mov); }

    public void agregarMovimientoCombate(Movimiento mov) {
        if (movimientos.size() < 4) movimientos.add(mov);
    }

    public void recibirDano(int dano) {
        this.hpActual -= dano;
        if (this.hpActual < 0) this.hpActual = 0;
    }

    public boolean estaVivo() { return hpActual > 0; }

    public int getId() { return id; }
    public String getNombre() { return nombre; }
    public String[] getTipos() { return tipos; }
    public int getHpMax() { return hpMax; }
    public int getHpActual() { return hpActual; }
    public int getVelocidad() { return velocidad; }
    public String getSpriteUrl() { return spriteUrl; }
    public List<Movimiento> getMovimientos() { return movimientos; }
    public List<Movimiento> getMovimientosApendice() { return movimientosApendice; }

    public Pokemon clonarParaRevancha() {
        Pokemon copia = new Pokemon(this.getId(), this.getNombre(), this.getTipos(), this.getHpMax(), this.getVelocidad());
        for (Movimiento m : this.getMovimientos()) {
            copia.agregarMovimientoCombate(new Movimiento(
                    m.getNombre(), m.getTipo(), m.getPoder(), m.getPrecision(),
                    m.getPpMax(), m.getEfecto(), m.getFlavorText()
            ));
        }
        return copia;
    }
}