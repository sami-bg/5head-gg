package main.java;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;


import RiotAPI.ChampConsts;
import RiotAPI.RiotAPI;
import freemarker.template.Configuration;
import Database.DatabaseHandler;

import com.google.common.collect.ImmutableMap;

import spark.*;
import spark.template.freemarker.FreeMarkerEngine;
import com.google.gson.Gson;

import static RiotAPI.RiotAPI.getSplashByName;

public final class Main {

  static String userID;

  static DatabaseHandler db = new DatabaseHandler();

  private static final Gson GSON = new Gson();

  public static void main(String[] args) throws IOException {
    new Main(args).run();
  }

  private final String[] args;

  private Main(String[] args) {
    this.args = args;
  }

  private void run() throws IOException {
    // TODO Auto-generated method stub
    //RiotAPI.test();

    //db.read("users.db");
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
    Spark.get("/currpatch", new PatchNoteHandler(), freeMarker);
    Spark.get("/mybets", new MyBetHandler(), freeMarker);
    Spark.post("/mybets/success", new BetSuccessHandler(), freeMarker);
    Spark.get("/leaderboard", new LeaderboardHandler(), freeMarker);
    Spark.get("/champion/:champname", new ChampionPageHandler(), freeMarker);


  }

  /**
   * Handler for front page.
   */
  private static class FrontHandler implements TemplateViewRoute {
    @Override
    public ModelAndView handle(Request req, Response res) {
      Map<String, Object> variables = ImmutableMap.of("userReputation", "", "googleLogin", "");

      return new ModelAndView(variables, "splash.ftl");
    }
  }

  /**
   * Handler for leaderboards page.
   */
  private static class LeaderboardHandler implements TemplateViewRoute {
    @Override
    public ModelAndView handle(Request req, Response res) {
      List<String> top50 = null;
      String first, second, third;
      try {
        top50 = db.getTopFifty();
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
      Map<String, Object> variables = null;
      //try {
      variables = ImmutableMap.<String, Object>builder()
          .put("userReputation", "")
          //.put("userReputation", db.getUser(userID).getReputation())
          .put("bettingStatus", "")
          .put("profileImage", "")
          .put("profileName", "")
//.put("firstplace", first)
//.put("secondplace", second)
//.put("thirdplace", third)
          .put("remainingplaces", "")
          .build();
      //} catch (SQLException throwables) {
      //    throwables.printStackTrace();
      //    //TODO: display error message
      //}

      return new ModelAndView(variables, "leaderboards.ftl");
    }
  }

  private static class MyBetHandler implements TemplateViewRoute {

    @Override
    public ModelAndView handle(Request req, Response res) {
      String champOptions;
      StringBuilder sb = new StringBuilder();
      List<String> champNames = ChampConsts.getChampNames();
      for (int i = 0; i < champNames.size(); i++) {
        String currChamp = champNames.get(i);
        sb.append("<option value=\"" + currChamp + "\">" + currChamp + "</option>");
      }
      Map < String, Object > variables = null;
      //try {
      variables = ImmutableMap.<String, Object>builder()
          .put("userReputation", "")
          //.put("userReputation", db.getUser(userID).getReputation())
          .put("bettingStatus", "")
          .put("profileImage", "")
          .put("profileName", "")
          .put("champOptions", sb.toString())
          .put("success", "")
          .build();
      //} catch (SQLException throwables) {
      //    throwables.printStackTrace();
      //    //TODO: display error message
      //}

      return new ModelAndView(variables, "mybets.ftl");
    }
  }

  private static class BetSuccessHandler implements TemplateViewRoute {
    @Override
    public ModelAndView handle(Request req, Response res) {
      QueryParamsMap qm = req.queryMap();
      Integer rep = Integer.parseInt(qm.value("rep"));
      String champ = qm.value("champion");
      String betType = qm.value("betType");
      String champOptions;
      StringBuilder sb = new StringBuilder();
      List<String> champNames = ChampConsts.getChampNames();
      //TODO: add placed bet to database
      for (int i = 0; i < champNames.size(); i++) {
        String currChamp = champNames.get(i);
        sb.append("<option value=\"" + currChamp + "\">" + currChamp + "</option>");
      }
      Map < String, Object > variables = null;
      //try {
      variables = ImmutableMap.<String, Object>builder()
          .put("userReputation", "")
          //.put("userReputation", db.getUser(userID).getReputation())
          .put("bettingStatus", "")
          .put("profileImage", "")
          .put("profileName", "")
          .put("champOptions", sb.toString())
          .put("success", "Bet submitted!")
          .build();
      //} catch (SQLException throwables) {
      //   throwables.printStackTrace();
      //   //TODO: display error message
      //}

      return new ModelAndView(variables, "mybets.ftl");
    }
  }

  /**
   * Handler for patch notes page.
   */
  private static class PatchNoteHandler implements TemplateViewRoute {
    @Override
    public ModelAndView handle(Request req, Response res) {
      String championDivs = "";
      for (String champname : ChampConsts.getChampNames()) {
        championDivs += "<img src=\"" + RiotAPI.getIconByName(champname) + "\">";
      }
      Map<String, Object> variables = null;
      //try {
      variables = ImmutableMap.<String, Object>builder()
          .put("userReputation", "")
          //.put("userReputation", db.getUser(userID).getReputation())
          .put("currentPatchLink",
              "https://na.leagueoflegends.com/en-us/news/game-updates/patch-10-8-notes/")
          .put("bettingStatus", "")
          .put("profileImage", "")
          .put("profileName", "")
          .put("championDivs", championDivs)
          .build();
      //} catch (SQLException throwables) {
      //    throwables.printStackTrace();
      //    //TODO: display error message
      //}

      return new ModelAndView(variables, "patchnotes.ftl");
    }
  }

  /**
   * Handler for a champion's page.
   */
  private static class ChampionPageHandler implements TemplateViewRoute {
    @Override
    public ModelAndView handle(Request req, Response res) {
      String champName = req.params(":champname");

      Map<String, Object> variables = null;
      //try {
      variables = ImmutableMap.<String, Object>builder()
          .put("userReputation", "")
          //.put("userReputation", db.getUser(userID).getReputation())
          .put("bettingStatus", "")
          .put("profileImage", "")
          .put("profileName", "")
          .put("champSplashimage", getSplashByName(champName))
          .put("winrateGraph", "")
          .put("pickrateGraph", "")
          .put("banrateGraph", "")
          .build();
      //} catch (SQLException throwables) {
      //    //TODO: display error message
      //   throwables.printStackTrace();
      //}

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
