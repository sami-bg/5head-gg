package RiotAPI;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class ChampConstsTest {

    @Test
    public void ChampConstsTest(){
        ChampConsts cc = new ChampConsts();
        assertTrue(cc.getChampNames().contains("Zyra"));
        assertTrue(cc.getChampNames().contains("Aatrox"));
        assertTrue(cc.getChampNames().contains("Aurelion Sol"));
        assertTrue(cc.getChampNames().contains("Heimerdinger"));
        assertTrue(!cc.getChampNames().contains("Ao Shin"));
        assertTrue(!cc.getChampNames().contains("KaiSa"));
        assertTrue(!cc.getChampNames().contains("Kai'Sa"));
        assertTrue(cc.getChampNames().contains("Kai'sa"));

    }
}
