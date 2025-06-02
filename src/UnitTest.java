import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class UnitTest {
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
        playerHero = new Hero("R",0,0);
        playerCastle = new Castle("Игрок");
        aiHero = new Hero("D",6,6);
        aiCastle = new Castle("Компьютер");


    }


    @Test

    public void testmove() {
        unit.move(1,1,2,2,map);
        assertEquals('ᛘ', map.getMap(2,2));


    }
    @Test
    public void testchangeLocationx() {
        unit.change_location(3,3);
        assertEquals(3, unit.get_locationx());

    }
    @Test
    public void testchangeLocationy() {
        unit.change_location(3,3);

        assertEquals(3, unit.get_locationy());
    }
    @Test
    public void testGetDamage() {
        int oldHealth = unit.hp;
        unit.takedamage(10);

        assertEquals(10, oldHealth - unit.hp);
    }
    @Test
    public void testisHero() {

        assertFalse(unit.isHero());

    }
    @Test
    public void testGetSpeed() {

        assertEquals(2,unit.get_speed());

    }
    @Test
    public void testGetIcon() {

        assertEquals('ᛘ',unit.get_icon());

    }
    @Test
    public void testisAliv() {

        assertTrue(unit.isAlive());

    }
    @Test
    public void testattack() {
        int oldHealth = unitai.hp;
        unit.attack(unitai);
        assertEquals(10,oldHealth-unitai.hp);

    }

}
