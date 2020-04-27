package Betting;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class to hold all the bets made during one patch.
 * @author sboughan
 *
 */
public class BettingSession {

  private final String type;

  // Map of champion to List<Bet>
  private Map<String, List<Bet>> mapOfChampionToBets = new HashMap<>();
  // Map of user ID to List<Bet>
  private Map<String, List<Bet>> mapOfUserToBets = new HashMap<>();

  public Map<String, List<Bet>> getMapOfChampionToBets() {
    return this.mapOfChampionToBets;
  }

  /**
   * Submits a bet to the current betting session.
   * @param b The bet to be added
   */
  public void addBet(final Bet b) {
	  //adds the bet to the map of bets by champion
	  //by mutating the list in the value
	List<Bet> champ = mapOfChampionToBets.get(b.getCategory());	
    if (champ != null) {
      champ.add(b);
    } else {
    	//if the champion is not a key, put the champion as a key
    	//with the new bet as a member of the value list.
	    List<Bet> newChamp = new ArrayList<Bet>();
	    newChamp.add(b);
    	mapOfChampionToBets.put(b.getCategory(), newChamp);

    }
    //adds the bet to the map of bets by user
    //by mutating the list in the value
	  List<Bet> user = mapOfUserToBets.get(b.getUserID());
    if (user != null) {
      user.add(b);
    } else {
    	//if the user ID is not a key, put the ID as a key
    	//with the new bet as a member of the value list.
	    List<Bet> newUser = new ArrayList<Bet>();
	    newUser.add(b);
    	mapOfUserToBets.put(b.getUserID(), newUser);
    }
  }

  /**
   * Method to get the list of bets a user has made.
   * @param id The user to search for
   * @return the list of bets whose user ID matches that 
   * of the given string
   */
  public List<Bet> getBetsFromUserID(String id) {
    return mapOfUserToBets.get(id);
  }

  /**
   * Bet type getter.
   * @return the typeof this bet
   */
  public String getType() {
    return this.type;
  }

  /**
   * Default constructor.
   * @param type the statistic the bets in the session
   * will be made respect to
   */
  BettingSession(String type) {
    this.type = type;
  }

  /**
   * Calculates the payouts for all the bets made on a specific champion
   * in the current session.
   * @param result the actual statistic for the champion during the patch
   * @param category The category (i.e. champion) to calculate the payouts for
   */
  public void broadcast(Double result, String category) {
    for (Bet b: mapOfChampionToBets.get(category)) {
      b.calculateChange(result);
    }
  }


}
