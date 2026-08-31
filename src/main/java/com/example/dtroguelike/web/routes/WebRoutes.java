package com.example.dtroguelike.web.routes;

import com.example.dtroguelike.config.AppContext;
import com.example.dtroguelike.web.controllers.CareerController;
import com.example.dtroguelike.web.controllers.ClubController;
import com.example.dtroguelike.web.controllers.FixtureController;
import com.example.dtroguelike.web.controllers.HomeController;
import com.example.dtroguelike.web.controllers.ManagerController;
import com.example.dtroguelike.web.controllers.PhaseAdvanceResult;
import com.example.dtroguelike.web.controllers.StandingsController;
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
    private final StandingsController standingsController;
    private final MustacheTemplateEngine templateEngine = new MustacheTemplateEngine();
    
    public WebRoutes(AppContext appContext) {
        this.homeController = new HomeController();
        this.managerController = new ManagerController(appContext.getManagerService(), appContext.getCareerService());
        this.clubController = new ClubController(appContext.getCareerService());
        this.careerController = new CareerController(appContext.getCareerService(), appContext.getSeasonService());
        this.fixtureController = new FixtureController(appContext.getCareerService());
        this.standingsController = new StandingsController(appContext.getCareerService());
    }

    public void register() {
        /* DEBUG
        before((req, res) -> {
            System.out.println("REQUEST: " + req.requestMethod() + " " + req.pathInfo());
        });
        */
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

        post("/career/club/select", (req, res) -> {
            clubController.selectClub(req.queryParams("clubId"));
            res.redirect("/career/dashboard");
            return null;
        });

        get("/career/dashboard", (req, res) -> render(careerController.showDashboard(), "dashboard.mustache"));
        get("/career/fixture", (req, res) -> render(fixtureController.showFixture(), "fixture.mustache"));
        get("/career/standings", (req, res) -> render(standingsController.showStandings(), "standings.mustache"));
        get("/career/summary", (req, res) -> render(careerController.showSeasonSummary(), "season-summary.mustache"));
        get("/career/career-over", (req, res) -> render(careerController.showCareerOver(), "career-over.mustache"));

        get("/career/clubs", (req, res) -> {
            if (careerController.isCareerOver()) {
                res.redirect("/career/career-over");
                return null;
            }

            return render(
                    clubController.showOffers(),
                    "club-selection.mustache"
            );
        });

        post("/career/match/simulate", (req, res) -> {
            careerController.simulateNextMatch();
            res.redirect("/career/dashboard");
            return null;
        });

        post("/career/season/advance", (req, res) -> {
            PhaseAdvanceResult result = careerController.advancePhase();

            switch (result.destination()) {
                case SEASON_SUMMARY ->
                        res.redirect("/career/summary");
                case DASHBOARD ->
                        res.redirect("/career/dashboard");
            }

            return null;
        });

        post("/career/season/finish", (req, res) -> {
            careerController.finishSeason();
            res.redirect("/career/dashboard");
            return null;
        });

        post("/career/season/next", (req, res) -> {
            res.redirect("/career/employment");
            return null;
        });

        // =========================================================
        // SITUACIÓN LABORAL
        // =========================================================

        get("/career/employment", (req, res) ->
                render(
                        careerController.showEmploymentSituation(),
                        "employment.mustache"
                )
        );

        // CONTINUAR EN CLUB
        post("/career/employment/continue", (req, res) -> {

            careerController.continueAtCurrentClub();

            res.redirect("/career/dashboard");

            return null;
        });


        // RENUNCIAR
        post("/career/employment/resign", (req, res) -> {

            careerController.resign();

            res.redirect("/career/clubs");

            return null;
        });

        // ACEPTAR RENOVACIÓN
        post("/career/employment/renew", (req, res) -> {

            careerController.acceptRenewal();

            res.redirect("/career/dashboard");

            return null;
        });

        // RECHAZAR RENOVACIÓN
        post("/career/employment/reject-renewal", (req, res) -> {

            careerController.rejectRenewal();

            res.redirect("/career/clubs");

            return null;
        });
    }

    private String render(Map<String, Object> model, String template) {
        Map<String, Object> safeModel = model != null ? model : new HashMap<>();
        return templateEngine.render(new ModelAndView(safeModel, template));
    }
}
