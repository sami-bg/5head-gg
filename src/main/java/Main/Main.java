package Main;

import Betting.Bet;
import Database.DatabaseEntryFiller;
import Database.DatabaseHandler;
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
        // TODO Auto-generated method stub
        //RiotAPI.test();

        db.read("data/5Head.db");
        DatabaseEntryFiller DBEF = new DatabaseEntryFiller();
        //I'm running this every time we run main so it might take a while on startup
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
        
        //Spark.post("/mybets/success", new BetSuccessHandler(), freeMarker);

    }

    /**
     * Handler for front page.
     */
    private static class FrontHandler implements TemplateViewRoute {
        @Override
        public ModelAndView handle(Request req, Response res) {
            Map<String, Object> variables = ImmutableMap.of("userReputation", "");

            return new ModelAndView(variables, "splash.ftl");
        }
    }

    /**
     * Handler for front page.
     */
    private static class LogoutHandler implements TemplateViewRoute {
        @Override
        public ModelAndView handle(Request req, Response res) {
            Map<String, Object> variables = ImmutableMap.of("userReputation", "");
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
            if (!SessionHandler.isUserLoggedIn(req)) {
                Map<String, Object> variables = ImmutableMap.of("userReputation", "", "googleLogin", "");

                return new ModelAndView(variables, "splash.ftl");
            } else {
                User currentUser = SessionHandler.getUserFromRequestCookie(req, db);
                List<String> top50 = new ArrayList<>();
                String leaderboards = "<div class=\"no-users\">No users.<div>";
                try {
                    for (User u : db.getTopFifty()) {
                        top50.add(u.getUsername() + "     " + u.getReputation());
                    }
                    leaderboards = LeaderboardBuilder.makeLeaderboard(top50);
                } catch (SQLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                Map<String, Object> variables = null;
                variables = ImmutableMap.<String, Object>builder()
                        .put("userReputation", currentUser.getReputation())
                        .put("bettingStatus", "")
                        .put("profileImage", "")
                        .put("profileName", "")
                        .put("leaderboard", leaderboards)
                        .build();
                return new ModelAndView(variables, "leaderboards.ftl");
            }
        }
    }

    private static class MyBetHandler implements TemplateViewRoute {
        @Override
        public ModelAndView handle(Request req, Response res) {
            if (!SessionHandler.isUserLoggedIn(req)) {
                Map<String, Object> variables = ImmutableMap.of("userReputation", "", "googleLogin", "");
                
                return new ModelAndView(variables, "splash.ftl");
            } else {
                User currentUser = SessionHandler.getUserFromRequestCookie(req, db);
                String champOptions;
                StringBuilder sb = new StringBuilder();
                StringBuilder sb1 = new StringBuilder();
                try {
                    for (Bet b : db.getUserBetsOnPatch(currentPatch, currentUser.getID())) {
                        sb1.append(
                                "Category: " + b.getCategory() + " Reputation Wagered: " + b.getRepWagered() + "<br>");
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                List<String> champNames = ChampConsts.getChampNames();
                for (int i = 0; i < champNames.size(); i++) {
                    String currChamp = champNames.get(i);
                    sb.append("<option value=\"" + currChamp + "\">" + currChamp + "</option>");
                }
                Map<String, Object> variables = null;
                variables = ImmutableMap.<String, Object>builder()
                        .put("userReputation", currentUser.getReputation())
                        .put("bettingStatus", "")
                        .put("profileImage", "")
                        .put("profileName", "")
                        .put("champOptions", sb.toString())
                        .put("success", "true")
                        .put("myBets", sb1.toString())
                        .build();
                return new ModelAndView(variables, "mybets.ftl");
            }
        }
    }

    private static class LoginPageHandler implements TemplateViewRoute {

        @Override
        public ModelAndView handle(Request req, Response res) {
            User currentUser = SessionHandler.getUserFromRequestCookie(req, db);
            String champOptions;
            StringBuilder sb = new StringBuilder();
            StringBuilder sb1 = new StringBuilder();
            SessionHandler.loginUser(req, res, db);
            try {
                for (Bet b : db.getUserBetsOnPatch(currentPatch, currentUser.getID())) {
                    sb1.append("Category: " + b.getCategory() + " Reputation Wagered: " + b.getRepWagered() + "<br>");
                }
            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
                List<String> champNames = ChampConsts.getChampNames();
                for (int i = 0; i < champNames.size(); i++) {
                    String currChamp = champNames.get(i);
                    sb.append("<option value=\"" + currChamp + "\">" + currChamp + "</option>");
                }
                Map<String, Object> variables = null;
                variables = ImmutableMap.<String, Object>builder()
                        .put("userReputation", currentUser.getReputation())
                        .put("bettingStatus", "")
                        .put("profileImage", "")
                        .put("profileName", "")
                        .put("champOptions", sb.toString())
                        .put("success", "")
                        .put("myBets", sb1)
                        .build();
                return new ModelAndView(variables, "mybets.ftl");
            }

    }

    // private static class BetSuccessHandler implements TemplateViewRoute {
    //     @Override
    //     public ModelAndView handle(Request req, Response res) {
    //         if (!SessionHandler.isUserLoggedIn(req)) {
    //             Map<String, Object> variables = ImmutableMap.of("userReputation", "", "googleLogin", "");

    //             return new ModelAndView(variables, "splash.ftl");
    //         } else {
    //             StringBuilder sb1 = new StringBuilder();
    //             for (Bet b : Patch.getBets(currentPatch, userID)){
    //                 sb1.append("Category: " + b.getCategory() + " Reputation Wagered: " + b.getRepWagered() + "<br>");
    //             }
    //             QueryParamsMap qm = req.queryMap();
    //             String rep = qm.value("rep");
    //             String percentage = qm.value("percentage");
    //             String champ = qm.value("champion");
    //             String betType = qm.value("betType");
    //             StringBuilder sb = new StringBuilder();
    //             List<String> champNames = ChampConsts.getChampNames();
    //             //TODO: add placed bet to database
    //             for (int i = 0; i < champNames.size(); i++) {
    //                 String currChamp = champNames.get(i);
    //                 sb.append("<option value=\"" + currChamp + "\">" + currChamp + "</option>");
    //             }
    //             Map<String, Object> variables = null;
    //             try {
    //                 if(rep == null || percentage == null || champ == null || betType == null || Integer.parseInt(rep) < 0){
    //                     variables = ImmutableMap.<String, Object>builder()
    //                             .put("userReputation", db.getUser(userID).getReputation())
    //                             .put("bettingStatus", "")
    //                             .put("profileImage", "")
    //                             .put("profileName", "")
    //                             .put("champOptions", sb.toString())
    //                             .put("success", "Bet failed to submit")
    //                             .put("myBets", sb1)
    //                             .build();
    //                 } else {
    //                     db.getUser(userID).submitBet(Integer.parseInt(rep), percentage, champ, betType);
    //                     variables = ImmutableMap.<String, Object>builder()
    //                             .put("userReputation", db.getUser(userID).getReputation())
    //                             .put("bettingStatus", "")
    //                             .put("profileImage", "")
    //                             .put("profileName", "")
    //                             .put("champOptions", sb.toString())
    //                             .put("success", "Bet success!")
    //                             .put("myBets", sb1)
    //                             .build();
    //                 }

    //             } catch (SQLException throwables) {
    //                 throwables.printStackTrace();
    //                 //TODO: display error message
    //             }

    //             return new ModelAndView(variables, "mybets.ftl");
    //         }
    //     }
    // }

    /**
     * Handler for patch notes page.
     */
    private static class PatchNoteHandler implements TemplateViewRoute {
        @Override
        public ModelAndView handle(Request req, Response res) throws IOException {
            if (!SessionHandler.isUserLoggedIn(req)) {
                Map<String, Object> variables = ImmutableMap.of("userReputation", "", "googleLogin", "");

                return new ModelAndView(variables, "splash.ftl");
            } else {
                User currentUser = SessionHandler.getUserFromRequestCookie(req, db);

                String championDivs = "";
                for (String champname : ChampConsts.getChampNames()) {
                    championDivs += "<a href=\"/champion/" + champname + "\">";
                    championDivs += "<div class=\"iconsdiv\">";
                    championDivs += "<img class=\"icons\" src=\"" + RiotAPI.getIconByName(champname) + "\">";
                    championDivs += "</div>";
                    championDivs += "</a>";
                }
                //getElementById("patch-notes-container") gets the entire patch notes, which is not useful. We
                // do getElementsByClass("content-box") instead
                org.jsoup.select.Elements patchNotes = Jsoup.connect(
                        // TODO: Link the current patch notes
                        "https://na.leagueoflegends.com/en-us/news/game-updates/patch-10-9-notes/")
                        .get().getElementsByClass("patch-change-block");
                String patchNotesString = (patchNotes).outerHtml();
                Map<String, Object> variables = null;
                ImmutableMap.Builder<String, Object> builder = ImmutableMap.builder();
                builder.put("userReputation", currentUser.getReputation());
                builder.put("currentPatchLink", "https://na.leagueoflegends.com/en-us/news/game-updates/patch-10-9-notes/");
                builder.put("currentPatch", patchNotesString);
                builder.put("bettingStatus", "");
                builder.put("profileImage", "");
                builder.put("profileName", "");
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
            if (!SessionHandler.isUserLoggedIn(req)) {
                Map<String, Object> variables = ImmutableMap.of("userReputation", "", "googleLogin", "");

                return new ModelAndView(variables, "splash.ftl");
            } else {
                User currentUser = SessionHandler.getUserFromRequestCookie(req, db);

                String champName = req.params(":champname");

                Map<String, Object> variables = null;
                variables = ImmutableMap.<String, Object>builder()
                        .put("userReputation", currentUser.getReputation())
                        .put("bettingStatus", "")
                        .put("profileImage", "")
                        .put("profileName", "")
                        .put("champSplashimage", RiotAPI.getSplashByName(champName))
                        .put("winrateGraph", "")
                        .put("pickrateGraph", "")
                        .put("banrateGraph", "")
                        .put("champname", champName)
                        .build();

                return new ModelAndView(variables, "champion.ftl");
            }
        }
    }

    private static class ChampionBetHandler implements TemplateViewRoute {
        @Override
        public ModelAndView handle(Request req, Response res) {
            if (!SessionHandler.isUserLoggedIn(req)) {
                Map<String, Object> variables = ImmutableMap.of("userReputation", "", "googleLogin", "");

                return new ModelAndView(variables, "splash.ftl");
            } else {
                String champName = req.params(":champname");

                QueryParamsMap qm = req.queryMap();
                System.out.println(qm.toString());

                String wper = req.queryMap().value("wpercentage");
                String pper = req.queryMap().value("ppercentage");
                String bper = req.queryMap().value("bpercentage");

                String wstake = req.queryMap().value("wstaked");
                String pstake = req.queryMap().value("pstaked");
                String bstake = req.queryMap().value("bstaked");

                System.out.println(Arrays.asList(wper, pper, bper).toString());

                User currentUser = SessionHandler.getUserFromRequestCookie(req, db);

                if (currentUser != null) {
                    if (wper != null && Integer.parseInt(wstake) > 0) {

                        try {
                            db.createNewBet(String.valueOf((currentUser.getID() + champName + "Win" + wper + wstake).hashCode()), currentUser.getID(), champName, "Win", wper, wstake, currentPatch);
                        } catch (SQLException e) {
                            System.out.println("Error adding bet to user with username " + currentUser.getUsername());
                        }
                    }
                    if (pper != null && Integer.parseInt(pstake) > 0) {

                        try {
                            db.createNewBet(String.valueOf((currentUser.getID() + champName + "Pick" + wper + wstake).hashCode()), currentUser.getID(), champName, "Win", wper, wstake, currentPatch);
                        } catch (SQLException e) {
                            System.out.println("Error adding bet to user with username " + currentUser.getUsername());
                        }
                    }
                    if (bper != null && Integer.parseInt(bstake) > 0) {

                        try {
                            db.createNewBet(String.valueOf((currentUser.getID() + champName + "Ban" + wper + wstake).hashCode()), currentUser.getID(), champName, "Win", wper, wstake, currentPatch);
                        } catch (SQLException e) {
                            System.out.println("Error adding bet to user with username " + currentUser.getUsername());
                        }
                    }
                }
                
                Map<String, Object> variables = null;
                variables = ImmutableMap.<String, Object>builder()
                        .put("userReputation", currentUser.getReputation())
                        .put("bettingStatus", "")
                        .put("profileImage", "")
                        .put("profileName", "")
                        .put("champSplashimage", getSplashByName(champName))
                        .put("winrateGraph", "")
                        .put("pickrateGraph", "")
                        .put("banrateGraph", "")
                        .put("champname", champName)
                        .build();

                return new ModelAndView(variables, "champion.ftl");
            }
        }
    }

    private static class ChampGraphHandler implements Route {

        @Override
        public Object handle(Request request, Response response) throws Exception {
            if (!SessionHandler.isUserLoggedIn(request)) {
                Map<String, Object> variables = ImmutableMap.of("userReputation", "", "googleLogin", "");

                return new ModelAndView(variables, "splash.ftl");
            } else {
                String champname = request.queryMap().value("champ");
                String winrates, pickrates, banrates, patches;
                winrates = pickrates = banrates = patches = "";
                List<List<String>> patchnums = db.getPatches();
                for (List<String> patch : patchnums) {
                    String p = patch.get(0);
                    winrates += db.getChampionWinRateFromPatch(p, champname) + ",";
                    banrates += db.getChampionBanRateFromPatch(p, champname) + ",";
                    pickrates += db.getChampionPickRateFromPatch(p, champname) + ",";
                    patches += patch + ",";

                }

                Map<String, String> graphData = ImmutableMap.of("patches", patches, "winrates", winrates, "banrates", banrates, "pickrates", pickrates);

                return GSON.toJson(graphData);
            }
        }
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
