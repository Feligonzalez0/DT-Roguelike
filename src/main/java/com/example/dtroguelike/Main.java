package com.example.dtroguelike;

import com.example.dtroguelike.config.AppContext;
import com.example.dtroguelike.devconsole.DevConsoleCommands;
import com.example.dtroguelike.devconsole.DeveloperConsole;
import com.example.dtroguelike.web.routes.WebRoutes;

import static spark.Spark.exception;
import static spark.Spark.port;
import static spark.Spark.staticFiles;

/**
 * Punto de entrada de la aplicacion. Configura el servidor Spark,
 * arma el contenedor de dependencias y registra las rutas web.
 */
public class Main {

    /**
     * Variable de entorno para desactivar la Developer Console (por
     * ejemplo, en produccion). Es una herramienta EXCLUSIVA de
     * desarrollo: para desactivarla alcanza con exportar
     * DEV_CONSOLE_ENABLED=false antes de arrancar el servidor, o con
     * borrar el bloque que la arranca mas abajo.
     */
    private static final String DEV_CONSOLE_ENV_VAR = "DEV_CONSOLE_ENABLED";

    public static void main(String[] args) {
        port(4567);
        staticFiles.location("/static");

        AppContext appContext = new AppContext();
        WebRoutes routes = new WebRoutes(appContext);
        routes.register();

        exception(Exception.class, (exception, request, response) -> {
            exception.printStackTrace();
            response.status(500);
            response.body("Ocurrio un error inesperado: " + exception.getMessage());
        });

        System.out.println("DT Roguelike corriendo en http://localhost:4567");

        if (isDevConsoleEnabled()) {
            DevConsoleCommands devConsoleCommands =
                    new DevConsoleCommands(appContext.getCareerRepository(), appContext.getCareerService());
            new DeveloperConsole(devConsoleCommands).start();
        }
    }

    private static boolean isDevConsoleEnabled() {
        String value = System.getenv(DEV_CONSOLE_ENV_VAR);
        return value == null || !value.equalsIgnoreCase("false");
    }
}
