package com.example.dtroguelike.devconsole;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

/**
 * Consola interactiva de desarrollo: lee comandos de STDIN y escribe los
 * resultados en STDOUT, en la MISMA terminal donde corre el servidor.
 *
 * <p>Corre en un hilo daemon separado para no bloquear el servidor web
 * (ver {@link com.example.dtroguelike.Main#main}, que es tambien el
 * lugar donde se puede desactivar por completo para produccion).
 *
 * <p>Es exclusivamente una herramienta de desarrollo: no expone rutas
 * HTTP, no depende de Spark ni de ningun framework externo.
 */
public class DeveloperConsole {

    private final DevConsoleCommands commands;
    private final BufferedReader reader;
    private final PrintStream out;

    public DeveloperConsole(DevConsoleCommands commands) {
        this(commands, new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8)), System.out);
    }

    /** Constructor visible para tests: permite inyectar streams en memoria en vez de STDIN/STDOUT reales. */
    DeveloperConsole(DevConsoleCommands commands, BufferedReader reader, PrintStream out) {
        this.commands = commands;
        this.reader = reader;
        this.out = out;
    }

    /** Arranca el loop de lectura de comandos en un hilo daemon separado. */
    public void start() {
        Thread thread = new Thread(this::run, "developer-console");
        thread.setDaemon(true);
        thread.start();
    }

    private void run() {
        out.println("Developer console enabled.");
        printPrompt();

        String line;
        try {
            while ((line = reader.readLine()) != null) {
                String result = commands.execute(line);
                if (DevConsoleCommands.CLEAR_SCREEN.equals(result)) {
                    out.print(result);
                } else if (!result.isEmpty()) {
                    out.println(result);
                }
                printPrompt();
            }
        } catch (IOException e) {
            // STDIN cerrado (por ejemplo, el proceso corre sin una terminal
            // interactiva). La consola simplemente deja de escuchar; el
            // servidor web sigue funcionando con normalidad.
        }
    }

    private void printPrompt() {
        out.print("> ");
        out.flush();
    }
}
