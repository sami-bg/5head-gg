import Database.DatabaseEntryFiller;
import Database.DatabaseHandler;
import org.junit.Before;
import org.junit.Test;

public class UserTest {
    DatabaseHandler DHTest;
    DatabaseEntryFiller DEFTest;
    @Before // setup()
    public void before() throws Exception {
         DEFTest = new DatabaseEntryFiller();
         DHTest = new DatabaseHandler();
    }

    @Test
    public void MakeUserTest() throws Exception {
        before();
    DHTest.addNewUser("testUser.", "testUser", "10000", "", "");
       // PreparedStatement prep = conn.prepareStatement("DELETE from Users");
        
    }
}
