import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class HeroTest {
    private Hero playerHero;
    private Hero aiHero;
    private Castle playerCastle;
    private Castle aiCastle;
    private GameMap map;
    private Unit unit;
    private Unit unitai;
    @BeforeEach
    public void setUp() {
        unit = new Spearman();
        unitai = new Spearman();
        map = new GameMap();
        playerHero = new Hero("R",1,1);
        playerCastle = new Castle("Игрок");
        aiHero = new Hero("D",6,6);
        aiCastle = new Castle("Компьютер");
        playerHero.addUnit(unit,map,"player");

    }//@BeforeEach означает, что этот метод будет выполняться перед каждым тестом в классе


    @Test

    public void teststudy() {
        playerHero.study();
        assertTrue(playerHero.study);


    }
    @Test

    public void testisHero() {

        assertTrue(playerHero.isHero());


    }
    @Test
    public void testgetspeed() {


        assertEquals(2,playerHero.get_speed());

    }
    @Test
    public void testgetDamage() {
        int oldHealth = playerHero.hp();
        playerHero.takeDamage(70);
        assertEquals(70,oldHealth - playerHero.hp());

    }
    @Test
    public void testattackUnit() {
        int oldHealth = unit.hp;
        playerHero.attackUnit(unit);
        assertEquals(40,oldHealth - unit.hp);

    }
    @Test
    public void testattackHero() {
        int oldHealth = aiHero.hp();
        playerHero.attackHero(aiHero);
        assertEquals(40,oldHealth - aiHero.hp());

    }
    @Test
    public void testmove() {
        playerHero.move(1,1,2,2,map);
        assertEquals('R',map.getMap(2,2));

    }
    @Test
    public void testhasarmy() {

        assertTrue(playerHero.hasArmy());

    }
    @Test
    public void testisAlive() {

        assertTrue(playerHero.isAlive());

    }
    @Test
    public void testsmert() {
        playerHero.smert(0,map);
        assertFalse(playerHero.hasArmy());

    }



}
