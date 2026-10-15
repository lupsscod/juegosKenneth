package main;

/**
 * Utilidad para borrar la consola.
 * - En una terminal real usa códigos ANSI (borra pantalla e historial).
 * - En la consola de un IDE (que no interpreta ANSI) imprime líneas en blanco como respaldo.
 */
public class Pantalla {

    private Pantalla() { }

    public static void limpiar() {
        if (System.console() != null) {
            System.out.print("\033[H\033[2J\033[3J");
            System.out.flush();
        } else {
            for (int i = 0; i < 60; i++) {
                System.out.println();
            }
        }
    }

    /** Espera a que el usuario presione Enter (para que alcance a leer antes de limpiar). */
    public static void pausar(java.util.Scanner scanner) {
        System.out.print("\nPresiona Enter para continuar...");
        scanner.nextLine();
    }
}
