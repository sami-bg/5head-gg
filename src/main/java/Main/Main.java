package Main;

import Betting.Bet;
import Betting.BettingSession;
import Betting.GainFunction;
import Database.DatabaseEntryFiller;
import Database.DatabaseHandler;
import Database.DatabaseHandler.RepException;
import RiotAPI.ChampConsts;
import RiotAPI.RiotAPI;
import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import freemarker.template.Configuration;
import org.jsoup.Jsoup;
import org.sqlite.SQLiteException;

import spark.*;
import spark.template.freemarker.FreeMarkerEngine;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

import static RiotAPI.RiotAPI.getSplashByName;

public final class Main {

    private static AtomicReference<String> currentPatch = new AtomicReference<>();

    private static final Double MIN_GAIN_MULT = 0.3;
    private static final Double MAX_GAIN_MULT = 3.0;
    private static final Double UPPER_BOUND = 2.0;
    private static final Double LOWER_BOUND = 0.2;
    private static final GainFunction gain = new SigmoidAdjustedGain(MIN_GAIN_MULT, MAX_GAIN_MULT, UPPER_BOUND,
            LOWER_BOUND);
    private static final Integer BROADCAST_INTERVAL_SECONDS = 60; // The interval at which we broadcast, in seconds

    private static BettingSession wr = new BettingSession("winrate", ChampConsts.getChampNames());
    private static BettingSession pr = new BettingSession("pickrate", ChampConsts.getChampNames());
    private static BettingSession br = new BettingSession("pickrate", ChampConsts.getChampNames());

    public static DatabaseHandler db = new DatabaseHandler();

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
     * 
     * @param port The port on which to run the Spark server.
     */
    private void runSparkServer(int port) {

        // Initializes the tables with all the champions
        // so that we can set values for them
        // DatabaseEntryFiller.addChampsToRatesTables(db);

        // Set up the thread that handles automatic broadcasting
        TimerTask patchTracker = new PatchTrackerThread(0, BROADCAST_INTERVAL_SECONDS, wr, pr, br, db, currentPatch);
        PatchTrackerThread patchTracker2 = new PatchTrackerThread(0, 10, wr, pr, br, db, currentPatch);

        currentPatch = patchTracker2.getAndUpdateCurrentPatch();
        Timer timer = new Timer();
        timer.schedule(patchTracker, 0, BROADCAST_INTERVAL_SECONDS * 1000);

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
            // checks to see if user is logged in and redirects to main page if not
            if (!SessionHandler.isUserLoggedIn(req)) {
                Map<String, Object> variables = ImmutableMap.of("incorrectPassword", "Please log in");
                return new ModelAndView(variables, "splash.ftl");
            } else {
                // makes the leaderboard from the top 50 users by reputation
                User currentUser = SessionHandler.getUserFromRequestCookie(req, db);
                List<String> top50 = new ArrayList<>();
                String leaderboards = "<div class=\"no-users\">No users.<div>";
                try {
                    List<User> topUsers = db.getTopFifty();
                    for (User u : topUsers) {
                        top50.add(u.getUsername() + "     " + u.getReputation());
                    }
                    leaderboards = LeaderboardBuilder.makeLeaderboard(top50);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                Map<String, Object> variables = null;
                // maps all the variables in the HTML to their elements
                variables = ImmutableMap.<String, Object>builder().put("userReputation", currentUser.getReputation())
                        .put("bettingStatus", "")
                        .put("profileImage", "<img  src=\"" + RiotAPI.getIconByName(ChampConsts.getChampNames()
                                .get((Integer.parseInt(currentUser.getID()) % ChampConsts.getChampNames().size()
                                        + ChampConsts.getChampNames().size()) % ChampConsts.getChampNames().size()))
                                + "\">")
                        .put("profileName", currentUser.getUsername()).put("leaderboard", leaderboards).build();
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
            // checks to see if user is logged in and redirects to main page if not
            if (!SessionHandler.isUserLoggedIn(req)) {
                Map<String, Object> variables = ImmutableMap.of("incorrectPassword",
                        "Entered a blank username or password");
                return new ModelAndView(variables, "splash.ftl");
            } else {
                User currentUser = SessionHandler.getUserFromRequestCookie(req, db);
                String champOptions;
                StringBuilder sb = new StringBuilder();
                StringBuilder sb1 = new StringBuilder();
                // builds and styles the list of all the user's bets
                try {
                    for (Bet b : db.getUserBetsOnPatch(currentPatch.get(), currentUser.getID())) {
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
                // maps all the variables in the HTML to their elements
                variables = ImmutableMap.<String, Object>builder().put("userReputation", currentUser.getReputation())
                        .put("bettingStatus", "")
                        .put("profileImage", "<img  src=\"" + RiotAPI.getIconByName(ChampConsts.getChampNames()
                                .get((Integer.parseInt(currentUser.getID()) % ChampConsts.getChampNames().size()
                                        + ChampConsts.getChampNames().size()) % ChampConsts.getChampNames().size()))
                                + "\">")
                        .put("profileName", currentUser.getUsername()).put("success", "true")
                        .put("myBets", sb1.toString()).build();
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
            String username = "username";
            String password = "password";

            // if the login fields are blank AND the create fields are blank
            if ((qm.value("username").equals("") || qm.value("password").equals(""))
                    && (qm.value("newusername").equals("") || qm.value("newusername").equals(""))) {
                successfulLogin = false;
                Map<String, Object> variables = ImmutableMap.of("incorrectPassword",
                        "Entered a blank username or password");

                return new ModelAndView(variables, "splash.ftl");
            }

            // if they are non blank, and the create new account field is blank, then login
            else if (qm.value("newusername").equals("") && qm.value("newusername").equals("")) {

                successfulLogin = SessionHandler.loginUser(req, res, db);
                if (successfulLogin == false) {
                    Map<String, Object> variables = ImmutableMap.of("incorrectPassword", "Incorrect password");

                    return new ModelAndView(variables, "splash.ftl");
                }

                // otherwise, create a new account(if not null)
            } else {
                if (qm.value("newusername").equals("") || qm.value("newpassword").equals("")) {
                    successfulLogin = false;
                    Map<String, Object> variables = ImmutableMap.of("incorrectPassword",
                            "Entered a blank username or password");

                    return new ModelAndView(variables, "splash.ftl");
                }

                successfulLogin = SessionHandler.createNewAccount(res, req, db);
                username = "newusername";
                password = "newpassword";

                // if the creation wentbad (likely due to overlapping username)
                if (successfulLogin == false) {
                    Map<String, Object> variables = ImmutableMap.of("incorrectPassword",
                            "That username has already been taken");

                    return new ModelAndView(variables, "splash.ftl");
                }
            }

            try {
                username = req.queryMap().value(username);
                password = req.queryMap().value(password);
                password = String.valueOf(password.hashCode());
                currentUser = db.getUser(username, password);
                assert currentUser != null;
                String id = String.valueOf(currentUser.getUsername().hashCode());
                // builds and styles the list of all the user's bets
                for (Bet b : db.getUserBetsOnPatch(currentPatch.get(), id)) {
                    sb1.append("<div id=\"userbet\" style=\"background-image: url(" + getSplashByName(b.getCategory())
                            + ") \"><div class=\"champion\"><div class=\"line\">Champion</div>" + b.getCategory()
                            + "</div> <div class=\"type\"> <div class=\"line\">Type</div>" + b.getBetType() + "rate"
                            + "</div> <div class=\"percent\"> <div class=\"line\">Percent Predicted </div>"
                            + b.getPercentChangePredicted() + "%"
                            + "</div> <div class=\"wager\"> <div class=\"line\">Reputation Wagered </div>"
                            + b.getRepWagered() + " </div> <div class=\"betpatch\"> <div class=\"line\">Patch</div>"
                            + currentPatch.get() + "</div></div>");
                }
            } catch (SQLException e) {
                // TODO Auto-generated catch block
                System.out.println("sql error in login");
                e.printStackTrace();
            }

            Map<String, Object> variables = null;
            // maps all the variables in the HTML to their elements
            variables = ImmutableMap.<String, Object>builder().put("userReputation", currentUser.getReputation())
                    .put("bettingStatus", "")
                    .put("profileImage", "<img  src=\""
                            + RiotAPI.getIconByName(ChampConsts.getChampNames()
                                    .get((Integer.parseInt(currentUser.getID()) % ChampConsts.getChampNames().size()
                                            + ChampConsts.getChampNames().size()) % ChampConsts.getChampNames().size()))
                            + "\">")
                    .put("profileName", currentUser.getUsername()).put("success", "").put("myBets", sb1).build();
            return new ModelAndView(variables, "mybets.ftl");
        }
    }

    /**
     * Handler for patch notes page.
     */
    private static class PatchNoteHandler implements TemplateViewRoute {
        @Override
        public ModelAndView handle(Request req, Response res) throws IOException {
            // checks to see if user is logged in and redirects to main page if not
            if (!SessionHandler.isUserLoggedIn(req)) {
                Map<String, Object> variables = ImmutableMap.of("incorrectPassword", "Please log in");
                return new ModelAndView(variables, "splash.ftl");
            } else {
                User currentUser = SessionHandler.getUserFromRequestCookie(req, db);
                // creates all the champion's icons
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
                // maps all the variables in the HTML to their elements
                ImmutableMap.Builder<String, Object> builder = ImmutableMap.builder();
                builder.put("userReputation", currentUser.getReputation());
                builder.put("currentPatchLink",
                        "https://na.leagueoflegends.com/en-us/news/game-updates/patch-10-9-notes/");
                builder.put("currentPatch", patchNotesString);
                builder.put("bettingStatus", "");
                builder.put("profileImage", "<img  src=\""
                        + RiotAPI.getIconByName(ChampConsts.getChampNames()
                                .get((Integer.parseInt(currentUser.getID()) % ChampConsts.getChampNames().size()
                                        + ChampConsts.getChampNames().size()) % ChampConsts.getChampNames().size()))
                        + "\">");
                builder.put("profileName", currentUser.getUsername());
                builder.put("championDivs", championDivs);// .put("userReputation", db.getUser(userID).getReputation())
                variables = builder.build();
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
            // checks to see if user is logged in and redirects to main page if not

            if (!SessionHandler.isUserLoggedIn(req)) {
                Map<String, Object> variables = ImmutableMap.of("incorrectPassword", "Please log in");

                return new ModelAndView(variables, "splash.ftl");
            } else {
                User currentUser = SessionHandler.getUserFromRequestCookie(req, db);

                String champName = req.params(":champname");
                // builds the charts for each statistic
                String wrchart = "";
                String brchart = "";
                String prchart = "";
                try {
                    wrchart = buildMetricChartForChampion(champName, "Win", db.getPatches());
                    brchart = buildMetricChartForChampion(champName, "Ban", db.getPatches());
                    prchart = buildMetricChartForChampion(champName, "Pick", db.getPatches());

                } catch (SQLException e) {
                    System.out.println("problem connecting to databse while building charts");
                }

                Map<String, Object> variables = null;
                // maps all the variables in the HTML to their elements

                variables = ImmutableMap.<String, Object>builder().put("userReputation", currentUser.getReputation())
                        .put("bettingStatus", "")
                        .put("profileImage", "<img class=\"icons\" src=\"" + RiotAPI.getIconByName(ChampConsts
                                .getChampNames()
                                .get((Integer.parseInt(currentUser.getID()) % ChampConsts.getChampNames().size()
                                        + ChampConsts.getChampNames().size()) % ChampConsts.getChampNames().size()))
                                + "\">")
                        .put("profileName", currentUser.getUsername())
                        .put("champSplashimage", getSplashByName(champName)).put("winrateGraph", wrchart)
                        .put("pickrateGraph", prchart).put("banrateGraph", brchart).put("champname", champName)
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
            // checks to see if user is logged in and redirects to main page if not
            if (!SessionHandler.isUserLoggedIn(req)) {
                Map<String, Object> variables = ImmutableMap.of("incorrectPassword", "Please log in");

                return new ModelAndView(variables, "splash.ftl");
            } else {
                String champName = req.params(":champname");

                QueryParamsMap qm = req.queryMap();

                String wper = qm.value("wpercentage");
                String pper = qm.value("ppercentage");
                String bper = qm.value("bpercentage");

                String wstake = qm.value("wstaked");
                String pstake = qm.value("pstaked");
                String bstake = qm.value("bstaked");

                User currentUser = SessionHandler.getUserFromRequestCookie(req, db);
                String error = "";
                if (currentUser != null) {
                    // if the winrate form is filled out, add a winrate bet
                    if (wper != null && Integer.parseInt(wstake) > 0) {
                        try {
                            String betID = String
                                    .valueOf((currentUser.getID() + champName + "Win" + wper + wstake).hashCode());
                            db.createNewBet(betID, currentUser.getID(), champName, "Win", wper, wstake,
                                    currentPatch.get());
                            Bet b = new Bet(betID, currentUser.getID(), Integer.parseInt(wstake),
                                    Double.parseDouble(wper), champName, gain, "Win", currentPatch.get());
                            wr.addBet(b);
                        } catch (SQLException e) {
                            System.out.println("Error adding bet to user with username " + currentUser.getUsername());
                        } catch (RepException e) {
                            error = "Not enough reputation to add that bet!";
                        }
                    }

                    // if the pickrate form is filled out, add a pickrate bet
                    if (pper != null && Integer.parseInt(pstake) > 0) {

                        try {
                            String betID = String
                                    .valueOf((currentUser.getID() + champName + "Pick" + pper + pstake).hashCode());
                            db.createNewBet(betID, currentUser.getID(), champName, "Pick", pper, pstake,
                                    currentPatch.get());
                            Bet b = new Bet(betID, currentUser.getID(), Integer.parseInt(pstake),
                                    Double.parseDouble(pper), champName, gain, "Pick", currentPatch.get());
                            pr.addBet(b);
                        } catch (SQLException e) {
                            System.out.println("Error adding bet to user with username " + currentUser.getUsername());
                        } catch (RepException e) {
                            error = "Not enough reputation to add that bet!";
                        }
                    }

                    // if the banerate form is filled out, add a banrate bet
                    if (bper != null && Integer.parseInt(bstake) > 0) {
                        try {
                            String betID = String
                                    .valueOf((currentUser.getID() + champName + "Ban" + bper + bstake).hashCode());
                            db.createNewBet(betID, currentUser.getID(), champName, "Ban", bper, bstake,
                                    currentPatch.get());
                            Bet b = new Bet(betID, currentUser.getID(), Integer.parseInt(bstake),
                                    Double.parseDouble(bper), champName, gain, "Ban", currentPatch.get());
                            br.addBet(b);
                        } catch (SQLException e) {
                            System.out.println("Error adding bet to user with username " + currentUser.getUsername());
                        } catch (RepException e) {
                            error = "Not enough reputation to add that bet!";
                        }
                    }
                }

                currentUser = SessionHandler.getUserFromRequestCookie(req, db);
                // builds the charts for each statistic
                String wrchart = "";
                String brchart = "";
                String prchart = "";

                try {
                    wrchart = buildMetricChartForChampion(champName, "Win", db.getPatches());
                    brchart = buildMetricChartForChampion(champName, "Ban", db.getPatches());
                    prchart = buildMetricChartForChampion(champName, "Pick", db.getPatches());

                } catch (SQLException e) {
                    System.out.println("problem connecting to databse while building charts");
                }

                Map<String, Object> variables = null;
                // maps all the variables in the HTML to their elements

                variables = ImmutableMap.<String, Object>builder().put("userReputation", currentUser.getReputation())
                        .put("bettingStatus", "")
                        .put("profileImage", "<img class=\"icons\" src=\"" + RiotAPI.getIconByName(ChampConsts
                                .getChampNames()
                                .get((Integer.parseInt(currentUser.getID()) % ChampConsts.getChampNames().size()
                                        + ChampConsts.getChampNames().size()) % ChampConsts.getChampNames().size()))
                                + "\">")
                        .put("profileName", currentUser.getUsername())
                        .put("champSplashimage", getSplashByName(champName)).put("winrateGraph", wrchart)
                        .put("pickrateGraph", prchart).put("banrateGraph", brchart).put("champname", champName)
                        .put("error", error).build();

                return new ModelAndView(variables, "champion.ftl");
            }
        }
    }

    /**
     * Builds a chart for the given champion and metric.
     * 
     * @param champname The champion for which to build the chart
     * @param metric    Whether the chart is for win/pick/ban rate
     * @return A string containing the JS for the chart
     */
    private static String buildMetricChartForChampion(String champname, String metric, List<List<String>> dbpatches) {

        final int NUM_DATA_POINTS = 4;

        String jschart = "";
        String labels = "";
        String ratedata = "";
        String graphTitle = "";
        String color = "";

        // gets the list of patches and uses them as labels
        try {
            // get the patches
            List<List<String>> patches = dbpatches;
            // only get the last N patches to keep load times reasonable
            patches = patches.subList(Math.max(patches.size() - NUM_DATA_POINTS, 0), patches.size());
            if (patches.size() > 0) {
                for (List<String> patch : patches) {
                    labels += "'" + patch.get(0) + "',";
                }
            } else {
                return "";
            }

            switch (metric) {
                case "Win":
                    for (List<String> patch : patches) {
                        Float rate = db.getChampionWinRateFromPatch(patch.get(0).substring(5), champname);
                        ratedata += String.valueOf(rate) + ",";
                    }
                    color = "'rgba(99, 255, 132, 0.8)'";
                    graphTitle = "wrgraph";
                    break;
                case "Pick":
                    for (List<String> patch : patches) {
                        Float rate = db.getChampionPickRateFromPatch(patch.get(0).substring(5), champname);
                        ratedata += String.valueOf(rate) + ",";
                    }
                    graphTitle = "prgraph";
                    color = "'rgba(132, 99, 255, 0.8)'";
                    break;
                case "Ban":
                    for (List<String> patch : patches) {
                        Float rate = db.getChampionBanRateFromPatch(patch.get(0).substring(5), champname);
                        ratedata += String.valueOf(rate) + ",";
                    }
                    color = "'rgba(255, 99, 132, 0.8)'";
                    graphTitle = "brgraph";
                    break;
            }

        } catch (SQLException e) {
            System.out.println("Problem connecting to SQL database while constructing chart");
        }

        /**
         * Adds the JS
         */
        jschart += "<script>";
        jschart += "var myChart = new Chart(" + graphTitle + ", {" + "type: 'line'," + "data: {" + "labels:" + "["
                + labels + "]," + "datasets: [{" + "label: '" + metric + "rate'," + "data: [" + ratedata + "],"
                + "backgroundColor: [" + color + "," + "]," + "borderColor: [" + color + "," + "]," + "borderWidth: 1"
                + "}]" + "}," + "options: {" + "scales: {" + "yAxes: [{" + "ticks: {" + "beginAtZero: false" + "}"
                + "}]" + "}" + "}" + "});";
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
