package com.example.dtroguelike;

import com.example.dtroguelike.config.AppContext;
import com.example.dtroguelike.web.routes.WebRoutes;

import static spark.Spark.exception;
import static spark.Spark.port;
import static spark.Spark.staticFiles;

/**
 * Punto de entrada de la aplicacion. Configura el servidor Spark,
 * arma el contenedor de dependencias y registra las rutas web.
 */
public class Main {

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
    }
}
