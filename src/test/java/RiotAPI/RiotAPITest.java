package RiotAPI;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

public class RiotAPITest {

    @Test
    public void RiotAPITest() {
        RiotAPI fa = new RiotAPI();
        RiotAPI.updateMapOfChamps("Anivia");
        assertEquals(RiotAPI.getSplashByName("Anivia"), "https://static.u.gg/assets/lol/riot_static/10.9.1/img/splash/Anivia_0.jpg");
        assertEquals(RiotAPI.urlFriendlyName("Anivia"), "anivia");
        assertEquals(RiotAPI.getIconByName("Anivia"), "https://static.u.gg/assets/lol/riot_static/10.9.1/img/champion/Anivia.png");
        assertEquals(RiotAPI.getIconByName("Jarvan IV"), "");
        assertEquals(RiotAPI.getSplashByName("Jarvan IV"), "");
        assertEquals(RiotAPI.urlFriendlyName("Jarvan IV"), "jarvaniv");
        assertEquals(RiotAPI.urlFriendlyName("Kai'sa"), "kaisa");
        assertEquals(RiotAPI.urlFriendlyName("ga 'ga0j9G"), "gaga0j9g");
        RiotAPI.updateMapOfChamps("Jarvan IV");
        RiotAPI.updateMapOfChamps("Kog'Maw");
        assertThrows(StringIndexOutOfBoundsException.class, () -> {RiotAPI.updateMapOfChamps("fakechamp");});
        assertEquals(RiotAPI.getIconByName("Jarvan IV"), "https://static.u.gg/assets/lol/riot_static/10.9.1/img/champion/JarvanIV.png");
        assertEquals(RiotAPI.getIconByName("Kog'Maw"), "https://static.u.gg/assets/lol/riot_static/10.9.1/img/champion/KogMaw.png");
        assertEquals(RiotAPI.getMapOfChampToWinPickBan().get("Kog'Maw").size(), 3);
    }
}
