package main.java;
import java.io.File;
import java.io.IOException;
import java.util.Map;

import com.google.common.collect.ImmutableMap;

import RiotAPI.RiotAPI;
import freemarker.template.Configuration;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.Spark;
import spark.TemplateViewRoute;
import spark.template.freemarker.FreeMarkerEngine;
public class Main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
    //RiotAPI.test();
	}
	
	private static FreeMarkerEngine createEngine() {
	    Configuration config = new Configuration();
	    File templates = new File("src/main/resources/spark/template/freemarker");
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
	    Spark.externalStaticFileLocation("src/main/resources/static");
	    Spark.exception(Exception.class, new ExceptionPrinter());

	    FreeMarkerEngine freeMarker = createEngine();

	    // Setup Spark Routes
	    Spark.get("/stars", new FrontHandler(), freeMarker);
	    Spark.post("/neighbors_name", new NeighborsNameHandler(), freeMarker);
	  }
	  
	  private static class FrontHandler implements TemplateViewRoute {
		    @Override
		    public ModelAndView handle(Request req, Response res) {
		      Map<String, Object> variables = ImmutableMap.of("title", "Stars", "suggestions", "");

		      return new ModelAndView(variables, "query.ftl");
		    }
		  }
	
	

}
