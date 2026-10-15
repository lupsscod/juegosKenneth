package pokemon;

public class EstadisticaPokemon implements Comparable<EstadisticaPokemon> {
    private String nickname;
    private long tiempoSegundos;
    private int pokemonesRestantes;
    private int psMinimos;

    public EstadisticaPokemon(String nickname, long tiempoSegundos, int pokemonesRestantes, int psMinimos) {
        this.nickname = nickname;
        this.tiempoSegundos = tiempoSegundos;
        this.pokemonesRestantes = pokemonesRestantes;
        this.psMinimos = psMinimos;
    }

    // Getters requeridos por el gestor de estadísticas para la persistencia
    public String getNickname() {
        return nickname;
    }

    public long getTiempoSegundos() {
        return tiempoSegundos;
    }

    public int getPokemonesRestantes() {
        return pokemonesRestantes;
    }

    public int getPsMinimos() {
        return psMinimos;
    }

    @Override
    public int compareTo(EstadisticaPokemon otra) {
        // Ordenar por más pokemones restantes, luego menor tiempo
        if (this.pokemonesRestantes != otra.pokemonesRestantes) {
            return Integer.compare(otra.pokemonesRestantes, this.pokemonesRestantes);
        }
        return Long.compare(this.tiempoSegundos, otra.tiempoSegundos);
    }

    @Override
    public String toString() {
        return nickname + " | Restantes: " + pokemonesRestantes + " | PS Min: " + psMinimos + " | Tiempo: " + tiempoSegundos + "s";
    }
}