package User;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;


public class UserTest {
    Database.DatabaseHandler DHTest;
    Database.DatabaseEntryFiller DEFTest;
    @Before // setup()
    public void before() throws Exception {
         DEFTest = new Database.DatabaseEntryFiller();
         DHTest = new Database.DatabaseHandler();
         DHTest.read("data/5HeadTest.db");
    }

    @After
    public void after() throws Exception {
        DHTest.updateData("DELETE FROM users", new ArrayList<>());
        DHTest.updateData("DELETE FROM bets", new ArrayList<>());

    }

    @Test
    public void MakeUserTest() throws Exception {
        before();
        DHTest.addNewUser("testUser.", "testUser", "10000", "", "a");
        assertEquals(DHTest.getUser("testUser.").getReputation(), 10000);
        assertEquals(DHTest.getUser("testUser.").getAuth(), "a");

        assertEquals(DHTest.getUser("testUser", "a").getUsername(), "testUser");
        DHTest.createNewBet("betID", "testUser.", "Akali","win", "50", "100", "10.9");
        assertEquals(DHTest.getUserBetsOnPatch("10.9", "testUser.").get(0).getBetType(), "win");
        after();
    }
}
