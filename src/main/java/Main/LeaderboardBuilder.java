package Main;

import java.util.List;

public class LeaderboardBuilder {

  /**
   *
   * @param entries - all the users in the top50
   * @return string of html. Each entry is a div, with class="leaderboard-entry"
   * first, 2nd, 3rd places have unique ids:
                              first-place, second-place, third-place
   * all are delineated by a \n
   */
  public static String makeLeaderboard(List<String> entries) {
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("<div id=\"top-3\">");
    for (int i = 0; i < entries.size() && i < 3; i++) {
      if (i == 0) {
        stringBuilder.append("<div class=\"top-entry\" id=\"first-place\">");
        stringBuilder.append("<div class=\"ranking-number\">" + (i + 1) + "</div>");
        stringBuilder.append("<div class=\"user-id\">" + entries.get(i) + "</div>");
        stringBuilder.append("</div>\n");
      }
      if (i == 1) {
        stringBuilder.append("<div class=\"top-entry\" id=\"second-place\">");
        stringBuilder.append("<div class=\"ranking-number\">" + (i + 1) + "</div>");
        stringBuilder.append("<div class=\"user-id\">" + entries.get(i) + "</div>");
        stringBuilder.append("</div>\n");
      }
      if (i == 2) {
        stringBuilder.append("<div class=\"top-entry\" id=\"third-place\">");
        stringBuilder.append("<div class=\"ranking-number\">" + (i + 1) + "</div>");
        stringBuilder.append("<div class=\"user-id\">" + entries.get(i) + "</div>");
        stringBuilder.append("</div>\n");
      }
    }
    stringBuilder.append("</div>\n");
    stringBuilder.append("<div id=\"top-50\">");
    for (int i = 3; i < entries.size(); i++) {
      stringBuilder.append("<div ");
      stringBuilder.append("class=\"leaderboard-entry\"");
      stringBuilder.append(">");
      stringBuilder.append("<div class=\"ranking-number\">" + (i + 1) + "</div>");
      stringBuilder.append("<div class=\"user-id\">" + entries.get(i) + "</div>");
      stringBuilder.append("</div>\n");
    }
    stringBuilder.append("</div>\n");
    return stringBuilder.toString();
  }
}
