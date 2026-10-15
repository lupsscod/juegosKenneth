package pokemon;

import stats.GestorEstadisticas;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import main.Jugador;

public class PokemonGame {

    private final String ARCHIVO_CUENTAS = "cuentas_entrenadores.txt";

    private List<Pokemon> gestionarCuentaYEquipo(Scanner scanner, String numeroEntrenador, Jugador jugador, List<Pokemon> pokedex) {
        System.out.print("\n" + numeroEntrenador + " - Ingresa tu nombre de entrenador: ");
        String nombre = scanner.nextLine().trim();
        jugador.setNickname(nombre);

        List<Pokemon> guardado = cargarEquipoDeArchivo(nombre, pokedex);

        if (guardado != null) {
            System.out.println("¡Bienvenido de vuelta, Entrenador " + nombre.toUpperCase() + "!");
            System.out.println("Hemos encontrado tu equipo guardado. ¿Qué deseas hacer?");
            System.out.println("1. Usar mi equipo guardado");
            System.out.println("2. Cambiar mis Pokémon y movimientos (Sobrescribirá el guardado anterior)");
            System.out.print("Elige: ");
            int opc = scanner.nextInt();
            scanner.nextLine();

            if (opc == 1) {
                System.out.println("¡Equipo cargado con éxito!");
                return guardado;
            }
        } else {
            System.out.println("Hola " + nombre.toUpperCase() + ", no tienes un equipo registrado. ¡Vamos a crearlo!");
        }

        List<Pokemon> nuevoEquipo = elegirEquipo(scanner, nombre, pokedex);
        guardarEquipoEnArchivo(nombre, nuevoEquipo);
        return nuevoEquipo;
    }

    private void guardarEquipoEnArchivo(String nombre, List<Pokemon> equipo) {
        Map<String, String> cuentas = leerTodasLasCuentas();
        StringBuilder data = new StringBuilder();
        for (Pokemon p : equipo) {
            data.append(p.getId()).append(",");
            for (Movimiento m : p.getMovimientos()) data.append(m.getNombre()).append(",");
            data.append(";");
        }
        cuentas.put(nombre.toLowerCase(), data.toString());

        try (PrintWriter out = new PrintWriter(new FileWriter(ARCHIVO_CUENTAS))) {
            for (Map.Entry<String, String> entry : cuentas.entrySet()) {
                out.println(entry.getKey() + "|" + entry.getValue());
            }
        } catch (IOException e) { System.out.println("Error al guardar la cuenta."); }
    }

    private List<Pokemon> cargarEquipoDeArchivo(String nombre, List<Pokemon> pokedex) {
        Map<String, String> cuentas = leerTodasLasCuentas();
        String data = cuentas.get(nombre.toLowerCase());
        if (data == null) return null;

        List<Pokemon> equipo = new ArrayList<>();
        String[] pokemonesData = data.split(";");

        for (String pData : pokemonesData) {
            if (pData.isEmpty()) continue;
            String[] partes = pData.split(",");
            int id = Integer.parseInt(partes[0]);

            Pokemon base = null;
            for (Pokemon p : pokedex) if (p.getId() == id) { base = p; break; }
            if(base == null) continue;

            Pokemon copia = new Pokemon(base.getId(), base.getNombre(), base.getTipos(), base.getHpMax(), base.getVelocidad());

            for (int i = 1; i <= 4; i++) {
                if (i < partes.length) {
                    String nombreMov = partes[i];
                    for (Movimiento mBase : base.getMovimientosApendice()) {
                        if (mBase.getNombre().equals(nombreMov)) {
                            copia.agregarMovimientoCombate(new Movimiento(mBase.getNombre(), mBase.getTipo(), mBase.getPoder(), mBase.getPrecision(), mBase.getPpMax(), mBase.getEfecto(), mBase.getFlavorText()));
                            break;
                        }
                    }
                }
            }
            equipo.add(copia);
        }
        return equipo;
    }

    private Map<String, String> leerTodasLasCuentas() {
        Map<String, String> cuentas = new HashMap<>();
        File archivo = new File(ARCHIVO_CUENTAS);
        if (!archivo.exists()) return cuentas;

        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] partes = linea.split("\\|");
                if (partes.length == 2) cuentas.put(partes[0], partes[1]);
            }
        } catch (Exception e) {}
        return cuentas;
    }

    public void iniciarPartida(Scanner scanner) {
        List<Pokemon> pokedex = cargarPokedexDesdeJSON();
        if (pokedex.isEmpty()) return;

        Jugador p1 = new Jugador("");
        Jugador p2 = new Jugador("");

        List<Pokemon> equipoBaseP1 = gestionarCuentaYEquipo(scanner, "ENTRENADOR 1", p1, pokedex);
        List<Pokemon> equipoBaseP2 = gestionarCuentaYEquipo(scanner, "ENTRENADOR 2", p2, pokedex);

        boolean jugar = true;
        while (jugar) {
            List<Pokemon> eqP1 = new ArrayList<>(); List<Pokemon> eqP2 = new ArrayList<>();
            for (Pokemon p : equipoBaseP1) eqP1.add(p.clonarParaRevancha());
            for (Pokemon p : equipoBaseP2) eqP2.add(p.clonarParaRevancha());

            ejecutarBatalla(scanner, p1, p2, eqP1, eqP2);

            System.out.println("\n¿Qué deseas hacer ahora?");
            System.out.println("1. Revancha (Mismos equipos y movimientos)");
            System.out.println("2. Cambiar de Entrenadores o Equipos");
            System.out.println("3. Volver al menú");
            int opc = scanner.nextInt();
            scanner.nextLine();

            if (opc == 2) {
                equipoBaseP1 = gestionarCuentaYEquipo(scanner, "ENTRENADOR 1", p1, pokedex);
                equipoBaseP2 = gestionarCuentaYEquipo(scanner, "ENTRENADOR 2", p2, pokedex);
            } else if (opc == 3) jugar = false;
        }
    }

    private void ejecutarBatalla(Scanner scanner, Jugador p1, Jugador p2, List<Pokemon> eqP1, List<Pokemon> eqP2) {
        long inicioTiempo = System.currentTimeMillis();

        System.out.println("\n¡Atención! La partida iniciará dentro de poco...");
        System.out.println("El campo de batalla está formándose y la tensión aumenta...");
        pausa(2500);

        VisorImagen visor = new VisorImagen();
        visor.setVisible(true);
        visor.mostrarTablaTipos();

        int actP1 = 0, actP2 = 0;
        visor.actualizarEstado(p1.getNickname(), eqP1, p2.getNickname(), eqP2, actP1, actP2);
        visor.animarCambioEntrada(true);
        visor.animarCambioEntrada(false);

        while (equipoVivo(eqP1) && equipoVivo(eqP2)) {
            Pokemon pk1 = eqP1.get(actP1), pk2 = eqP2.get(actP2);

            System.out.println("\n--- Turno de " + p1.getNickname().toUpperCase() + " ---");
            System.out.println("1. Atacar | 2. Cambiar");
            int acc1 = scanner.nextInt();
            int elecc1 = seleccionarAccion(scanner, pk1, eqP1, acc1);

            System.out.println("\n--- Turno de " + p2.getNickname().toUpperCase() + " ---");
            System.out.println("1. Atacar | 2. Cambiar");
            int acc2 = scanner.nextInt();
            int elecc2 = seleccionarAccion(scanner, pk2, eqP2, acc2);

            if (acc1 == 2 && elecc1 != -1) {
                visor.animarCambioSalida(true); pausa(500); actP1 = elecc1;
                visor.actualizarEstado(p1.getNickname(), eqP1, p2.getNickname(), eqP2, actP1, actP2);
                visor.animarCambioEntrada(true); pausa(500);
            }
            if (acc2 == 2 && elecc2 != -1) {
                visor.animarCambioSalida(false); pausa(500); actP2 = elecc2;
                visor.actualizarEstado(p1.getNickname(), eqP1, p2.getNickname(), eqP2, actP1, actP2);
                visor.animarCambioEntrada(false); pausa(500);
            }

            if (acc1 == 1 && acc2 == 1) {
                if (pk1.getVelocidad() >= pk2.getVelocidad()) {
                    ejecutarAtaqueAnimado(visor, pk1, pk2, elecc1, true);
                    if (pk2.estaVivo()) ejecutarAtaqueAnimado(visor, pk2, pk1, elecc2, false);
                    else visor.animarMuerte(false);
                } else {
                    ejecutarAtaqueAnimado(visor, pk2, pk1, elecc2, false);
                    if (pk1.estaVivo()) ejecutarAtaqueAnimado(visor, pk1, pk2, elecc1, true);
                    else visor.animarMuerte(true);
                }
            } else if (acc1 == 1) {
                ejecutarAtaqueAnimado(visor, pk1, pk2, elecc1, true);
                if (!pk2.estaVivo()) visor.animarMuerte(false);
            } else if (acc2 == 1) {
                ejecutarAtaqueAnimado(visor, pk2, pk1, elecc2, false);
                if (!pk1.estaVivo()) visor.animarMuerte(true);
            }

            visor.actualizarEstado(p1.getNickname(), eqP1, p2.getNickname(), eqP2, actP1, actP2);

            if (!eqP1.get(actP1).estaVivo() && equipoVivo(eqP1)) {
                actP1 = elegirSiguienteVivo(scanner, p1.getNickname(), eqP1);
                visor.actualizarEstado(p1.getNickname(), eqP1, p2.getNickname(), eqP2, actP1, actP2);
                visor.animarCambioEntrada(true);
            }
            if (!eqP2.get(actP2).estaVivo() && equipoVivo(eqP2)) {
                actP2 = elegirSiguienteVivo(scanner, p2.getNickname(), eqP2);
                visor.actualizarEstado(p1.getNickname(), eqP1, p2.getNickname(), eqP2, actP1, actP2);
                visor.animarCambioEntrada(false);
            }
        }

        long duracion = (System.currentTimeMillis() - inicioTiempo) / 1000;
        scanner.nextLine();
        Jugador ganador = equipoVivo(eqP1) ? p1 : p2;
        GestorEstadisticas.registrarStatPokemon(new EstadisticaPokemon(ganador.getNickname(), duracion, 3, 100));

        System.out.println("\n¡GANADOR: " + ganador.getNickname().toUpperCase() + "!");
        System.out.println("Mostrando resultados en pantalla...");
        pausa(5000); visor.cerrar();
    }

    private void ejecutarAtaqueAnimado(VisorImagen visor, Pokemon atq, Pokemon def, int idxMov, boolean esP1) {
        if (idxMov < 0 || idxMov >= atq.getMovimientos().size()) return;
        Movimiento mov = atq.getMovimientos().get(idxMov);

        System.out.println("¡" + atq.getNombre() + " usó " + mov.getNombre().toUpperCase() + "!");
        if (mov.usarPP() && Math.random() * 100 <= mov.getPrecision()) {
            visor.animarAtaqueElemental(esP1, mov.getTipo());
            pausa(800);

            double multiplicador = 1.0;
            for (String tipoDef : def.getTipos()) {
                multiplicador *= calcularEfectividad(mov.getTipo(), tipoDef);
            }

            if (multiplicador > 1.0) System.out.println("¡Es súper eficaz!");
            else if (multiplicador < 1.0 && multiplicador > 0) System.out.println("No es muy eficaz...");
            else if (multiplicador == 0) System.out.println("¡No afecta a " + def.getNombre() + "!");

            int danoBase = mov.getPoder() > 0 ? mov.getPoder() + 10 : 0;
            int danoFinal = (int) (danoBase * multiplicador);

            def.recibirDano(danoFinal);

        } else { System.out.println("¡El ataque falló!"); }
    }

    private double calcularEfectividad(String atq, String def) {
        atq = atq.toLowerCase(); def = def.toLowerCase();
        if(atq.equals("fire")) {
            if(def.equals("grass") || def.equals("bug") || def.equals("ice")) return 2.0;
            if(def.equals("water") || def.equals("fire") || def.equals("rock") || def.equals("dragon")) return 0.5;
        }
        else if(atq.equals("water")) {
            if(def.equals("fire") || def.equals("ground") || def.equals("rock")) return 2.0;
            if(def.equals("water") || def.equals("grass") || def.equals("dragon")) return 0.5;
        }
        else if(atq.equals("grass")) {
            if(def.equals("water") || def.equals("ground") || def.equals("rock")) return 2.0;
            if(def.equals("fire") || def.equals("grass") || def.equals("poison") || def.equals("flying") || def.equals("bug") || def.equals("dragon")) return 0.5;
        }
        else if(atq.equals("electric")) {
            if(def.equals("water") || def.equals("flying")) return 2.0;
            if(def.equals("electric") || def.equals("grass") || def.equals("dragon")) return 0.5;
            if(def.equals("ground")) return 0.0;
        }
        else if(atq.equals("ice")) {
            if(def.equals("grass") || def.equals("ground") || def.equals("flying") || def.equals("dragon")) return 2.0;
            if(def.equals("fire") || def.equals("water") || def.equals("ice")) return 0.5;
        }
        else if(atq.equals("fighting")) {
            if(def.equals("normal") || def.equals("ice") || def.equals("rock")) return 2.0;
            if(def.equals("poison") || def.equals("flying") || def.equals("psychic") || def.equals("bug")) return 0.5;
            if(def.equals("ghost")) return 0.0;
        }
        else if(atq.equals("poison")) {
            if(def.equals("grass") || def.equals("bug")) return 2.0;
            if(def.equals("poison") || def.equals("ground") || def.equals("rock") || def.equals("ghost")) return 0.5;
        }
        else if(atq.equals("ground")) {
            if(def.equals("fire") || def.equals("electric") || def.equals("poison") || def.equals("rock")) return 2.0;
            if(def.equals("grass") || def.equals("bug")) return 0.5;
            if(def.equals("flying")) return 0.0;
        }
        else if(atq.equals("flying")) {
            if(def.equals("grass") || def.equals("fighting") || def.equals("bug")) return 2.0;
            if(def.equals("electric") || def.equals("rock")) return 0.5;
        }
        else if(atq.equals("psychic")) {
            if(def.equals("fighting") || def.equals("poison")) return 2.0;
            if(def.equals("psychic")) return 0.5;
        }
        else if(atq.equals("bug")) {
            if(def.equals("grass") || def.equals("poison") || def.equals("psychic")) return 2.0;
            if(def.equals("fire") || def.equals("fighting") || def.equals("flying") || def.equals("ghost")) return 0.5;
        }
        else if(atq.equals("rock")) {
            if(def.equals("fire") || def.equals("ice") || def.equals("flying") || def.equals("bug")) return 2.0;
            if(def.equals("fighting") || def.equals("ground")) return 0.5;
        }
        else if(atq.equals("ghost")) {
            if(def.equals("ghost")) return 2.0;
            if(def.equals("normal")) return 0.0;
        }
        else if(atq.equals("dragon")) {
            if(def.equals("dragon")) return 2.0;
        }
        else if(atq.equals("normal")) {
            if(def.equals("rock")) return 0.5;
            if(def.equals("ghost")) return 0.0;
        }
        return 1.0;
    }

    private List<Pokemon> cargarPokedexDesdeJSON() {
        List<Pokemon> pokedex = new ArrayList<>();
        try {
            String content = new String(Files.readAllBytes(Paths.get("pokedex.json")));
            Pattern pokemonPattern = Pattern.compile("\\{\\s*\"pokemon_name\":\\s*\"([^\"]+)\",\\s*\"pokemon_type\":\\s*\\[(.*?)\\],\\s*\"moves\":\\s*\\[(.*?)\\]\\s*\\}", Pattern.DOTALL);
            Matcher pokemonMatcher = pokemonPattern.matcher(content);

            int idCounter = 1;
            while (pokemonMatcher.find()) {
                String name = pokemonMatcher.group(1);
                String typesRaw = pokemonMatcher.group(2);
                String movesRaw = pokemonMatcher.group(3);

                List<String> typesList = new ArrayList<>();
                Matcher typeMatcher = Pattern.compile("\"([^\"]+)\"").matcher(typesRaw);
                while (typeMatcher.find()) typesList.add(typeMatcher.group(1));

                int hpMax = 160 + (name.length() * 5);
                int velocidad = 70 + (idCounter % 30);
                Pokemon pokemon = new Pokemon(idCounter, name, typesList.toArray(new String[0]), hpMax, velocidad);

                Pattern movePattern = Pattern.compile("\\{\\s*\"name\":\\s*\"([^\"]+)\",\\s*\"type\":\\s*\"([^\"]+)\",\\s*\"power\":\\s*([^,\\s]+),\\s*\"accuracy\":\\s*([^,\\s]+),\\s*\"pp\":\\s*(\\d+),\\s*\"effect\":\\s*\"(.*?)\",\\s*\"flavor_text\":\\s*\"(.*?)\"\\s*\\}", Pattern.DOTALL);
                Matcher moveMatcher = movePattern.matcher(movesRaw);

                while (moveMatcher.find()) {
                    int power = moveMatcher.group(3).equals("null") ? 0 : Integer.parseInt(moveMatcher.group(3));
                    int accuracy = moveMatcher.group(4).equals("null") ? 100 : Integer.parseInt(moveMatcher.group(4));
                    pokemon.agregarMovimientoApendice(new Movimiento(moveMatcher.group(1), moveMatcher.group(2), power, accuracy, Integer.parseInt(moveMatcher.group(5)), moveMatcher.group(6), moveMatcher.group(7)));
                }
                pokedex.add(pokemon);
                idCounter++;
            }
        } catch (Exception e) {}
        return pokedex;
    }

    private List<Pokemon> elegirEquipo(Scanner scanner, String nick, List<Pokemon> pokedex) {
        List<Pokemon> equipo = new ArrayList<>();
        System.out.println("\n=== SELECCIÓN: " + nick.toUpperCase() + " ===");
        for (Pokemon p : pokedex) System.out.println("[" + p.getId() + "] " + obtenerEmojiTipos(p.getTipos()) + " " + p.getNombre() + " -> " + p.getSpriteUrl());

        for (int i = 0; i < 3; i++) {
            System.out.print("Elige el ID del Pokemon " + (i + 1) + ": ");
            int id = scanner.nextInt();
            Pokemon base = pokedex.get(0);
            for (Pokemon p : pokedex) if (p.getId() == id) { base = p; break; }

            Pokemon copia = new Pokemon(base.getId(), base.getNombre(), base.getTipos(), base.getHpMax(), base.getVelocidad());

            // FILTRADO INTELIGENTE DE MOVIMIENTOS
            List<Movimiento> filtrados = new ArrayList<>();
            for (Movimiento m : base.getMovimientosApendice()) {
                if (m.getTipo().equalsIgnoreCase("normal")) { filtrados.add(m); continue; }
                for (String t : base.getTipos()) {
                    if (m.getTipo().equalsIgnoreCase(t)) { filtrados.add(m); break; }
                }
            }
            if (filtrados.size() < 4) filtrados = base.getMovimientosApendice(); // Respaldo por si hay muy pocos

            System.out.println("\nSelecciona 4 movimientos lógicos para " + copia.getNombre() + ":");
            for (int m = 0; m < filtrados.size(); m++) {
                System.out.println((m + 1) + ". " + filtrados.get(m).getNombre() + " [Tipo: " + filtrados.get(m).getTipo() + " | Poder: " + filtrados.get(m).getPoder() + "]");
            }

            for (int m = 0; m < 4; m++) {
                System.out.print("Elige el movimiento " + (m + 1) + " (1-" + filtrados.size() + "): ");
                int idxMov = scanner.nextInt() - 1;
                if (idxMov < 0 || idxMov >= filtrados.size()) idxMov = 0;
                Movimiento mov = filtrados.get(idxMov);
                copia.agregarMovimientoCombate(new Movimiento(mov.getNombre(), mov.getTipo(), mov.getPoder(), mov.getPrecision(), mov.getPpMax(), mov.getEfecto(), mov.getFlavorText()));
            }
            equipo.add(copia);
        }
        return equipo;
    }

    private String obtenerEmojiTipos(String[] tipos) {
        StringBuilder sb = new StringBuilder();
        for (String tipo : tipos) {
            switch (tipo.toLowerCase()) {
                case "fire": sb.append("🔥"); break; case "water": sb.append("💧"); break;
                case "grass": sb.append("🌿"); break; case "electric": sb.append("⚡"); break;
                case "ice": sb.append("❄️"); break; case "fighting": sb.append("🥊"); break;
                case "poison": sb.append("☠️"); break; case "ground": sb.append("🪨"); break;
                case "flying": sb.append("🌪️"); break; case "psychic": sb.append("🔮"); break;
                case "bug": sb.append("🐛"); break; case "rock": sb.append("🪨"); break;
                case "ghost": sb.append("👻"); break; case "dragon": sb.append("🐉"); break;
                default: sb.append("⚪"); break;
            }
        }
        return sb.toString();
    }

    private int seleccionarAccion(Scanner sc, Pokemon p, List<Pokemon> eq, int acc) {
        if (acc == 1) {
            for (int i = 0; i < p.getMovimientos().size(); i++) System.out.println((i+1) + ". " + p.getMovimientos().get(i).getNombre() + " [Tipo: "+p.getMovimientos().get(i).getTipo()+"]");
            System.out.print("Elige ataque: "); return sc.nextInt() - 1;
        } else {
            for (int i = 0; i < eq.size(); i++) System.out.println((i+1) + ". " + eq.get(i).getNombre() + (eq.get(i).estaVivo() ? " (VIVO)" : " (MUERTO)"));
            System.out.print("Cambiar a: "); return sc.nextInt() - 1;
        }
    }

    private int elegirSiguienteVivo(Scanner sc, String nick, List<Pokemon> eq) {
        System.out.println("\n" + nick.toUpperCase() + ", elige tu siguiente Pokémon:");
        return seleccionarAccion(sc, null, eq, 2);
    }

    private boolean equipoVivo(List<Pokemon> eq) {
        for (Pokemon p : eq) if (p.estaVivo()) return true; return false;
    }

    private void pausa(int ms) { try { Thread.sleep(ms); } catch (Exception e){} }
}