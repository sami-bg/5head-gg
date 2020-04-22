package Betting;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BettingSession {

  private final String type;

  // Map of champion to List<Bet>
  private Map<String, List<Bet>> mapOfChampionToBets;
  private Map<String, List<Bet>> mapOfUserToBets;

  public Map<String, List<Bet>> getMapOfChampionToBets() {
    return this.mapOfChampionToBets;
  }

  public void addBet(final Bet b) {
    if (mapOfChampionToBets.containsKey(b.getCategory())) {
      mapOfChampionToBets.get(b.getCategory()).add(b);
    } else {
      mapOfChampionToBets.put(b.getCategory(), new ArrayList<Bet>(){{
        add(b);
      }});
    }
    if (mapOfUserToBets.containsKey(b.getUserID())) {
      mapOfUserToBets.get(b.getUserID()).add(b);
    } else {
      mapOfUserToBets.put(b.getUserID(), new ArrayList<Bet>(){{
        add(b);
      }});
    }
  }

  public List<Bet> getBetsFromUserID(String id) {
    return mapOfUserToBets.get(id);
  }

  public String getType() {
    return this.type;
  }

  BettingSession(String type) {
    this.type = type;
  }

  public void broadcast(Double result, String category) {
    for (Bet b: mapOfChampionToBets.get(category)) {
      b.calculateChange(result);
    }
  }


}
