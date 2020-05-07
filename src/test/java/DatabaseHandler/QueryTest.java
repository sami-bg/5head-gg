package DatabaseHandler;

import Betting.Bet;
import Database.DatabaseHandler;
import Main.Champion;
import Main.SigmoidAdjustedGain;
import Main.User;
import org.junit.Before;
import org.junit.Test;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class QueryTest {
DatabaseHandler db = null;
    @Before
    public void setUp() throws Exception {
        db = new DatabaseHandler();
        db.read("data/5HeadTest.db");
    }

    @Test
    public void testErrors() throws SQLException {
        db = new DatabaseHandler();
        db.read("fakedb");
            assertThrows(SQLException.class, () -> {db.getUser("any");});
        assertThrows(SQLException.class, () -> db.addNewUser("usrID", "usrName", "320", "brown.edu", "pswrd"));
        assertEquals(db.queryData(null, null), Arrays.asList(new ArrayList<String>()));
        assertThrows(SQLException.class, () -> db.createNewBet("2", "usr_ID_1", "Aatrox", "Ban", "0.5", "50", "10.10"));

    }

    @Test
    public void testUserQueries() {
        User usr1 = new User("usrID", "usrName", 320, "brown.edu", "pswrd");
        User usr2 = new User("usrID1", "usrName1", 9000, "gmail.com", "pswrd1");
        try {
            setUp();
            db.addNewUser("usrID", "usrName", "320", "brown.edu", "pswrd");
            db.addNewUser("usrID1", "usrName1", "9000", "gmail.com", "pswrd1");
            assertEquals(db.getUser("usrID").getID(), usr1.getID());
            assertEquals(db.getUser("usrName1", "pswrd1").getID(), usr2.getID());
            db.updateReputation("usrID", "1000");
            assertEquals(db.getUser("usrID").getReputation(), 1000);
            assertEquals(db.getTopFifty().size(), 2);
            assertEquals(db.getTopFifty().get(0).getUsername(), "usrName1");
            assertEquals(db.getTopFifty().get(1).getUsername(), "usrName");
            db.addNewUser("usrID2", "usrName2", "9000", "gmail.com", "pswrd1");
            assertEquals(db.getTopFifty().get(0).getReputation(), db.getTopFifty().get(1).getReputation());
            db.deleteData("Users");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

   @Test
    public void testChampionQueries() {
        List<String> winRate = new ArrayList<>();
        List<String> banRate = new ArrayList<>();
        List<String> pickRate = new ArrayList<>();
        winRate.add("Aatrox");
        winRate.add("0.55");
        banRate.add("Aatrox");
        banRate.add("0.75");
        pickRate.add("Aatrox");
        pickRate.add("0.45");
        Champion aatrox = new Champion("Aatrox", winRate, banRate, pickRate);
        try {
            setUp();
            db.addChampion("Aatrox");
            db.addRatestoChamps("Aatrox", "10.10", "0.55", "0.75", "0.45");
            assertEquals(db.getChampion("Aatrox").name, aatrox.name);
            System.out.println("Print: " + db.getChampionWinRateFromPatch("10.10", "Aatrox"));
            assertEquals(db.getChampionWinRateFromPatch("10.10", "Aatrox"), 0.55f, 0.0f);
            assertEquals(db.getChampionBanRateFromPatch("10.10", "Aatrox"),0.75f, 0.0f);
            assertEquals(db.getChampionPickRateFromPatch("10.10", "Aatrox"), 0.45f, 0.0f);
            db.addRatestoChamps("Aatrox", "10.11", "0.35", "0.25", "0.45");
            assertEquals(db.getChampionWinRateFromPatch("10.11", "Aatrox"), 0.35f, 0.0f);
            assertEquals(db.getChampionWinRateFromPatch("10.11", "Atrox"), 0.0, 0.001);
            assertEquals(db.getChampionPickRateFromPatch("10.11", "Atrox"), 0.0, 0.001);
            assertEquals(db.getChampionBanRateFromPatch("10.11", "Atrox"), 0.0, 0.001);
            db.deleteData("WinRate");
            db.deleteData("BanRate");
            db.deleteData("PickRate");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testBetQueries() {
        SigmoidAdjustedGain gainFunc = new SigmoidAdjustedGain(1.5, 0.75, 0.0, 0.0);
        Bet bet1 = new Bet("1", "usr_ID", 50, 0.5, "Aatrox", gainFunc, "Pick", "10.10");
        Bet bet2 = new Bet("2", "usr_ID_1", 50, 0.5, "Aatrox", gainFunc, "Ban", "10.10");
        Bet bet4 = new Bet("4", "usr_ID_1", 50, 0.5, "Mumu", gainFunc, "Pick", "10.10");

        List<Bet> bets = new ArrayList<>();
        bets.add(bet2);
        bets.add(bet4);
        try {
            setUp();
            db.addNewUser("usr_ID", "usrName", "320", "brown.edu", "pswrd");
            db.addNewUser("usr_ID_1", "usrName1", "9000", "gmail.com", "pswrd1");
            db.createNewBet("1", "usr_ID", "Aatrox", "Pick", "0.5", "50", "10.10");
            db.createNewBet("2", "usr_ID_1", "Aatrox", "Ban", "0.5", "50", "10.10");
            db.createNewBet("4", "usr_ID_1", "Mumu", "Pick", "0.5", "50", "10.10");
            db.createNewBet("3", "usr_ID_1", "Aatrox", "Win", "0.5", "50", "10.11");
            assertThrows(DatabaseHandler.RepException.class, () -> db.createNewBet("5", "usr_ID", "Taric", "Pick", "0.5", "280", "10.10"));
            assertEquals(db.getUser("usr_ID").getReputation(), 270);
            db.createNewBet("5", "usr_ID", "Senna", "Pick", "0.5", "270", "10.10");
            assertEquals(db.getUser("usr_ID").getReputation(), 0);
            assertEquals(db.getUser("usr_ID_1").getReputation(), 8850);
            assertEquals(db.getBet("1").getUserID(), bet1.getUserID());
            assertEquals(db.getUserBetsOnPatch("10.10", "usr_ID_1").get(0).getBetID(), bets.get(0).getBetID());
            assertTrue(db.countNumberOfBets("Aatrox", "10.10") == 2);
            assertTrue(db.countNumberOfBets("Lucian", "10.10") == 0);
            assertTrue(db.countNumberOfBets("Taric", "10.10") == 0);
            assertThrows(SQLException.class, () -> db.getBet("3577"));
            db.deleteData("Bets");
            db.deleteData("Users");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
