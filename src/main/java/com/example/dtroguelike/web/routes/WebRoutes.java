package com.example.dtroguelike.web.routes;

import com.example.dtroguelike.config.AppContext;
import com.example.dtroguelike.web.controllers.CareerController;
import com.example.dtroguelike.web.controllers.ClubController;
import com.example.dtroguelike.web.controllers.FixtureController;
import com.example.dtroguelike.web.controllers.HomeController;
import com.example.dtroguelike.web.controllers.ManagerController;
import spark.ModelAndView;
import spark.template.mustache.MustacheTemplateEngine;

import java.util.HashMap;
import java.util.Map;

import static spark.Spark.get;
import static spark.Spark.post;

/**
 * Registra todas las rutas HTTP de la aplicacion y las conecta con los
 * controllers correspondientes. Ninguna logica de negocio importante
 * vive aca: solo manejo de HTTP (parametros, redirects, vistas).
 */
public class WebRoutes {

    private final HomeController homeController;
    private final ManagerController managerController;
    private final ClubController clubController;
    private final CareerController careerController;
    private final FixtureController fixtureController;
    private final MustacheTemplateEngine templateEngine = new MustacheTemplateEngine();

    public WebRoutes(AppContext appContext) {
        this.homeController = new HomeController();
        this.managerController = new ManagerController(appContext.getManagerService(), appContext.getCareerService());
        this.clubController = new ClubController(appContext.getCareerService());
        this.careerController = new CareerController(appContext.getCareerService(), appContext.getSeasonService());
        this.fixtureController = new FixtureController(appContext.getCareerService());
    }

    public void register() {
        get("/", (req, res) -> render(homeController.index(), "index.mustache"));

        get("/career/create", (req, res) -> render(managerController.createForm(), "manager-create.mustache"));

        post("/career/create", (req, res) -> {
            managerController.handleCreate(
                    req.queryParams("name"),
                    req.queryParams("age"),
                    req.queryParams("nationality"),
                    req.queryParams("style")
            );
            res.redirect("/career/clubs");
            return null;
        });

        get("/career/clubs", (req, res) -> render(clubController.showOffers(), "club-selection.mustache"));

        post("/career/club/select", (req, res) -> {
            clubController.selectClub(req.queryParams("clubId"));
            res.redirect("/career/dashboard");
            return null;
        });

        get("/career/dashboard", (req, res) -> render(careerController.showDashboard(), "dashboard.mustache"));
        
        get("/career/fixture", (req, res) -> render(fixtureController.showFixture(), "fixture.mustache"));

        post("/career/season/advance", (req, res) -> {
            careerController.advancePhase();
            res.redirect("/career/dashboard");
            return null;
        });

        post("/career/season/finish", (req, res) -> {
            careerController.finishSeason();
            res.redirect("/career/dashboard");
            return null;
        });

        post("/career/season/next", (req, res) -> {
            careerController.startNextSeason();
            res.redirect("/career/dashboard");
            return null;
        });
    }

    private String render(Map<String, Object> model, String template) {
        Map<String, Object> safeModel = model != null ? model : new HashMap<>();
        return templateEngine.render(new ModelAndView(safeModel, template));
    }
}
