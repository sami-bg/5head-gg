//package main.java;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;


import RiotAPI.ChampConsts;
import freemarker.template.Configuration;
import main.java.Database.DatabaseHandler;

import com.google.common.collect.ImmutableMap;

import spark.*;
import spark.template.freemarker.FreeMarkerEngine;

import static RiotAPI.RiotAPI.getIconByName;
import static RiotAPI.RiotAPI.updateMapOfChamps;

import com.google.gson.Gson;

public final class Main {

    private static final Gson GSON = new Gson();

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
        //I'm running this every time we run main so it might take a while on startup
        updateMapOfChamps();
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
        	List<String> top50 = null;
        	String first, second, third;
			try {
				top50 = DatabaseHandler.getTopFifty();
				//first = top50.get(0).getUsername(); //getUsername doesnt exist in user, i think we should add that
				//second = top50.get(1).getUsername();
				//third = top50.get(2).getUsername();
				top50.remove(0);
				top50.remove(1);
				top50.remove(2);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        	Map<String, Object> variables = ImmutableMap.<String, Object>builder()
                    .put("userReputation", "")
                    .put("bettingStatus", "")
                    .put("profileImage", "")
                    .put("profileName", "")
                    //.put("firstplace", first)
                    //.put("secondplace", second)
                    //.put("thirdplace", third)
                    .put("remainingplaces", "").build();

            return new ModelAndView(variables, "leaderboards.ftl");
        }
    }

    private static class PatchNoteHandler implements TemplateViewRoute {
        @Override
        public ModelAndView handle(Request req, Response res) {
            String championDivs = "";
            for (String champname : ChampConsts.getChampNames()) {
                championDivs += "<img src=\"" + getIconByName(champname) + "\">";
            }
            Map<String, Object> variables = ImmutableMap.<String, Object>builder()
                    .put("userReputation", "")
                    .put("currentPatchLink",
                            "https://na.leagueoflegends.com/en-us/news/game-updates/patch-10-8-notes/")
                    .put("bettingStatus", "")
                    .put("profileImage", "")
                    .put("profileName", "")
                    .put("championDivs", championDivs).build();

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
