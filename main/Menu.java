package main;

import battleship.BattleshipGame;
import pokemon.PokemonGame;
import stats.GestorEstadisticas;

import java.util.Scanner;

/**
 * Menú de navegación del programa:
 *   Principal:  1. Start   2. Salir
 *   Start:      1. Pokémon 2. Battleship  0. Volver
 *   Cada juego: 1. Jugar   2. Estadísticas (Top 3)  0. Volver
 */
public class Menu {
    private final Scanner scanner;

    public Menu(Scanner scanner) {
        this.scanner = scanner;
    }

    public void mostrar() {
        boolean salir = false;
        while (!salir) {
            Pantalla.limpiar();
            System.out.println("=== MENÚ PRINCIPAL ===");
            System.out.println("1. Start");
            System.out.println("0. Salir");
            switch (leerOpcion(0, 1)) {
                case 1:
                    menuJuegos();
                    break;
                case 0:
                    salir = true;
                    System.out.println("¡Hasta luego!");
                    break;
            }
        }
    }

    private void menuJuegos() {
        boolean volver = false;
        while (!volver) {
            Pantalla.limpiar();
            System.out.println("=== ELIGE UN JUEGO ===");
            System.out.println("1. Pokémon");
            System.out.println("2. Battleship");
            System.out.println("0. Volver");
            switch (leerOpcion(0, 2)) {
                case 1:
                    menuJuego("POKÉMON");
                    break;
                case 2:
                    menuJuego("BATTLESHIP");
                    break;
                case 0:
                    volver = true;
                    break;
            }
        }
    }

    private void menuJuego(String juego) {
        boolean volver = false;
        while (!volver) {
            Pantalla.limpiar();
            System.out.println("=== " + juego + " ===");
            System.out.println("1. Jugar");
            System.out.println("2. Ver estadísticas");
            System.out.println("0. Volver");
            switch (leerOpcion(0, 2)) {
                case 1:
                    Pantalla.limpiar();
                    if (juego.equals("POKÉMON")) {
                        new PokemonGame().iniciarPartida(scanner);
                    } else {
                        new BattleshipGame().iniciarPartida(scanner);
                    }
                    break;
                case 2:
                    Pantalla.limpiar();
                    if (juego.equals("POKÉMON")) {
                        GestorEstadisticas.mostrarTop3Pokemon();
                    } else {
                        GestorEstadisticas.mostrarTop3Battleship();
                    }
                    Pantalla.pausar(scanner);
                    break;
                case 0:
                    volver = true;
                    break;
            }
        }
    }

    /** Lee una línea completa y la valida (evita errores si el usuario escribe letras). */
    private int leerOpcion(int min, int max) {
        while (true) {
            System.out.print("Elige una opción: ");
            String linea = scanner.nextLine().trim();
            try {
                int v = Integer.parseInt(linea);
                if (v >= min && v <= max) {
                    return v;
                }
            } catch (NumberFormatException e) {
                // se vuelve a pedir
            }
            System.out.println("Opción inválida. Ingresa un número entre " + min + " y " + max + ".");
        }
    }
}
