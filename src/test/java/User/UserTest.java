package User;
import org.junit.Before;
import org.junit.Test;


public class UserTest {
    Database.DatabaseHandler DHTest;
    Database.DatabaseEntryFiller DEFTest;
    @Before // setup()
    public void before() throws Exception {
         DEFTest = new Database.DatabaseEntryFiller();
         DHTest = new Database.DatabaseHandler();
    }

    @Test
    public void MakeUserTest() throws Exception {
        before();
    DHTest.addNewUser("testUser.", "testUser", "10000", "", "");
       // PreparedStatement prep = conn.prepareStatement("DELETE from Users");
        
    }
}
