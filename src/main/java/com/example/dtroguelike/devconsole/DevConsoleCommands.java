package com.example.dtroguelike.devconsole;

import com.example.dtroguelike.application.CareerService;
import com.example.dtroguelike.domain.career.Career;
import com.example.dtroguelike.domain.career.CareerEndReason;
import com.example.dtroguelike.domain.career.CareerState;
import com.example.dtroguelike.domain.club.Club;
import com.example.dtroguelike.domain.club.ClubState;
import com.example.dtroguelike.domain.manager.Manager;
import com.example.dtroguelike.domain.offer.ClubOffer;
import com.example.dtroguelike.infrastructure.repository.CareerRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Parsea y ejecuta los comandos de la Developer Console.
 *
 * <p>Es solo texto de entrada -> texto de salida: no toca STDIN/STDOUT,
 * lo que permite testear la logica de comandos sin simular una terminal
 * real (ver {@link DeveloperConsole} para el loop interactivo).
 *
 * <p>Los comandos operan siempre sobre la MISMA carrera activa que usa
 * la app web, obtenida via {@link CareerRepository#findCurrent()}.
 * Nunca crean una Career ni un estado propio, y los setters son
 * asignaciones directas: no disparan logica de juego (engines,
 * generacion de ofertas, fin de temporada, etc).
 */
public class DevConsoleCommands {

    /** Devuelto por {@code execute("clear")}; el llamador debe interpretarlo como "limpiar pantalla". */
    static final String CLEAR_SCREEN = "\u001B[H\u001B[2J";

    private static final String NO_CAREER_ERROR =
            "ERROR: no active career. Start a career from the web app first.";
    private static final String NO_CLUB_ERROR =
            "ERROR: no club assigned to the current career yet.";

    private final CareerRepository careerRepository;
    private final CareerService careerService;

    public DevConsoleCommands(CareerRepository careerRepository, CareerService careerService) {
        this.careerRepository = careerRepository;
        this.careerService = careerService;
    }

    /** Ejecuta una linea de comando y devuelve el texto a mostrar (puede ser vacio). */
    public String execute(String rawLine) {
        if (rawLine == null) {
            return "";
        }
        String line = rawLine.trim();
        if (line.isEmpty()) {
            return "";
        }

        String[] tokens = line.split("\\s+");
        String command = tokens[0].toLowerCase();

        switch (command) {
            case "help":
                return help();
            case "clear":
                return CLEAR_SCREEN;
            case "get":
                return handleGet(tokens);
            case "set":
                return handleSet(tokens);
            default:
                return "ERROR: unknown command. Type 'help' for available commands.";
        }
    }

    // ------------------------------------------------------------------
    // GET
    // ------------------------------------------------------------------

    private String handleGet(String[] tokens) {
        if (tokens.length < 2) {
            return "ERROR: missing property.\nUsage: get <property>";
        }
        String path = tokens[1];

        if (path.equalsIgnoreCase("all")) {
            return getAll();
        }
        if (path.equalsIgnoreCase("offers") || path.equalsIgnoreCase("career.offers")) {
            return formatOffers();
        }
        if (path.equalsIgnoreCase("offers.count")) {
            return "offers.count = " + careerService.getCurrentOffers().size();
        }

        Optional<Career> careerOpt = careerRepository.findCurrent();
        if (careerOpt.isEmpty()) {
            return NO_CAREER_ERROR;
        }
        Career career = careerOpt.get();
        Club club = career.getCurrentClub();
        ClubState clubState = career.getCurrentClubState();

        switch (path) {
            case "manager.name":
                return "manager.name = " + career.getManager().getName();
            case "manager.reputation":
                return "manager.reputation = " + career.getManager().getReputation();
            case "manager.tactics":
                return "manager.tactics = " + career.getManager().getAttributes().getTactics();
            case "manager.currentClub":
                return "manager.currentClub = " + (club == null ? "none" : club.getName());
            case "manager.jobSecurity":
                return "ERROR: unknown property: manager.jobSecurity "
                        + "(jobSecurity is tracked per club employment; use 'get career.jobSecurity' instead)";

            case "career.season":
                return "career.season = " + (career.getCurrentSeason() == null
                        ? "none" : career.getCurrentSeason().getYear());
            case "career.status":
                return "career.status = " + career.getState();
            case "career.endReason":
                return "career.endReason = " + (career.getEndReason() == null ? "none" : career.getEndReason());
            case "career.currentClub":
                return "career.currentClub = " + (club == null ? "none" : club.getName());
            case "career.jobSecurity":
                return "career.jobSecurity = " + (clubState == null ? "none" : clubState.getJobSecurity());

            case "club.name":
                return club == null ? NO_CLUB_ERROR : "club.name = " + club.getName();
            case "club.attack":
                return club == null ? NO_CLUB_ERROR : "club.attack = " + club.getStrength().getAttack();
            case "club.defense":
                return club == null ? NO_CLUB_ERROR : "club.defense = " + club.getStrength().getDefense();
            case "club.reputation":
                return club == null ? NO_CLUB_ERROR : "club.reputation = " + club.getReputation();
            case "club.budget":
                return clubState == null ? NO_CLUB_ERROR : "club.budget = " + clubState.getBudget();

            default:
                return "ERROR: unknown property: " + path;
        }
    }

    private String getAll() {
        Optional<Career> careerOpt = careerRepository.findCurrent();
        if (careerOpt.isEmpty()) {
            return NO_CAREER_ERROR;
        }
        Career career = careerOpt.get();
        Manager manager = career.getManager();
        Club club = career.getCurrentClub();
        ClubState clubState = career.getCurrentClubState();

        StringBuilder sb = new StringBuilder();
        sb.append("=== DEBUG STATE ===\n\n");

        sb.append("MANAGER\n");
        sb.append("  name: ").append(manager.getName()).append('\n');
        sb.append("  reputation: ").append(manager.getReputation()).append('\n');
        sb.append("  tactics: ").append(manager.getAttributes().getTactics()).append("\n");
        sb.append("  leadership: ").append(manager.getAttributes().getLeadership()).append("\n");
        sb.append("  managment: ").append(manager.getAttributes().getManagement()).append("\n");
        sb.append("  negotiation: ").append(manager.getAttributes().getNegotiation()).append("\n");
        sb.append("  youthDevelopment: ").append(manager.getAttributes().getYouthDevelopment()).append("\n");
        sb.append("  motivation: ").append(manager.getAttributes().getMotivation()).append("\n\n");

        sb.append("CAREER\n");
        sb.append("  season: ").append(career.getCurrentSeason() == null
                ? "none" : career.getCurrentSeason().getYear()).append('\n');
        sb.append("  status: ").append(career.getState()).append('\n');
        sb.append("  endReason: ").append(career.getEndReason() == null ? "none" : career.getEndReason())
                .append('\n');
        sb.append("  currentClub: ").append(club == null ? "none" : club.getName()).append('\n');
        sb.append("  jobSecurity: ").append(clubState == null ? "none" : clubState.getJobSecurity())
                .append("\n\n");

        sb.append("CLUB\n");
        if (club == null) {
            sb.append("  (no club assigned yet)\n\n");
        } else {
            sb.append("  name: ").append(club.getName()).append('\n');
            sb.append("  attack: ").append(club.getStrength().getAttack()).append('\n');
            sb.append("  defense: ").append(club.getStrength().getDefense()).append('\n');
            sb.append("  reputation: ").append(club.getReputation()).append('\n');
            sb.append("  budget: ").append(clubState.getBudget()).append("\n\n");
        }

        sb.append("OFFERS\n");
        sb.append("  count: ").append(careerService.getCurrentOffers().size());

        return sb.toString();
    }

    private String formatOffers() {
        List<ClubOffer> offers = careerService.getCurrentOffers();
        if (offers.isEmpty()) {
            return "Offers: (none)";
        }
        StringBuilder sb = new StringBuilder("Offers:");
        for (ClubOffer offer : offers) {
            sb.append("\n- ").append(offer.getClub().getName())
                    .append(" (salary: ").append(offer.getSalary())
                    .append(", contract: ").append(offer.getContractLengthYears()).append("y")
                    .append(", jobSecurity: ").append(offer.getJobSecurity())
                    .append(")");
        }
        return sb.toString();
    }

    // ------------------------------------------------------------------
    // SET
    // ------------------------------------------------------------------

    private String handleSet(String[] tokens) {
        if (tokens.length < 2) {
            return "ERROR: missing property.\nUsage: set <property> <value>";
        }
        String path = tokens[1];

        if (tokens.length < 3) {
            return "ERROR: missing value.\nUsage: set " + path + " <value>";
        }
        String value = String.join(" ", Arrays.copyOfRange(tokens, 2, tokens.length));

        Optional<Career> careerOpt = careerRepository.findCurrent();
        if (careerOpt.isEmpty()) {
            return NO_CAREER_ERROR;
        }
        Career career = careerOpt.get();

        switch (path) {
            case "manager.reputation": {
                Integer parsed = parseInt(value);
                if (parsed == null) {
                    return invalidInt(value);
                }
                career.getManager().setReputation(parsed);
                return "Manager reputation set to " + career.getManager().getReputation();
            }
            case "manager.tactics": {
                Integer parsed = parseInt(value);
                if (parsed == null) {
                    return invalidInt(value);
                }
                career.getManager().getAttributes().setTactics(parsed);
                return "Manager tactics set to " + career.getManager().getAttributes().getTactics();
            }
            case "career.status": {
                CareerState state;
                try {
                    state = CareerState.valueOf(value.toUpperCase());
                } catch (IllegalArgumentException e) {
                    return "ERROR: invalid career.status value: " + value
                            + "\nValid values: " + Arrays.toString(CareerState.values());
                }
                career.setState(state);
                return "Career status set to " + state;
            }
            case "career.endReason": {
                CareerEndReason reason;
                try {
                    reason = CareerEndReason.valueOf(value.toUpperCase());
                } catch (IllegalArgumentException e) {
                    return "ERROR: invalid career.endReason value: " + value
                            + "\nValid values: " + Arrays.toString(CareerEndReason.values());
                }
                // No existe un setter independiente para endReason: el unico
                // metodo del dominio que lo asigna es Career.finish(reason),
                // que tambien deja status=FINISHED. Se usa tal cual (sin
                // agregar logica nueva) porque es exactamente el atajo que
                // se pidio para preparar el flujo de career-over.
                career.finish(reason);
                return "Career endReason set to " + reason + " (career finished: status=" + career.getState() + ")";
            }
            case "career.jobSecurity": {
                if (career.getCurrentClubState() == null) {
                    return NO_CLUB_ERROR;
                }
                Integer parsed = parseInt(value);
                if (parsed == null) {
                    return invalidInt(value);
                }
                career.getCurrentClubState().setJobSecurity(parsed);
                return "Career jobSecurity set to " + career.getCurrentClubState().getJobSecurity();
            }
            case "club.attack": {
                if (career.getCurrentClub() == null) {
                    return NO_CLUB_ERROR;
                }
                Integer parsed = parseInt(value);
                if (parsed == null) {
                    return invalidInt(value);
                }
                career.getCurrentClub().getStrength().setAttack(parsed);
                return "Club attack set to " + career.getCurrentClub().getStrength().getAttack();
            }
            case "club.defense": {
                if (career.getCurrentClub() == null) {
                    return NO_CLUB_ERROR;
                }
                Integer parsed = parseInt(value);
                if (parsed == null) {
                    return invalidInt(value);
                }
                career.getCurrentClub().getStrength().setDefense(parsed);
                return "Club defense set to " + career.getCurrentClub().getStrength().getDefense();
            }
            case "club.reputation": {
                if (career.getCurrentClub() == null) {
                    return NO_CLUB_ERROR;
                }
                Integer parsed = parseInt(value);
                if (parsed == null) {
                    return invalidInt(value);
                }
                career.getCurrentClub().setReputation(parsed);
                return "Club reputation set to " + career.getCurrentClub().getReputation();
            }
            case "club.budget": {
                if (career.getCurrentClubState() == null) {
                    return NO_CLUB_ERROR;
                }
                Long parsed = parseLong(value);
                if (parsed == null) {
                    return invalidInt(value);
                }
                career.getCurrentClubState().setBudget(parsed);
                return "Club budget set to " + career.getCurrentClubState().getBudget();
            }

            default:
                return "ERROR: unknown property: " + path;
        }
    }

    private static Integer parseInt(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private static Long parseLong(String value) {
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private static String invalidInt(String value) {
        return "ERROR: invalid integer value: " + value;
    }

    // ------------------------------------------------------------------
    // HELP
    // ------------------------------------------------------------------

    private String help() {
        return String.join("\n",
                "Available commands:",
                "  get <property>            Show the current value of a property.",
                "  set <property> <value>    Set a property directly (debug only, no game logic runs).",
                "  get all                   Show a compact summary of the active career.",
                "  get offers                List the current club offers.",
                "  get offers.count          Number of current club offers.",
                "  help                      Show this help message.",
                "  clear                     Clear the terminal.",
                "",
                "Properties:",
                "  manager.name              get only",
                "  manager.reputation        get/set int, clamped 0-100",
                "  manager.tactics           get/set int, clamped 0-100",
                "  manager.currentClub       get only",
                "  career.season             get only (fixed when the season starts)",
                "  career.status             get/set one of " + Arrays.toString(CareerState.values()),
                "  career.endReason          get/set one of " + Arrays.toString(CareerEndReason.values())
                        + " (finishes the career)",
                "  career.currentClub        get only",
                "  career.jobSecurity        get/set int, clamped 0-100 (requires an assigned club)",
                "  career.offers             get only (same as 'get offers')",
                "  club.name                 get only (requires an assigned club)",
                "  club.attack               get/set int, clamped 0-100 (requires an assigned club)",
                "  club.defense              get/set int, clamped 0-100 (requires an assigned club)",
                "  club.reputation           get/set int (requires an assigned club)",
                "  club.budget               get/set long, min 0 (requires an assigned club)"
        );
    }
}
