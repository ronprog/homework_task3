import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GameMapTest {
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


    }//@BeforeEach означает, что этот метод будет выполняться перед каждым тестом в классе


    @Test

    public void testmapChange() {
        map.MapChange(1,1,'0');

        assertEquals('0', map.getMap(1,1));

    }
    @Test
    public void testGetmapwidth() {


        assertEquals(6, map.Map_width());

    }
    @Test
    public void testmapheight() {


        assertEquals(6, map.Map_height());

    }
    @Test
    public void testgetMap() {
        assertEquals('⚑',map.getMap(0,0));
    }
    @Test

    public void testload() {

        assertEquals(36,map.loadmap("⚑ᛘX◉XXRHHXXXXXHHX◉XX◉ᛘ☄◉XXXXHHX◉XXD⚐"));

    }
}
