package stats;

import pokemon.EstadisticaPokemon;
import battleship.EstadisticaBattleship;
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GestorEstadisticas {
    private static final String ARCHIVO_POKEMON = "stats_pokemon.txt";
    private static final String ARCHIVO_BATTLESHIP = "stats_battleship.txt";

    private static List<EstadisticaPokemon> statsPokemon = new ArrayList<>();
    private static List<EstadisticaBattleship> statsBattleship = new ArrayList<>();

    // Cargar datos al iniciar el programa
    static {
        cargarEstadisticasPokemon();
        cargarEstadisticasBattleship();
    }

    public static void registrarStatPokemon(EstadisticaPokemon stat) {
        statsPokemon.add(stat);
        Collections.sort(statsPokemon);
        guardarEstadisticasPokemon();
    }

    /** true si la partida entra al Top 3 (hay menos de 3 registros o supera al tercer puesto). */
    public static boolean esRecordBattleship(long tiempoSegundos, int barcosVivos) {
        if (statsBattleship.size() < 3) return true;
        EstadisticaBattleship candidato = new EstadisticaBattleship("", tiempoSegundos, barcosVivos);
        return candidato.compareTo(statsBattleship.get(2)) < 0;
    }

    public static void registrarStatBattleship(EstadisticaBattleship stat) {
        statsBattleship.add(stat);
        Collections.sort(statsBattleship);
        guardarEstadisticasBattleship();
    }

    // ==========================================
    // MÉTODOS TOP 3 PARA EL MENÚ (AQUÍ ESTÁ EL FIX)
    // ==========================================
    public static void mostrarTop3Pokemon() {
        System.out.println("\n--- TOP 3 MEJORES JUGADORES POKÉMON ---");
        if (statsPokemon.isEmpty()) {
            System.out.println("No hay partidas registradas.");
            return;
        }
        for (int i = 0; i < Math.min(3, statsPokemon.size()); i++) {
            System.out.println((i + 1) + ". " + statsPokemon.get(i));
        }
    }

    public static void mostrarTop3Battleship() {
        System.out.println("\n--- TOP 3 MEJORES JUGADORES BATTLESHIP ---");
        if (statsBattleship.isEmpty()) {
            System.out.println("No hay partidas registradas.");
            return;
        }
        for (int i = 0; i < Math.min(3, statsBattleship.size()); i++) {
            System.out.println((i + 1) + ". " + statsBattleship.get(i));
        }
    }

    // ==========================================
    // PERSISTENCIA DE DATOS (ARCHIVOS)
    // ==========================================
    private static void guardarEstadisticasPokemon() {
        try (PrintWriter out = new PrintWriter(new FileWriter(ARCHIVO_POKEMON))) {
            for (EstadisticaPokemon stat : statsPokemon) {
                out.println(stat.getNickname() + "," + stat.getTiempoSegundos() + "," + stat.getPokemonesRestantes() + "," + stat.getPsMinimos());
            }
        } catch (IOException e) {
            System.out.println("Error al guardar stats de Pokémon.");
        }
    }

    private static void cargarEstadisticasPokemon() {
        statsPokemon.clear();
        File archivo = new File(ARCHIVO_POKEMON);
        if (!archivo.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] partes = linea.split(",");
                if (partes.length == 4) {
                    statsPokemon.add(new EstadisticaPokemon(
                            partes[0], Long.parseLong(partes[1]), Integer.parseInt(partes[2]), Integer.parseInt(partes[3])
                    ));
                }
            }
            Collections.sort(statsPokemon);
        } catch (Exception e) {}
    }

    private static void guardarEstadisticasBattleship() {
        try (PrintWriter out = new PrintWriter(new FileWriter(ARCHIVO_BATTLESHIP))) {
            for (EstadisticaBattleship stat : statsBattleship) {
                out.println(stat.getNickname().replace(",", " ") + "," + stat.getTiempoSegundos() + "," + stat.getBarcosVivos());
            }
        } catch (IOException e) {
            System.out.println("Error al guardar stats de Battleship.");
        }
    }

    private static void cargarEstadisticasBattleship() {
        statsBattleship.clear();
        File archivo = new File(ARCHIVO_BATTLESHIP);
        if (!archivo.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] partes = linea.split(",");
                if (partes.length == 3) {
                    try {
                        statsBattleship.add(new EstadisticaBattleship(
                                partes[0], Long.parseLong(partes[1].trim()), Integer.parseInt(partes[2].trim())));
                    } catch (NumberFormatException e) {
                        // línea dañada: se ignora sin perder las demás
                    }
                }
            }
            Collections.sort(statsBattleship);
        } catch (IOException e) {
            System.out.println("Error al cargar stats de Battleship.");
        }
    }
}
