package battleship;

public class EstadisticaBattleship implements Comparable<EstadisticaBattleship> {
    private final String nickname;
    private final long tiempoSegundos;
    private final int barcosVivos;

    public EstadisticaBattleship(String nickname, long tiempoSegundos, int barcosVivos) {
        this.nickname = nickname;
        this.tiempoSegundos = tiempoSegundos;
        this.barcosVivos = barcosVivos;
    }

    public String getNickname() { return nickname; }
    public long getTiempoSegundos() { return tiempoSegundos; }
    public int getBarcosVivos() { return barcosVivos; }

    /** Mejor = más barcos vivos al ganar; en empate, menos segundos de duración. */
    @Override
    public int compareTo(EstadisticaBattleship otra) {
        if (this.barcosVivos != otra.barcosVivos) {
            return Integer.compare(otra.barcosVivos, this.barcosVivos);
        }
        return Long.compare(this.tiempoSegundos, otra.tiempoSegundos);
    }

    @Override
    public String toString() {
        return nickname + " | Barcos vivos: " + barcosVivos + " | Tiempo: " + tiempoSegundos + "s";
    }
}
