package RiotAPI;
import org.jsoup.*;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RiotAPI {
  /**
   * NOTE: Format of value is {Winrate, pickrate, banrate}.
   */
  private static Map<String, List<Double>> mapOfChampToWinPickBan = new HashMap<>();

  /**
   *
   * @return a map of String -> String, i.e. chogath -> https://static.u.gg/assets/lol/riot_static/10.8.1/img/splash/Chogath_0.jpg
   */

  private static Map<String, List<String>> mapOfChampToImageURL = new HashMap<>();

  public static Map<String, List<String>> getMapOfChampToImageURL() {
    return mapOfChampToImageURL;
  }

  /**
   *
   * @return a map of String: champname _> List of WR PR BR: doubles.
   */
  public static Map<String, List<Double>> getMapOfChampToWinPickBan() {
    return mapOfChampToWinPickBan;
  }

  /**
   * Overloaded method that takes in a champ name and updates map for that champ.
   * Mostly for debugging purposes.
   * @param champname - Original string of champ name - i.e. Jarvan IV not jarvaniv, Kai'sa not kaisa.
   */
  public static void updateMapOfChamps(String champname) {
    try {
      // We get name into appropriate format, i.e. Jarvan IV --> jarvaniv, cho'gath --> chogath
      String urlFriendlyName = urlFriendlyName(champname);
      // We visit the site and parse the rates
      Document document = Jsoup.connect("https://u.gg/lol/champions/" + urlFriendlyName + "/build").get();

      Elements price = document.select(".value:contains(%)");
      //Construct the list to be used as value
      List<Double> listOfWrPrBr = new ArrayList<>();
      for (org.jsoup.nodes.Element element : price) {
        Double rate = Double.parseDouble(element.text().replace("%", ""));
        listOfWrPrBr.add(rate);
      }
      // NOTE: Original champname gets put into map. i.e. Jarvan IV NOT jarvaniv
      mapOfChampToWinPickBan.put(champname, listOfWrPrBr);

      List<String> splashIconList = new ArrayList<>();
      Elements background = document.getElementsByClass("champion-profile-container");
      String style = background.get(0).attr("style");
      String splash = style.substring(style.indexOf("https://"), style.indexOf(".jpg") + 4);
      String icon = document.getElementsByClass("champion-image").attr("src");
      splashIconList.add(splash);
      splashIconList.add(icon);
      mapOfChampToImageURL.put(urlFriendlyName, splashIconList);

    } catch (IOException | NullPointerException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (NumberFormatException e) {
      System.out.println("Couldn't parse rate for " + champname);
      e.printStackTrace();
    }
  }

  /**
   * Overloaded method that takes no argument, and updates map of champ for every champion.
   */
  public static void updateMapOfChamps() {
    for (String champname : ChampConsts.getChampNames()) {
      updateMapOfChamps(champname);
    }
  }

  /**
   *
   * @param champname - Champ name for which to get Icon for: its a .png
   *
   * @return String denoting icon url
   * For instance: Ashe -> https://static.u.gg/assets/lol/riot_static/10.8.1/img/champion/Ashe.png
   */
  public static String getIconByName(String champname) {
    String urlFriendlyName = urlFriendlyName(champname);
    return mapOfChampToImageURL.get(urlFriendlyName).get(1);
  }

  /**
   *
   * @param champname - Champ name for which to get Splash for: its a .jpg
   *
   * @return String denoting icon url
   * For instance: Cho'gath -> https://static.u.gg/assets/lol/riot_static/10.8.1/img/splash/Chogath_0.jpg
   */
  public static String getSplashByName(String champname) {
    String urlFriendlyName = urlFriendlyName(champname);
    return mapOfChampToImageURL.get(urlFriendlyName).get(0);
  }

  private static String urlFriendlyName(String champname) {
    String urlFriendlyName = champname.toLowerCase().replace("'", "");
    urlFriendlyName = urlFriendlyName.replace(" ", "");
    return urlFriendlyName;
  }
}
