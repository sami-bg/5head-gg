package Main;
import Betting.Bet;
import Database.DatabaseHandler;
import Main.Champion;
import Main.SigmoidAdjustedGain;
import Main.User;
import org.junit.Before;
import org.junit.Test;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.*;

public class TestPatchTracker {
    DatabaseHandler db = null;
    PatchTrackerThread ptt = null;
    AtomicReference<String> ref = null;
    @Before
    public void setUp() throws Exception {
        db = new DatabaseHandler();
        ref = new AtomicReference<>();
        ptt = new PatchTrackerThread(0, 0, null,null, null, db, ref);
        db.read("data/5HeadTest.db");
    }

    @Test
    public void patchTest() {
        //These tests pass as of 5/07/2020, patch 10.9 was the last patch released.
        LocalDateTime date = LocalDateTime.parse("2020-04-28T19:00:00.000");
        String currPatch = "10.9";
        try {
            setUp();
            //No patches were released in the last second.
            assertFalse(PatchTrackerThread.hasPatchBeenReleasedWithin(0,1));
            //Patches release every two weeks, so patch has been released.
            assertTrue(PatchTrackerThread.hasPatchBeenReleasedWithin(100, 0));

            assertEquals(PatchTrackerThread.getPreviousPatchDate(), date);
            ptt.getAndUpdateCurrentPatch();
            assertEquals(ref.get(), currPatch);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
