package pokemon;

public class Movimiento {
    private String nombre;
    private String tipo;
    private int poder;
    private int precision;
    private int ppActual;
    private int ppMax;
    private String efecto;
    private String flavorText;

    public Movimiento(String nombre, String tipo, int poder, int precision, int pp, String efecto, String flavorText) {
        this.nombre = nombre;
        this.tipo = tipo;
        this.poder = poder;
        this.precision = precision;
        this.ppMax = pp;
        this.ppActual = pp;
        this.efecto = efecto;
        this.flavorText = flavorText;
    }

    public boolean usarPP() {
        if (ppActual > 0) {
            ppActual--;
            return true;
        }
        return false;
    }

    public String getNombre() { return nombre; }
    public String getTipo() { return tipo; }
    public int getPoder() { return poder; }
    public int getPrecision() { return precision; }
    public int getPpActual() { return ppActual; }
    public int getPpMax() { return ppMax; }
    public String getEfecto() { return efecto; }
    public String getFlavorText() { return flavorText; }
}