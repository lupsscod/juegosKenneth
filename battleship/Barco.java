package battleship;

public class Barco {
    private String nombre;
    private int tamaño;
    private int impactosRecibidos;

    public Barco(String nombre, int tamaño) {
        this.nombre = nombre;
        this.tamaño = tamaño;
        this.impactosRecibidos = 0;
    }

    public void registrarImpacto() { impactosRecibidos++; }
    public boolean estaHundido() { return impactosRecibidos >= tamaño; }
    public String getNombre() { return nombre; }
    public int getTamaño() { return tamaño; }
}