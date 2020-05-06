package Main;

import Betting.Bet;
import Database.DatabaseEntryFiller;
import Database.DatabaseHandler;
import Database.DatabaseHandler.RepException;
import RiotAPI.ChampConsts;
import RiotAPI.RiotAPI;
import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import freemarker.template.Configuration;
import org.jsoup.Jsoup;
import spark.*;
import spark.template.freemarker.FreeMarkerEngine;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.SQLException;
import java.util.*;

import static RiotAPI.RiotAPI.getSplashByName;

public final class Main {

    public static DatabaseHandler db = new DatabaseHandler();

    private static final String currentPatch = "10.9";

    private static final Gson GSON = new Gson();

    public static void main(String[] args) throws IOException, SQLException {
        new Main(args).run();
    }

    private final String[] args;

    private Main(String[] args) {
        this.args = args;
    }

    private void run() throws IOException, SQLException {
        db.read("data/5Head.db");
        DatabaseEntryFiller DBEF = new DatabaseEntryFiller();
        RiotAPI.updateMapOfChamps();
        runSparkServer(4567);
    }

    private static FreeMarkerEngine createEngine() {
        Configuration config = new Configuration();
        File templates = new File("src/resources/templates");
        try {
            config.setDirectoryForTemplateLoading(templates);
        } catch (IOException ioe) {
            System.out.printf("ERROR: Unable use %s for template loading.%n", templates);
            System.exit(1);
        }
        return new FreeMarkerEngine(config);
    }

    /**
     * Runs the Spark server.
     * @param port The port on which to run the Spark server.
     */
    private void runSparkServer(int port) {
        Spark.port(port);
        Spark.externalStaticFileLocation("src/resources/static");
        Spark.exception(Exception.class, new ExceptionPrinter());

        FreeMarkerEngine freeMarker = createEngine();

        // Setup Spark Routes
        Spark.get("/home", new FrontHandler(), freeMarker);
        Spark.get("/", new FrontHandler(), freeMarker);
        Spark.post("/", new LogoutHandler(), freeMarker);
        Spark.get("/profile", new FrontHandler(), freeMarker);
        Spark.get("/currpatch", new PatchNoteHandler(), freeMarker);
        Spark.get("/mybets", new MyBetHandler(), freeMarker);
        Spark.post("/mybets", new LoginPageHandler(), freeMarker);
        Spark.get("/leaderboard", new LeaderboardHandler(), freeMarker);
        Spark.get("/champion/:champname", new ChampionPageHandler(), freeMarker);
        Spark.post("/champion/:champname", new ChampionBetHandler(), freeMarker);
    }

    /**
     * Handler for front page.
     */
    private static class FrontHandler implements TemplateViewRoute {
        @Override
        public ModelAndView handle(Request req, Response res) {
            Map<String, Object> variables = ImmutableMap.of("incorrectPassword", "");

            return new ModelAndView(variables, "splash.ftl");
        }
    }

    /**
     * Handler for front page after logging out.
     */
    private static class LogoutHandler implements TemplateViewRoute {
        @Override
        public ModelAndView handle(Request req, Response res) {
            Map<String, Object> variables = ImmutableMap.of("incorrectPassword", "You have been logged out");
            SessionHandler.logoutUser(res);
            return new ModelAndView(variables, "splash.ftl");
        }
    }

    /**
     * Handler for leaderboards page.
     */
    private static class LeaderboardHandler implements TemplateViewRoute {
        @Override
        public ModelAndView handle(Request req, Response res) {
            //checks to see if user is logged in and redirects to main page if not
            if (!SessionHandler.isUserLoggedIn(req)) {
                Map<String, Object> variables = ImmutableMap.of("incorrectPassword", "Please log in");
                return new ModelAndView(variables, "splash.ftl");
            } else {
                //makes the leaderboard from the top 50 users by reputation
                User currentUser = SessionHandler.getUserFromRequestCookie(req, db);
                List<String> top50 = new ArrayList<>();
                String leaderboards = "<div class=\"no-users\">No users.<div>";
                try {
                    for (User u : db.getTopFifty()) {
                        top50.add(u.getUsername() + "     " + u.getReputation());
                    }
                    leaderboards = LeaderboardBuilder.makeLeaderboard(top50);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                Map<String, Object> variables = null;
                //maps all the variables in the HTML to their elements
                variables = ImmutableMap.<String, Object>builder()
                        .put("userReputation", currentUser.getReputation())
                        .put("bettingStatus", "")
                        .put("profileImage", "<img class=\"icons\" src=\"" + RiotAPI.getIconByName(
                                ChampConsts.getChampNames().get(
                                        (Integer.parseInt(currentUser.getID())%ChampConsts.getChampNames().size() +
                                                ChampConsts.getChampNames().size())%ChampConsts.getChampNames().size()))
                                + "\">")
                        .put("profileName", currentUser.getUsername())
                        .put("leaderboard", leaderboards)
                        .build();
                return new ModelAndView(variables, "leaderboards.ftl");
            }
        }
    }

    /**
     * Handler for the mybets/profiles page.
     */
    private static class MyBetHandler implements TemplateViewRoute {
        @Override
        public ModelAndView handle(Request req, Response res) {
            //checks to see if user is logged in and redirects to main page if not
            if (!SessionHandler.isUserLoggedIn(req)) {
                Map<String, Object> variables = ImmutableMap.of("incorrectPassword",
                        "Entered a blank username or password");
                return new ModelAndView(variables, "splash.ftl");
            } else {
                User currentUser = SessionHandler.getUserFromRequestCookie(req, db);
                String champOptions;
                StringBuilder sb = new StringBuilder();
                StringBuilder sb1 = new StringBuilder();
                //builds and styles the list of all the user's bets
                try {
                    for (Bet b : db.getUserBetsOnPatch(currentPatch, currentUser.getID())) {
                        sb1.append("<div id=\"userbet\" style=\"background-image: url("
                                + getSplashByName(b.getCategory())
                                + ") \"><div class=\"champion\"><div class=\"line\">Champion</div>" + b.getCategory()
                                + "</div> <div class=\"type\"> <div class=\"line\">Type</div>" + b.getBetType() + "rate"
                                + "</div> <div class=\"percent\"> <div class=\"line\">Percent Predicted </div>"
                                + b.getPercentChangePredicted() + "%"
                                + "</div> <div class=\"wager\"> <div class=\"line\">Reputation Wagered </div>"
                                + b.getRepWagered() + " </div> <div class=\"betpatch\"> <div class=\"line\">Patch</div>"
                                + currentPatch + "</div></div>");
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                Map<String, Object> variables = null;
                //maps all the variables in the HTML to their elements
                variables = ImmutableMap.<String, Object>builder()
                        .put("userReputation", currentUser.getReputation())
                        .put("bettingStatus", "")
                        .put("profileImage", "<img class=\"icons\" src=\"" + RiotAPI.getIconByName(
                                ChampConsts.getChampNames().get(
                                        (Integer.parseInt(currentUser.getID())%ChampConsts.getChampNames().size() +
                                                ChampConsts.getChampNames().size())%ChampConsts.getChampNames().size()))
                                + "\">")
                        .put("profileName", currentUser.getUsername())
                        .put("success", "true")
                        .put("myBets", sb1.toString())
                        .build();
                return new ModelAndView(variables, "mybets.ftl");
            }
        }
    }

    /**
     * Handler for the main/login page.
     */
    private static class LoginPageHandler implements TemplateViewRoute {

        @Override
        public ModelAndView handle(Request req, Response res) {
            User currentUser = null;
            String champOptions;
            StringBuilder sb = new StringBuilder();
            StringBuilder sb1 = new StringBuilder();
            QueryParamsMap qm = req.queryMap();
            Boolean successfulLogin;
            //checks to see if user is logged in and redirects to main page if not
            if (qm.value("username").equals("") || qm.value("password").equals("")) {
                successfulLogin = false;
                Map<String, Object> variables = ImmutableMap.of("incorrectPassword",
                        "Entered a blank username or password");

                return new ModelAndView(variables, "splash.ftl");
            } else {
                successfulLogin = SessionHandler.loginUser(req, res, db);
            }
            if (successfulLogin == false) {
                Map<String, Object> variables = ImmutableMap.of("incorrectPassword",
                        "Incorrect password or " + "that username has already been taken");

                return new ModelAndView(variables, "splash.ftl");
            }
            try {
                String username = req.queryMap().value("username");
                String password = req.queryMap().value("password");
                currentUser = db.getUser(username, password);
                assert currentUser != null;
                String id = String.valueOf(qm.value("username").hashCode());
                //builds and styles the list of all the user's bets
                for (Bet b : db.getUserBetsOnPatch(currentPatch, id)) {
                    sb1.append("<div id=\"userbet\" style=\"background-image: url(" + getSplashByName(b.getCategory())
                            + ") \"><div class=\"champion\"><div class=\"line\">Champion</div>" + b.getCategory()
                            + "</div> <div class=\"type\"> <div class=\"line\">Type</div>" + b.getBetType() + "rate"
                            + "</div> <div class=\"percent\"> <div class=\"line\">Percent Predicted </div>"
                            + b.getPercentChangePredicted() + "%"
                            + "</div> <div class=\"wager\"> <div class=\"line\">Reputation Wagered </div>"
                            + b.getRepWagered() + " </div> <div class=\"betpatch\"> <div class=\"line\">Patch</div>"
                            + currentPatch + "</div></div>");
                }
            } catch (SQLException e) {
                // TODO Auto-generated catch block
                System.out.println("sql error in login");
                e.printStackTrace();
            }

            Map<String, Object> variables = null;
            //maps all the variables in the HTML to their elements
            variables = ImmutableMap.<String, Object>builder()
                    .put("userReputation", currentUser.getReputation())
                    .put("bettingStatus", "")
                    .put("profileImage", "<img class=\"icons\" src=\"" + RiotAPI.getIconByName(
                            ChampConsts.getChampNames().get(
                                    (Integer.parseInt(currentUser.getID())%ChampConsts.getChampNames().size() +
                                            ChampConsts.getChampNames().size())%ChampConsts.getChampNames().size()))
                    + "\">")
                    .put("profileName", currentUser.getUsername())
                    .put("success", "")
                    .put("myBets", sb1)
                    .build();
            return new ModelAndView(variables, "mybets.ftl");
        }
    }

    /**
     * Handler for patch notes page.
     */
    private static class PatchNoteHandler implements TemplateViewRoute {
        @Override
        public ModelAndView handle(Request req, Response res) throws IOException {
            //checks to see if user is logged in and redirects to main page if not
            if (!SessionHandler.isUserLoggedIn(req)) {
                Map<String, Object> variables = ImmutableMap.of("incorrectPassword", "Please log in");
                return new ModelAndView(variables, "splash.ftl");
            } else {
                User currentUser = SessionHandler.getUserFromRequestCookie(req, db);
                //creates all the champion's icons
                String championDivs = "";
                for (String champname : ChampConsts.getChampNames()) {
                    championDivs += "<a href=\"/champion/" + champname + "\">";
                    championDivs += "<div class=\"iconsdiv\">";
                    championDivs += "<img class=\"icons\" src=\"" + RiotAPI.getIconByName(champname) + "\">";
                    championDivs += "</div>";
                    championDivs += "</a>";
                }
                // getElementById("patch-notes-container") gets the entire patch notes, which is
                // not useful. We
                // do getElementsByClass("content-box") instead
                org.jsoup.select.Elements patchNotes = Jsoup.connect(
                        // TODO: Link the current patch notes
                        "https://na.leagueoflegends.com/en-us/news/game-updates/patch-10-9-notes/").get()
                        .getElementsByClass("patch-change-block");
                String patchNotesString = (patchNotes).outerHtml();
                Map<String, Object> variables = null;
                //maps all the variables in the HTML to their elements
                ImmutableMap.Builder<String, Object> builder = ImmutableMap.builder();
                builder.put("userReputation", currentUser.getReputation());
                builder.put("currentPatchLink",
                        "https://na.leagueoflegends.com/en-us/news/game-updates/patch-10-9-notes/");
                builder.put("currentPatch", patchNotesString);
                builder.put("bettingStatus", "");
                builder.put("profileImage", "<img class=\"icons\" src=\"" + RiotAPI.getIconByName(
                        ChampConsts.getChampNames().get(
                                (Integer.parseInt(currentUser.getID())%ChampConsts.getChampNames().size() +
                                        ChampConsts.getChampNames().size())%ChampConsts.getChampNames().size()))
                        + "\">");
                builder.put("profileName", currentUser.getUsername());
                builder.put("championDivs", championDivs);//.put("userReputation", db.getUser(userID).getReputation())
                variables = builder
                        .build();
                return new ModelAndView(variables, "patchnotes.ftl");
            }
        }
    }

    /**
     * Handler for a champion's page.
     */
    private static class ChampionPageHandler implements TemplateViewRoute {
        @Override
        public ModelAndView handle(Request req, Response res) {
            //checks to see if user is logged in and redirects to main page if not

            if (!SessionHandler.isUserLoggedIn(req)) {
                Map<String, Object> variables = ImmutableMap.of("incorrectPassword", "Please log in");

                return new ModelAndView(variables, "splash.ftl");
            } else {
                User currentUser = SessionHandler.getUserFromRequestCookie(req, db);

                String champName = req.params(":champname");
                //builds the charts for each statistic
                String wrchart = buildMetricChartForChampion(champName, "Win");
                String brchart = buildMetricChartForChampion(champName, "Ban");
                String prchart = buildMetricChartForChampion(champName, "Pick");

                Map<String, Object> variables = null;
                //maps all the variables in the HTML to their elements

                variables = ImmutableMap.<String, Object>builder().put("userReputation", currentUser.getReputation())
                        .put("bettingStatus", "")
                        .put("profileImage", "<img class=\"icons\" src=\"" + RiotAPI.getIconByName(
                                ChampConsts.getChampNames().get(
                                        (Integer.parseInt(currentUser.getID())%ChampConsts.getChampNames().size() +
                                                ChampConsts.getChampNames().size())%ChampConsts.getChampNames().size()))
                                + "\">")
                        .put("profileName", currentUser.getUsername())
                        .put("champSplashimage", getSplashByName(champName)).put("winrateGraph", wrchart)
                        .put("pickrateGraph", prchart)
                        .put("banrateGraph", brchart)
                        .put("champname", champName)
                        .put("error", "").build();

                return new ModelAndView(variables, "champion.ftl");
            }
        }
    }

    /**
     * Handler for making a bet on a champion's page.
     */
    private static class ChampionBetHandler implements TemplateViewRoute {
        @Override
        public ModelAndView handle(Request req, Response res) {
            //checks to see if user is logged in and redirects to main page if not
            if (!SessionHandler.isUserLoggedIn(req)) {
                Map<String, Object> variables = ImmutableMap.of("incorrectPassword", "Please log in");
                return new ModelAndView(variables, "splash.ftl");
            } else {
                String champName = req.params(":champname");

                QueryParamsMap qm = req.queryMap();

                String wper = req.queryMap().value("wpercentage");
                String pper = req.queryMap().value("ppercentage");
                String bper = req.queryMap().value("bpercentage");

                String wstake = req.queryMap().value("wstaked");
                String pstake = req.queryMap().value("pstaked");
                String bstake = req.queryMap().value("bstaked");
                User currentUser = SessionHandler.getUserFromRequestCookie(req, db);
                String error = "";

                if (currentUser != null) {
                    // if the winrate form is filled out, add a winrate bet
                    if (wper != null && Integer.parseInt(wstake) > 0) {

                        try {
                            db.createNewBet(
                                    String.valueOf((currentUser.getID() + champName + "Win" + wper + wstake).hashCode()),
                                                    currentUser.getID(), champName, "Win", wper, wstake, currentPatch);
                        } catch (SQLException e) {
                            System.out.println("Error adding bet to user with username " + currentUser.getUsername());
                        } catch (RepException e) {
                            error = "Not enough reputation to add that bet!";
                        }
                    }

                    // if the pickrate form is filled out, add a pickrate bet
                    if (pper != null && Integer.parseInt(pstake) > 0) {

                        try {
                            db.createNewBet(
                                    String.valueOf((currentUser.getID() + champName + "Pick" + pper + pstake).hashCode()),
                                                    currentUser.getID(), champName, "Pick", pper, pstake, currentPatch);
                        } catch (SQLException e) {
                            System.out.println("Error adding bet to user with username " + currentUser.getUsername());
                        } catch (RepException e) {
                            error = "Not enough reputation to add that bet!";
                        }
                    }

                    // if the banerate form is filled out, add a banrate bet
                    if (bper != null && Integer.parseInt(bstake) > 0) {

                        try {
                            db.createNewBet(
                                    String.valueOf(
                                            (currentUser.getID() + champName + "Ban" + bper + bstake).hashCode()),
                                    currentUser.getID(), champName, "Ban", bper, bstake, currentPatch);
                        } catch (SQLException e) {
                            System.out.println("Error adding bet to user with username " + currentUser.getUsername());
                        } catch (RepException e) {
                            error = "Not enough reputation to add that bet!";
                        }
                    }
                }

                currentUser = SessionHandler.getUserFromRequestCookie(req, db);
                //builds the charts for each statistic
                String wrchart = buildMetricChartForChampion(champName, "Win");
                String brchart = buildMetricChartForChampion(champName, "Ban");
                String prchart = buildMetricChartForChampion(champName, "Pick");

                //maps all the variables in the HTML to their elements
                Map<String, Object> variables = null;
                variables = ImmutableMap.<String, Object>builder()
                        .put("userReputation", currentUser.getReputation())
                        .put("bettingStatus", "")
                        .put("profileImage", "<img class=\"icons\" src=\"" + RiotAPI.getIconByName(
                                ChampConsts.getChampNames().get(
                                        (Integer.parseInt(currentUser.getID())%ChampConsts.getChampNames().size() +
                                                ChampConsts.getChampNames().size())%ChampConsts.getChampNames().size()))
                                + "\">")
                        .put("profileName", currentUser.getUsername())
                        .put("champSplashimage", getSplashByName(champName))
                        .put("winrateGraph", wrchart)
                        .put("pickrateGraph", brchart)
                        .put("banrateGraph", prchart)
                        .put("champname", champName)
                        .put("error", error).build();

                return new ModelAndView(variables, "champion.ftl");
            }
        }
    }

    /**
     * Builds a chart for the given champion and metric.
     * @param champname The champion for which to build the chart
     * @param metric Whether the chart is for win/pick/ban rate
     * @return A string containing the JS for the chart
     */
    private static String buildMetricChartForChampion(String champname, String metric)  {
        
        String jschart = "";
        String labels = "";
        String ratedata = "";

        //gets the list of patches and uses them as labels
        try {
            List<List<String>> patches = db.getPatches();
            if (patches.size() > 0) {
                for (List<String> patch : patches) {
                    labels += "'" + patch.get(0) + "',";
                }
            } else {
                return "";
            }

            switch(metric){
                case "Win":
                    for (List<String> patch : patches) {
                        Float rate = db.getChampionWinRateFromPatch(patch.get(0).substring(5), champname);
                                ratedata += String.valueOf(rate);
                    }
                    break;
                case "Pick":
                    for (List<String> patch : patches) {
                        Float rate = db.getChampionPickRateFromPatch(patch.get(0).substring(5), champname);
                                ratedata += String.valueOf(rate);
                    }
                    break;
                case "Ban":
                    for (List<String> patch : patches) {
                        Float rate = db.getChampionBanRateFromPatch(patch.get(0).substring(5), champname);
                                ratedata += String.valueOf(rate);
                    }
                    break;
            }

        } catch (SQLException e) {
           System.out.println("Problem connecting to SQL database while constructing chart");
        }

        /**
         * Adds the JS
         */
        jschart += "<script>";
        jschart += "var myChart = new Chart(wrgraph, {"
            + "type: 'line',"
            + "data: {"
            +    "labels:" + "[" + labels + "],"
            +    "datasets: [{"
            +        "label: 'Winrate',"
            +            "data: [" + ratedata + "],"
            // +            "backgroundColor: ["
            // +                "'rgba(255, 99, 132, 0.2)',"
            // +            "],"
            +            "borderColor: ["
            +                "'rgba(255, 99, 132, 1)',"
            +            "],"
            +            "borderWidth: 1"
            +        "}]"
            +    "},"
            +    "options: {"
            +        "scales: {"
            +            "yAxes: [{"
            +                "ticks: {"
            +                    "beginAtZero: false"
            +                "}"
            +            "}]"
            +        "}"
            +    "}"
            + "});";
        jschart += "</script>";
        return jschart.toString();

    }

    /**
     * Display an error page when an exception occurs in the server.
     */
    private static class ExceptionPrinter implements ExceptionHandler {
        @Override
        public void handle(Exception e, Request req, Response res) {
            res.status(500);
            StringWriter stacktrace = new StringWriter();
            try (PrintWriter pw = new PrintWriter(stacktrace)) {
                pw.println("<pre>");
                e.printStackTrace(pw);
                pw.println("</pre>");
            }
            res.body(stacktrace.toString());
        }
    }
}
