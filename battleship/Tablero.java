package battleship;

public class Tablero {
    private Barco[][] celdasBarcos; // Guarda referencias a los objetos Barco
    private char[][] mapaVisible;   // Lo que ve el enemigo: '~', 'X', '-'
    private Barco[] flota;

    public Tablero() {
        celdasBarcos = new Barco[10][10];
        mapaVisible = new char[10][10];
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                mapaVisible[i][j] = '~';
            }
        }
        // Configuración solicitada: 3 barcos de tamaños 2, 3 y 4
        flota = new Barco[]{
                new Barco("Patrullero", 2),
                new Barco("Submarino", 3),
                new Barco("Acorazado", 4)
        };
    }

    public Barco[] getFlota() { return flota; }

    public boolean colocarBarcoManual(Barco barco, int f, int c, char orientacion) {
        int dr = (orientacion == 'V' || orientacion == 'v') ? 1 : 0;
        int dc = (orientacion == 'H' || orientacion == 'h') ? 1 : 0;

        if (dr == 0 && dc == 0) return false; // Orientación inválida

        // Validar que no se salga del tablero
        if (f + dr * barco.getTamaño() > 10 || c + dc * barco.getTamaño() > 10) return false;

        // Validar que no haya otro barco superpuesto
        for (int i = 0; i < barco.getTamaño(); i++) {
            if (celdasBarcos[f + dr * i][c + dc * i] != null) return false;
        }

        // Colocar referencias del barco en la matriz
        for (int i = 0; i < barco.getTamaño(); i++) {
            celdasBarcos[f + dr * i][c + dc * i] = barco;
        }
        return true;
    }

    public String recibirDisparo(int f, int c) {
        if (f < 0 || f > 9 || c < 0 || c > 9) return "FUERA";
        if (mapaVisible[f][c] != '~') return "REPETIDO";

        Barco b = celdasBarcos[f][c];
        if (b != null) {
            mapaVisible[f][c] = 'X';
            b.registrarImpacto();
            if (b.estaHundido()) return "HUNDIDO:" + b.getNombre();
            return "IMPACTO";
        } else {
            mapaVisible[f][c] = '-';
            return "AGUA";
        }
    }

    public boolean todosHundidos() {
        for (Barco b : flota) {
            if (!b.estaHundido()) return false;
        }
        return true;
    }

    public int barcosVivos() {
        int vivos = 0;
        for (Barco b : flota) {
            if (!b.estaHundido()) vivos++;
        }
        return vivos;
    }

    public void imprimirVisible() {
        System.out.println("  0 1 2 3 4 5 6 7 8 9");
        for (int i = 0; i < 10; i++) {
            System.out.print(i + " ");
            for (int j = 0; j < 10; j++) {
                System.out.print(mapaVisible[i][j] + " ");
            }
            System.out.println();
        }
    }

    public void imprimirPropio() {
        System.out.println("  0 1 2 3 4 5 6 7 8 9");
        for(int i=0; i<10; i++) {
            System.out.print(i + " ");
            for(int j=0; j<10; j++) {
                if (celdasBarcos[i][j] != null) System.out.print("O ");
                else System.out.print("~ ");
            }
            System.out.println();
        }
    }
}