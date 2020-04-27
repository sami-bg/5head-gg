
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Map;

import RiotAPI.RiotAPI;
import freemarker.template.Configuration;
import com.google.common.collect.ImmutableMap;
import spark.*;
import spark.template.freemarker.FreeMarkerEngine;

public class Main {

    public static void main(String[] args) throws IOException {
        new Main(args).run();
    }

    private String[] args;

    private Main(String[] args) {
        this.args = args;
    }

    private void run() throws IOException {
        // TODO Auto-generated method stub
        //RiotAPI.test();
        runSparkServer(4567);
    }

    private static FreeMarkerEngine createEngine() {
        Configuration config = new Configuration();
        File templates = new File("src/resources/static");
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
        Spark.get("/patchnotes", new PatchNoteHandler(), freeMarker);
        Spark.get("/mybets", new PatchNoteHandler(), freeMarker);
        Spark.get("/leaderboard", new LeaderboardHandler(), freeMarker);
        Spark.get("/champion/:champname", new ChampionPageHandler(), freeMarker);


    }

    private static class FrontHandler implements TemplateViewRoute {
        @Override
        public ModelAndView handle(Request req, Response res) {
            Map<String, Object> variables = ImmutableMap.of("userReputation", "", "googleLogin", "");

            return new ModelAndView(variables, "splash.ftl");
        }
    }

    private static class LeaderboardHandler implements TemplateViewRoute {
        @Override
        public ModelAndView handle(Request req, Response res) {
            Map<String, Object> variables = ImmutableMap.of("userReputation", "");

            return new ModelAndView(variables, "leaderboards.ftl");
        }
    }

    private static class PatchNoteHandler implements TemplateViewRoute {
        @Override
        public ModelAndView handle(Request req, Response res) {
            Map<String, Object> variables = ImmutableMap.<String, Object>builder()
                    .put("userReputation", "")
                    .put("currentPatchLink",
                            "https://na.leagueoflegends.com/en-us/news/game-updates/patch-10-8-notes/")
                    .put("bettingStatus", "")
                    .put("profileImage", "")
                    .put("profileName", "")
                    .put("championDivs", "").build();

            return new ModelAndView(variables, "patchnotes.ftl");
        }
    }

    private static class ChampionPageHandler implements TemplateViewRoute {
        @Override
        public ModelAndView handle(Request req, Response res) {
            String champName = req.params(":champname");

            Map<String, Object> variables = ImmutableMap.of("userReputation", "");

            return new ModelAndView(variables, "champion.ftl");
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
