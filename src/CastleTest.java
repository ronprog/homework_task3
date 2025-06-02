import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CastleTest {

    private Hero playerHero;
    private Hero aiHero;
    private Castle playerCastle;
    private Castle aiCastle;
    @BeforeEach
    public void setUp() {
        playerHero = new Hero("R",0,0);
        playerCastle = new Castle("Игрок");
        aiHero = new Hero("D",6,6);
        aiCastle = new Castle("Компьютер");

    }//@BeforeEach означает, что этот метод будет выполняться перед каждым тестом в классе

    @Test

    public void testGetDamage() {
        int oldhealth = playerCastle.GetOsad();
        playerCastle.GetDamage();
        assertEquals(-1, playerCastle.GetOsad()-oldhealth);

    }





    @Test
    public void testCollectGold() {
        int oldgold = playerCastle.Get_Gold();
        playerCastle.Collect_Gold(750);
        assertEquals(750, playerCastle.Get_Gold()-oldgold);

    }

}
