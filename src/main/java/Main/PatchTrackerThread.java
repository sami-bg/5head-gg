package Main;

import Betting.Bet;
import Betting.BettingSession;
import Database.DatabaseHandler;
import RiotAPI.ChampConsts;
import RiotAPI.RiotAPI;
import com.google.common.util.concurrent.AbstractScheduledService;
import com.google.common.util.concurrent.AbstractScheduledService.Scheduler.*;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static com.google.common.util.concurrent.AbstractScheduledService.Scheduler.newFixedRateSchedule;


public class PatchTrackerThread extends TimerTask {

  private final BettingSession wrSession;
  private final BettingSession prSession;
  private final BettingSession brSession;
  private final DatabaseHandler db;
  private static AtomicReference<String> patch;
  /**
   * IMPORTANT: Interval in SECONDS of how often to loop
   */
  private Integer interval;

  protected void runOneIteration() {
    System.out.println("Running iteration with interval " + interval);
    getAndUpdateCurrentPatch();
    if (hasPatchBeenReleasedWithin(0, this.interval)) {
      Map<String, List<Double>> oldMetrics = RiotAPI.getMapOfChampToWinPickBan();
      RiotAPI.updateMapOfChamps();
      Map<String, List<Double>> newMetrics = RiotAPI.getMapOfChampToWinPickBan();
      // If so, releases rewards for all champions, writes to db, empties bettingsession.
      for (String hero: ChampConsts.getChampNames()) {
        Double oldWinrate = oldMetrics.get(hero).get(0);
        Double newWinrate = newMetrics.get(hero).get(0);
        wrSession.broadcast(newWinrate, oldWinrate, hero);

        Double oldPickrate = oldMetrics.get(hero).get(1);
        Double newPickrate = newMetrics.get(hero).get(1);
        prSession.broadcast(newPickrate, oldPickrate, hero);

        Double oldBanrate = oldMetrics.get(hero).get(2);
        Double newBanrate = newMetrics.get(hero).get(2);
        brSession.broadcast(newBanrate, oldBanrate, hero);
      }

      //Write gains of each bet to db
      updateBetGainsForSession(wrSession);
      updateBetGainsForSession(prSession);
      updateBetGainsForSession(brSession);
      //Update gain for each user.
      updateReputationForUsersInSession(wrSession);
      updateReputationForUsersInSession(prSession);
      updateReputationForUsersInSession(brSession);
      //Add column of new patch's wrprbr in databse
      updateDatabaseMetrics(newMetrics);
      // Reset the betting sessions
      this.wrSession.resetSession();
      this.prSession.resetSession();
      this.brSession.resetSession();
    } else return;
  }

  private void updateDatabaseMetrics(Map<String, List<Double>> newMetrics) {
    String currPatchString = getAndUpdateCurrentPatch().get();
    try {
      db.createNewPatch(currPatchString);
      for (String hero: ChampConsts.getChampNames()) {
        Double winrate = newMetrics.get(hero).get(0);
        Double pickrate = newMetrics.get(hero).get(1);
        Double banrate = newMetrics.get(hero).get(2);
        db.addRatestoChamps(hero, currPatchString, String.valueOf(winrate), String.valueOf(pickrate), String.valueOf(banrate));
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  private void updateReputationForUsersInSession(BettingSession session) {
    List<String> userIDs = session.getUsers();
    for (String userID: userIDs) {
      List<Bet> bets = session.getBetsFromUserID(userID);
      int reputationChange = 0;
      for (Bet b: bets) {
        reputationChange += SigmoidAdjustedGain.calculateSigmoidReputationChange(b.getRepWagered(), b.getGain());
      }
      db.addToUserReputation(reputationChange, userID);
    }
  }

  private void updateBetGainsForSession(BettingSession session) {
    List<String> users = session.getUsers();

    for (String user: users) {
      List<Bet> betsForUser = session.getBetsFromUserID(user);
      for (Bet bet: betsForUser) {
        db.updateBetGains(bet);
      }
    }
  }

  public PatchTrackerThread(Integer day, Integer seconds,
                     BettingSession wr,
                     BettingSession pr,
                     BettingSession br,
                     DatabaseHandler db,
                     AtomicReference<String> patchRef) {
    this.interval = (day * 3600 * 24) + seconds;
    this.wrSession = wr;
    this.prSession = pr;
    this.brSession = br;
    this.db = db;
    patch = patchRef;
  }
  /**
   *
   * @param day checks if patch has been released within this many days.
   * @return boolean if patch has been released
   */

  public static Boolean hasPatchBeenReleasedWithin(Integer day, Integer seconds) {
    //Integer representing SECONDS of interval to check when patch was released.
    Integer interval = (day * 24 * 3600) + seconds;
    long howManyDaysAgoHasPatchBeenReleased = ChronoUnit.SECONDS.between(getPreviousPatchDate(), LocalDateTime.now());
    return howManyDaysAgoHasPatchBeenReleased <= interval;
  }

  /**
   * Scrapes the date from https://na.leagueoflegends.com/en-us/news/tags/patch-notes and uses LocalDate.parse
   * to turn it into LocalDate format.
   * @return
   */
  public static LocalDateTime getPreviousPatchDate() {
    //We get the first <time> tag from the url above.
    //Parse the datetime feature within that time tag and return it
    try {
      Document patchPage = Jsoup.connect("https://na.leagueoflegends.com/en-us/news/tags/patch-notes").get();
      Element firstTimeTag = patchPage.getElementsByTag("time").get(0);
      String tagString = firstTimeTag.toString();
      String datetime = tagString.substring(tagString.indexOf("datetime="), tagString.indexOf("class"));
      datetime = datetime.substring(datetime.indexOf("=") + 2, datetime.indexOf("Z") - 1);
      datetime = datetime.replace("Z\" ", "");
      return LocalDateTime.parse(datetime);
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }

  /**
   * Scrapes and returns the current patch we are on. i.e. "10.9".
   * Does this by visiting u.gg and getting current patch.
   * @return String of current patch.
   */
  public AtomicReference<String> getAndUpdateCurrentPatch() {
    try {
      Document uggAatroxPage = Jsoup.connect("https://u.gg/lol/champions/aatrox/build").get();
      Element patchNumberTag = uggAatroxPage.getElementsByClass("select-value-label").get(0);
      patch.set(patchNumberTag.text());
      System.out.println(patch.get());
      return patch;
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }


  @Override
  public void run() {
    runOneIteration();
  }
}
