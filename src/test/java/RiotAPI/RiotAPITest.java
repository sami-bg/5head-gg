package RiotAPI;

import org.junit.Assert;
import org.junit.Test;
import static org.junit.Assert.*;

public class RiotAPITest {

    @Test
    public void RiotAPITest() {
        RiotAPI fa = new RiotAPI();
        fa.updateMapOfChamps("Anivia");
        assertEquals(fa.getSplashByName("Anivia"), "https://static.u.gg/assets/lol/riot_static/10.9.1/img/splash/Anivia_0.jpg");
        assertEquals(fa.urlFriendlyName("Anivia"), "anivia");
        assertEquals(fa.getIconByName("Anivia"), "https://static.u.gg/assets/lol/riot_static/10.9.1/img/champion/Anivia.png");
        assertEquals(fa.getIconByName("Jarvan IV"), "");
        assertEquals(fa.getSplashByName("Jarvan IV"), "");
        assertEquals(fa.urlFriendlyName("Jarvan IV"), "jarvaniv");
        assertEquals(fa.urlFriendlyName("Kai'sa"), "kaisa");
        assertEquals(fa.urlFriendlyName("ga 'ga0j9G"), "gaga0j9g");
        fa.updateMapOfChamps("Jarvan IV");
        fa.updateMapOfChamps("Kog'Maw");
        Assert.assertThrows(StringIndexOutOfBoundsException.class, () -> {fa.updateMapOfChamps("fakechamp");});
        assertEquals(fa.getIconByName("Jarvan IV"), "https://static.u.gg/assets/lol/riot_static/10.9.1/img/champion/JarvanIV.png");
        assertEquals(fa.getIconByName("Kog'Maw"), "https://static.u.gg/assets/lol/riot_static/10.9.1/img/champion/KogMaw.png");
        assertEquals(fa.getMapOfChampToWinPickBan().get("Kog'Maw").size(), 3);
    }
}
