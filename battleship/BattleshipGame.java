package battleship;

import main.Jugador;
import main.Pantalla;
import stats.GestorEstadisticas;
import java.util.Scanner;

public class BattleshipGame {

    public void iniciarPartida(Scanner scanner) {
        long inicio = System.currentTimeMillis();
        Jugador p1 = new Jugador("Player1");
        Jugador p2 = new Jugador("Player2");

        Tablero tb1 = new Tablero();
        Tablero tb2 = new Tablero();

        Pantalla.limpiar();
        System.out.println("=== BATTLESHIP - FASE DE COLOCACIÓN ===");
        System.out.println("\n" + p1.getNickname() + ", coloca tu flota:");
        colocarFlota(scanner, tb1);
        Pantalla.pausar(scanner);

        Pantalla.limpiar(); // oculta el tablero del jugador 1
        System.out.println("=== BATTLESHIP - FASE DE COLOCACIÓN ===");
        System.out.println("\n" + p2.getNickname() + ", coloca tu flota:");
        colocarFlota(scanner, tb2);
        Pantalla.pausar(scanner);

        boolean turnoP1 = true;
        int turnosTotales = 0;
        boolean hayGanador = false;

        while (!hayGanador) {
            String actual = turnoP1 ? p1.getNickname() : p2.getNickname();
            String enemigo = turnoP1 ? p2.getNickname() : p1.getNickname();
            Tablero tableroEnemigo = turnoP1 ? tb2 : tb1;

            Pantalla.limpiar();
            System.out.println("=== BATTLESHIP - BATALLA ===");
            System.out.println("\n--- Turno de " + actual + " ---");
            tableroEnemigo.imprimirVisible();

            System.out.print("Fila para disparar (0-9): ");
            int fila = scanner.nextInt();
            System.out.print("Columna para disparar (0-9): ");
            int col = scanner.nextInt();
            scanner.nextLine(); // consumir el resto de la línea

            String resultado = tableroEnemigo.recibirDisparo(fila, col);

            // Se muestra el tablero actualizado junto con el resultado del disparo
            Pantalla.limpiar();
            System.out.println("=== BATTLESHIP - BATALLA ===");
            System.out.println("\n--- Turno de " + actual + " ---");
            tableroEnemigo.imprimirVisible();

            if (resultado.startsWith("HUNDIDO")) {
                String nombreBarco = resultado.split(":")[1];
                System.out.println("\n💥 ¡BOMBA! ¡" + actual + " destruyó el " + nombreBarco + " de " + enemigo + "!");
            } else if (resultado.equals("IMPACTO")) {
                System.out.println("\n🔥 ¡IMPACTO en un barco enemigo!");
            } else if (resultado.equals("AGUA")) {
                System.out.println("\n💦 Agua...");
            } else if (resultado.equals("REPETIDO")) {
                System.out.println("\n⚠️ Ya disparaste ahí. Pierdes el turno.");
            } else {
                System.out.println("\n⚠️ Disparo fuera del mapa. Pierdes el turno.");
            }

            if (tableroEnemigo.todosHundidos()) {
                hayGanador = true;
                long fin = System.currentTimeMillis();
                long duracion = (fin - inicio) / 1000;

                System.out.println("\n============================================");
                System.out.println("¡" + actual + " ha hundido toda la flota enemiga!");
                System.out.println("============================================");
                Jugador ganadorJugador = turnoP1 ? p1 : p2;
                int barcosVivos = (turnoP1 ? tb1 : tb2).barcosVivos();
                System.out.println("Duración: " + duracion + "s | Barcos que te quedaron vivos: " + barcosVivos);

                if (GestorEstadisticas.esRecordBattleship(duracion, barcosVivos)) {
                    System.out.println("🏆 ¡Rompiste un récord! Entras al Top 3.");
                    System.out.print("Ingresa tu Nickname real para estadísticas: ");
                    String nick = scanner.nextLine().trim();
                    if (nick.isEmpty()) nick = ganadorJugador.getNickname();
                    ganadorJugador.setNickname(nick);
                    GestorEstadisticas.registrarStatBattleship(new EstadisticaBattleship(nick, duracion, barcosVivos));
                    System.out.println("¡Récord guardado!");
                } else {
                    System.out.println("No superaste ningún récord del Top 3 esta vez.");
                }
                Pantalla.pausar(scanner);
            } else {
                Pantalla.pausar(scanner); // deja leer el resultado antes de cambiar de turno
                turnoP1 = !turnoP1;
                turnosTotales++;
            }
        }
    }

    private void colocarFlota(Scanner scanner, Tablero tablero) {
        for (Barco b : tablero.getFlota()) {
            boolean colocado = false;
            String aviso = "";
            while (!colocado) {
                tablero.imprimirPropio();
                if (!aviso.isEmpty()) System.out.println(aviso);
                System.out.println("Colocando: " + b.getNombre() + " (Tamaño: " + b.getTamaño() + " espacios)");
                System.out.print("Fila inicial (0-9): ");
                int f = scanner.nextInt();
                System.out.print("Columna inicial (0-9): ");
                int c = scanner.nextInt();
                System.out.print("Orientación (H para Horizontal, V para Vertical): ");
                char orientacion = scanner.next().charAt(0);
                scanner.nextLine(); // consumir el resto de la línea

                if (tablero.colocarBarcoManual(b, f, c, orientacion)) {
                    colocado = true;
                } else {
                    aviso = "Error: Posición inválida o se superpone con otro barco. Intenta de nuevo.\n";
                }
                Pantalla.limpiar();
                System.out.println("=== BATTLESHIP - FASE DE COLOCACIÓN ===\n");
            }
        }
        System.out.println("Tu tablero final:");
        tablero.imprimirPropio();
    }
}
