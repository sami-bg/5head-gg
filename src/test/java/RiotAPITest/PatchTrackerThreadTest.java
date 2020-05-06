package RiotAPITest;

import Main.java.RiotAPI.PatchTrackerThread;
import org.junit.Test;

public class PatchTrackerThreadTest {

  private static final PatchTrackerThread patch = new PatchTrackerThread();

  @Test
  public void testPreviousPatchDate() {
    System.out.println(patch.getPreviousPatchDate());
    System.out.println(patch.hasPatchBeenReleasedWithin(8));
    System.out.println(patch.hasPatchBeenReleasedWithin(7));
    System.out.println(patch.hasPatchBeenReleasedWithin(6));
    System.out.println(patch.getCurrentPatch());
  }
}
