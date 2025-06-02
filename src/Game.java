import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import java.io.*;
import java.util.*;
import java.lang.System.Logger;
import java.lang.System;
import java.util.logging.FileHandler;
import java.util.logging.SimpleFormatter;

public class Game {
    public static int perk_chose(Hero playerHero){
        Scanner scanner = new Scanner(System.in);
        System.out.println("Сделайте выбор перка:");
        for (int i = 0; i < playerHero.getArmy().size(); i++) {
            System.out.println((i + 1) + " " + playerHero.getArmy().get(i));
        }
        System.out.println(100 + " " + "Герой!");
        if (playerHero.isAlive()||playerHero.getArmy().size()>0) {
            int perk = scanner.nextInt();

            return perk;
        }
        return -10;
    }

    public static void botAttackAndMove(Hero botHero, Hero playerHero, Castle botCastle, GameMap map) {
        boolean hasAliveUnits = botHero.getArmy().stream().anyMatch(Unit::isAlive);

        if (hasAliveUnits) {
            for (int i = 0; i < botHero.getArmy().size(); i++) {
                Unit botUnit = botHero.getArmy().get(i);
                if (botUnit.isAlive()) {
                    int range = botUnit.get_range();
                    int px = botUnit.get_locationx();
                    int py = botUnit.get_locationy();

                    System.out.println("Бот " + botUnit.name + " на позиции (" + px + ", " + py + ")");

                    Unit targetU = findClosestEnemyInRangeU(px, py, range, playerHero);
                    Unit targetK = findClosestEnemyInRangeU(px, py, range, playerHero);
                    if (targetK != null) {
                        System.out.println("Бот выбрал цель: " + targetK.name);
                        botUnit.attack(targetK);
                        if (!targetK.isAlive()) {
                            System.out.println("Бот уничтожил " + targetK.name);
                            if (targetK.isHero()) {
                                playerHero.smert(-1, map); // Обработка смерти героя
                            } else {
                                playerHero.smert(playerHero.getArmy().indexOf(targetK), map); // Обработка смерти юнита
                            }
                            botCastle.Collect_Gold(750);
                        }
                    } else {
                        System.out.println("Врагов в радиусе атаки нет.");
                        moveTowardsCastle(botUnit, map,botCastle);
                    }
                }
            }
        } else {
            Hero botHeroUnit = botHero;
            if (botHeroUnit.isAlive()) {
                int range = 2;
                int px = botHeroUnit.get_locationx();
                int py = botHeroUnit.get_locationy();

                System.out.println("Герой бота на позиции (" + px + ", " + py + ")");

                Unit targetU = findClosestEnemyInRangeU(px, py, range, playerHero);
                Hero targetK = findClosestEnemyInRangeK(px, py, range, playerHero);
                if (targetU != null) {
                    System.out.println("Бот выбрал цель: " + targetU.name);
                    botHeroUnit.attackUnit(targetU);
                    if (!targetU.isAlive()) {
                        System.out.println("Бот уничтожил " + targetU.name);
                        if (targetU.isHero()) {
                            playerHero.smert(-1, map);
                        } else {
                            playerHero.smert(playerHero.getArmy().indexOf(targetU), map);
                        }
                        botCastle.Collect_Gold(750);
                    }
                }
                else if (targetK != null) {
                    System.out.println("Бот выбрал цель: Герой" );
                    botHeroUnit.attackHero(targetK);
                    if (playerHero.hp()==0) {
                        System.out.println("Бот уничтожил Герой" );
                        if (targetK.isHero()) {
                            playerHero.death( map);
                        }
                        botCastle.Collect_Gold(750);
                    }
                }
                else {
                    System.out.println("Врагов в радиусе атаки нет.");
                    moveTowardsCastleHero(botHeroUnit, map,botCastle);
                }
            }
        }
    }
    // Метод для поиска ближайшего врага в радиусе атаки
    private static Unit findClosestEnemyInRangeU(int px, int py, int range, Hero playerHero) {
        Unit targetU = null;
        Hero targetk = null;
        double minDistance = Double.MAX_VALUE;

        // Проверяем вражеских юнитов
        for (Unit enemyUnit : playerHero.getArmy()) {
            if (enemyUnit.isAlive()) {
                int ax = enemyUnit.get_locationx();
                int ay = enemyUnit.get_locationy();
                double distance = Math.abs(px - ax) + Math.abs(py - ay); // Манхэттенское расстояние

                if (distance <= range ) {
                    targetU = enemyUnit;
                    minDistance = distance;
                }
            }
        }

        // Проверяем вражеского героя


        return targetU;

    }
    private static Hero findClosestEnemyInRangeK(int px, int py, int range, Hero playerHero) {

        Hero targetk = null;
        double minDistance = Double.MAX_VALUE;



        // Проверяем вражеского героя
        Hero enemyHero = playerHero; // Предполагаем, что герой - первый в армии
        if (enemyHero.isAlive()) {
            int ax = enemyHero.get_locationx();
            int ay = enemyHero.get_locationy();
            double distance = Math.abs(px - ax) + Math.abs(py - ay); // Манхэттенское расстояние

            if (distance <= range && distance < minDistance) {
                targetk = enemyHero;
            }
        }
        return targetk;

    }
    // Метод для движения в сторону замка (0, 0)
    private static void moveTowardsCastle(Unit unit, GameMap map,Castle aiCastle) {
        int x = unit.get_locationx();
        int y = unit.get_locationy();
        int speed = unit.get_speed();

        // Вычисляем направление движения
        int dx = Integer.compare(0, x); // Направление по X (влево или вправо)
        int dy = Integer.compare(0, y); // Направление по Y (вверх или вниз)

        // Пытаемся двигаться в сторону замка
        for (int step = 1; step <= speed; step++) {
            int newX = x + dx * step;
            int newY = y + dy * step;

            // Проверяем, можно ли переместиться на новую клетку
            if (newX >= 0 && newX < map.Map_width() && newY >= 0 && newY < map.Map_height()) {
                char cell = map.getMap(newX, newY);
                if (cell == 'H' || cell == '◉' || cell == '☠') { // Проверяем допустимые клетки
                    System.out.println("Бот двигается на (" + newX + ", " + newY + ")");
                    unit.move(x, y, newX, newY, map);
                    if (cell == '☠'){
                        aiCastle.Collect_Gold(500);
                    }
                    break;
                } else {
                    System.out.println("Клетка (" + newX + ", " + newY + ") недоступна.");
                }
            } else {
                System.out.println("Клетка (" + newX + ", " + newY + ") за пределами карты.");
            }
        }
    }
    private static void moveTowardsCastleHero(Hero unit, GameMap map,Castle aiCastle) {
        int x = unit.get_locationx();
        int y = unit.get_locationy();
        int speed = unit.get_speed();

        // Вычисляем направление движения
        int dx = Integer.compare(0, x); // Направление по X (влево или вправо)
        int dy = Integer.compare(0, y); // Направление по Y (вверх или вниз)

        // Пытаемся двигаться в сторону замка
        for (int step = 1; step <= speed; step++) {
            int newX = x + dx * step;
            int newY = y + dy * step;

            // Проверяем, можно ли переместиться на новую клетку
            if (newX >= 0 && newX < map.Map_width() && newY >= 0 && newY < map.Map_height()) {
                char cell = map.getMap(newX, newY);
                if (cell == 'H' || cell == '◉' || cell == '☠') { // Проверяем допустимые клетки
                    System.out.println("Бот двигается на (" + newX + ", " + newY + ")");
                    unit.move(x, y, newX, newY, map);
                    if (cell == '☠'){
                        aiCastle.Collect_Gold(500);
                    }
                    break;

                } else {
                    System.out.println("Клетка (" + newX + ", " + newY + ") недоступна.");
                }
            } else {
                System.out.println("Клетка (" + newX + ", " + newY + ") за пределами карты.");
            }
        }
    }
    public static void atacka(Hero playerHero, Hero aiHero, Castle playerCastle, Castle aiCastle, int perk, GameMap map) {
        int k = 0;
        int range = playerHero.getArmy().get(perk - 1).get_range(); // Радиус атаки выбранного юнита
        Set<Integer> processedEnemies = new HashSet<>();

        // Координаты выбранного юнита игрока
        int unitX = playerHero.getArmy().get(perk - 1).get_locationx();
        int unitY = playerHero.getArmy().get(perk - 1).get_locationy();

        // Проверка, находится ли вражеский герой в радиусе атаки
        int aiHeroX = aiHero.get_locationx();
        int aiHeroY = aiHero.get_locationy();
        boolean canAttackHero = Math.abs(unitX - aiHeroX) <= range && Math.abs(unitY - aiHeroY) <= range;

        // Проверка, находится ли замок в радиусе атаки
        int castleX = 5;
        int castleY = 5;
        boolean canAttackCastle = Math.abs(unitX - castleX) <= range && Math.abs(unitY - castleY) <= range;

        // Вывод списка вражеских юнитов в радиусе атаки
        for (int j = 0; j < aiHero.getArmy().size(); j++) {
            int enemyUnitX = aiHero.getArmy().get(j).get_locationx();
            int enemyUnitY = aiHero.getArmy().get(j).get_locationy();

            if (Math.abs(unitX - enemyUnitX) <= range && Math.abs(unitY - enemyUnitY) <= range && !processedEnemies.contains(j)) {
                k++;
                processedEnemies.add(j);
                System.out.println(k + " ) " + aiHero.getArmy().get(j).name + " с " + aiHero.getArmy().get(j).hp + " ХП");
            }
        }

        // Если вражеский герой в радиусе атаки, добавляем его в список целей
        if (canAttackHero) {
            System.out.println((k + 1) + " ) "  + " (герой) с " + aiHero.hp() + " ХП");
        }

        // Если замок в радиусе атаки, добавляем его в список целей
        if (canAttackCastle) {
            System.out.println((k + 2) + " ) Замок с " + aiCastle.GetOsad() + " ОСАД");
        }

        if (k > 0 || canAttackHero || canAttackCastle) {
            Scanner scanner = new Scanner(System.in);
            System.out.print("Выберите цель для атаки (введите номер): ");
            int target = scanner.nextInt();

            if (canAttackHero && target == k + 1) {
                // Атака на вражеского героя
                aiHero.takeDamage(playerHero.getArmy().get(perk).attack);
                if (!aiHero.isAlive()) {
                    System.out.println("Герой "  + " уничтожен!");
                    aiHero.death(map);
                    playerCastle.Collect_Gold(1500); // Награда за убийство героя
                }
            } else if (canAttackCastle && target == k + 2) {
                // Атака на замок
                aiCastle.GetDamage();
                if (aiCastle.GetOsad()==0) {
                    System.out.println("Замок уничтожен!");
                    // Дополнительные действия при уничтожении замка
                }
            } else if (target <= k) {
                // Атака на юнита
                k = 0;
                for (int j = 0; j < aiHero.getArmy().size(); j++) {
                    int enemyUnitX = aiHero.getArmy().get(j).get_locationx();
                    int enemyUnitY = aiHero.getArmy().get(j).get_locationy();

                    if (Math.abs(unitX - enemyUnitX) <= range && Math.abs(unitY - enemyUnitY) <= range) {
                        k++;
                        if (k == target) {
                            playerHero.getArmy().get(perk - 1).attack(aiHero.getArmy().get(j));
                            if (!aiHero.getArmy().get(j).isAlive()) {
                                System.out.println("Юнит " + aiHero.getArmy().get(j).name + " уничтожен!");
                                aiHero.smert(j, map); // Удаление юнита из армии врага
                                playerCastle.Collect_Gold(750); // Награда за убийство юнита
                            }
                        }
                    }
                }
            } else {
                System.out.println("Неверный выбор цели.");
            }
        } else {
            System.out.println("Врагов по близости нет...");
        }
        System.out.println("");
    }
    public static void atackaHero(Hero playerHero, Hero aiHero, Castle playerCastle, Castle aiCastle, int perk, GameMap map) {
        int k = 0;
        int range = 2; // Радиус атаки
        Set<Integer> processedEnemies = new HashSet<>();

        // Координаты игрока
        int playerX = playerHero.get_locationx();
        int playerY = playerHero.get_locationy();

        // Проверка, находится ли вражеский герой в радиусе атаки
        int aiHeroX = aiHero.get_locationx();
        int aiHeroY = aiHero.get_locationy();
        boolean canAttackHero = Math.abs(playerX - aiHeroX) <= range && Math.abs(playerY - aiHeroY) <= range;

        // Проверка, находится ли замок в радиусе атаки
        int castleX = 5;
        int castleY = 5;
        boolean canAttackCastle = Math.abs(playerX - castleX) <= range && Math.abs(playerY - castleY) <= range;

        // Вывод списка вражеских юнитов в радиусе атаки
        for (int j = 0; j < aiHero.getArmy().size(); j++) {
            int unitX = aiHero.getArmy().get(j).get_locationx();
            int unitY = aiHero.getArmy().get(j).get_locationy();

            if (Math.abs(playerX - unitX) <= range && Math.abs(playerY - unitY) <= range && !processedEnemies.contains(j)) {
                k++;
                processedEnemies.add(j);
                System.out.println(k + " ) " + aiHero.getArmy().get(j).name + " с " + aiHero.getArmy().get(j).hp + " ХП");
            }
        }

        // Если вражеский герой в радиусе атаки, добавляем его в список целей
        if (canAttackHero) {
            System.out.println((k + 1) + " ) "  + " (герой) с " + aiHero.hp() + " ХП");
        }

        // Если замок в радиусе атаки, добавляем его в список целей
        if (canAttackCastle) {
            System.out.println((k + 2) + " ) Замок с " + aiCastle.GetOsad() + " ОСАД");
        }

        if (k > 0 || canAttackHero || canAttackCastle) {
            Scanner scanner = new Scanner(System.in);
            System.out.print("Выберите цель для атаки (введите номер): ");
            int target = scanner.nextInt();

            if (canAttackHero && target == k + 1) {
                // Атака на вражеского героя
                playerHero.attackHero(aiHero);
                if (!aiHero.isAlive()) {
                    System.out.println("Герой " +  " уничтожен!");
                    playerCastle.Collect_Gold(1500); // Награда за убийство героя
                    aiHero.death( map);
                }
            } else if (canAttackCastle && target == k + 2) {
                // Атака на замок
                aiCastle.GetDamage();
                if (aiCastle.GetOsad()==0) {
                    System.out.println("Замок уничтожен!");
                    System.out.println("Ты победил!");
                    System.exit(0);
                    // Дополнительные действия при уничтожении замка
                }
            } else if (target <= k) {
                // Атака на юнита
                k = 0;
                for (int j = 0; j < aiHero.getArmy().size(); j++) {
                    int unitX = aiHero.getArmy().get(j).get_locationx();
                    int unitY = aiHero.getArmy().get(j).get_locationy();

                    if (Math.abs(playerX - unitX) <= range && Math.abs(playerY - unitY) <= range) {
                        k++;
                        if (k == target) {
                            playerHero.attackUnit(aiHero.getArmy().get(j));
                            if (!aiHero.getArmy().get(j).isAlive()) {
                                System.out.println("Юнит " + aiHero.getArmy().get(j).name + " уничтожен!");
                                aiHero.smert(j,map); // Удаление юнита из армии врага
                                playerCastle.Collect_Gold(750); // Награда за убийство юнита
                            }
                        }
                    }
                }
            } else {
                System.out.println("Неверный выбор цели.");
            }
        } else {
            System.out.println("Врагов по близости нет...");
        }
        System.out.println("");
    }
    @Override
    public String toString() {
        return super.toString();
    }
    public static void save(String records,String mapfile, String pHero, String iHero,String pCastle,String iCastle,String pUnit,String iUnit, Hero playerHero, Hero aiHero, Castle playerCastle, Castle aiCastle, GameMap map) throws IOException {
        FileWriter mapf = new FileWriter(mapfile)  ;
        for (int i = 0; i<6;i++){
            for (int j = 0; j<6;j++){
                mapf.write(map.getMap(j,i));

            }
        }
        FileWriter pHer = new FileWriter(pHero);
        pHer.write(playerHero.hp() + " " + playerHero.study + " " + playerHero.get_locationx() + " " + playerHero.get_locationy() + " " + playerHero.getArmy().size() );
        pHer.close();
        FileWriter iHer = new FileWriter(iHero);
        iHer.write(aiHero.hp() + " " + aiHero.study + " " + aiHero.get_locationx() + " " + aiHero.get_locationy() + " " + aiHero.getArmy().size());
        iHer.close();
        FileWriter pCastl = new FileWriter(pCastle);
        pCastl.write(playerCastle.getBuildingCount() + " " + playerCastle.getSoldiersCount() + " " +playerCastle.Get_Gold());
        pCastl.close();
        FileWriter iCastl = new FileWriter(iCastle);
        iCastl.write(aiCastle.getBuildingCount() + " " + aiCastle.getSoldiersCount() + " " +aiCastle.Get_Gold());
        iCastl.close();
        FileWriter pUni = new FileWriter(pUnit);
        for(int i  =0 ; i<playerHero.getArmy().size();i++){
            Unit p = playerHero.getArmy().get(i);
            pUni.write(p.name + " " + p.hp  + " " + p.get_locationx() + " " + p.get_locationy() + "\n");
        }
        pUni.close();
        FileWriter iUni = new FileWriter(iUnit);
        for(int i  =0 ; i<aiHero.getArmy().size();i++){
            Unit p = aiHero.getArmy().get(i);
            iUni.write(p.name + " " + p.hp + " " + p.get_locationx() + " " + p.get_locationy()+ "\n");
        }
        iUni.close();
        mapf.close();
        File rc = new File("records.txt");
        BufferedReader pC = new BufferedReader(new FileReader(rc));
        int a = Integer.parseInt(pC.readLine());

        pC.close();
        FileWriter rec = new FileWriter(records);

        Integer ff = playerCastle.Get_Gold();
        if (a<playerCastle.Get_Gold()) {
            rec.write(ff.toString());
            System.out.println(playerCastle.Get_Gold() + "Вы обновили рекорд золота!");
            ObjectMapper jsonMapper = new ObjectMapper();
            jsonMapper.writeValue(new File("gamestats.json"),ff.toString() );

            XmlMapper xmlMapper = new XmlMapper();
            xmlMapper.writeValue(new File("gamestats.xml"), ff.toString());
            String  statsFromJson = jsonMapper.readValue(new File("gamestats.json"), String.class);
            System.out.println(statsFromJson);
        }

    }
    //Здоровье героя Здоровье ии Золото героя Золото ии Постройки героя Постройки ии Бойцы героя Бойцы ии Здоровье каждого бойца героя Здоровье каждого бойца ии + координаты
    private static final Logger logger = System.getLogger("Main");

    public static void main(String[] args) throws Exception{



            // Настройка JUL
        java.util.logging.Logger julLogger = java.util.logging.Logger.getLogger("");
        FileHandler fileHandler = new FileHandler("app.log");
        fileHandler.setFormatter(new SimpleFormatter());
        julLogger.addHandler(fileHandler);
        julLogger.setUseParentHandlers(false); // Отключить вывод в консоль


        logger.log(Logger.Level.INFO, "Сообщение записано в файл");

        Logger logger = (Logger) System.getLogger(Game.class.getName());
        logger.log(Logger.Level.INFO,"игра началась");

        Scanner scanner = new Scanner(System.in);
        System.out.println("Выберите вариант загрузки \n(1) Новая игра \n(2) Загрузить сохранение");

        int choice = scanner.nextInt();
        if (choice == 1) {
            GameMap map = new GameMap();
            Castle playerCastle = new Castle("Игрок");
            Castle aiCastle = new Castle("Компьютер");
            Hero playerHero = new Hero("Рыцарь", 0, 1);
            map.MapChange(0, 1, 'R');
            playerHero.addUnit(new Spearman(),map,"player");

            playerHero.getArmy().get(0).change_location(1, 0);
            Hero aiHero = new Hero("Тёмный лорд", 4, 5);
            map.MapChange(4, 5, 'D');
            aiHero.addUnit(new Spearman(),map,"ai");


            aiCastle.Collect_Gold(2100);
            map.printMap();

            int map_height = map.Map_height();
            int map_width = map.Map_width();

            while (true) {
                aiCastle.buildAi();
                aiCastle.recruitAi(aiHero,map);
                botAttackAndMove(aiHero,playerHero,aiCastle,map);
                System.out.println("\nВаш ход!\n");
                System.out.println("Ваше золото: " + playerCastle.Get_Gold());
                System.out.println("Ваш ход: \n(1) Двигаться \n(2) Атаковать \n(3) Построить новое здание \n(4) Нанять бойца\n(5) Поиграть в кабаке(казино)\n(6) Отправить героя на лесопилку учиться рубить препятствия (300 ◉ )\n(7) Рубить препятствия\n(8) Сохраниться");
                choice = scanner.nextInt();

                if (choice == 1) {
                    int perk = perk_chose(playerHero);
                    if (perk == 100) {
                        System.out.println("Сделайте выбор клетки для перемещения:");

                        int x = playerHero.get_locationx();
                        int y = playerHero.get_locationy();
                        System.out.println("Координаты героя " + x + " " + y);
                        int trans = playerHero.get_speed();
                        int k = 0;
                        for (int stp = 1; stp <= trans; stp++) {
                            int[][] directions = {{stp, 0}, {0, stp}, {-stp, 0}, {0, -stp}, {stp / 2, stp / 2}, {-stp / 2, -stp / 2}, {stp / 2, -stp / 2}, {-stp / 2, stp / 2}};

                            for (int[] dir : directions) {
                                boolean blocked = false;
                                for (int step = 1; step <= stp; step++) {
                                    int newX = x + dir[0] * step / stp, newY = y + dir[1] * step / stp;
                                    if (newX < 0 || newX >= map_width || newY < 0 || newY >= map_height || map.getMap(newX, newY) == 'X') {
                                        blocked = true;
                                        break;
                                    }
                                }
                                if (!blocked) {
                                    int newX = x + dir[0], newY = y + dir[1];
                                    char cell = map.getMap(newX, newY);
                                    if (cell == '#' || cell == 'H' || cell == '◉' || cell == 'O' || cell == '☠'||cell == 'Ø') {
                                        System.out.println(++k + ")" + newX + " " + newY);
                                    }
                                }
                            }
                        }

                        k = 0;
                        int chos = scanner.nextInt();

                        for (int stp = 1; stp <= trans; stp++) {
                            int[][] directions = {{stp, 0}, {0, stp}, {-stp, 0}, {0, -stp}, {stp / 2, stp / 2}, {-stp / 2, -stp / 2}, {stp / 2, -stp / 2}, {-stp / 2, stp / 2}};

                            for (int[] dir : directions) {
                                boolean blocked = false;
                                for (int step = 1; step <= stp; step++) {
                                    int newX = x + dir[0] * step / stp, newY = y + dir[1] * step / stp;
                                    if (newX < 0 || newX >= map_width || newY < 0 || newY >= map_height || map.getMap(newX, newY) == 'X') {
                                        blocked = true;
                                        break;
                                    }
                                }
                                if (!blocked) {
                                    int newX = x + dir[0], newY = y + dir[1];
                                    char cell = map.getMap(newX, newY);
                                    if (cell == '#' || cell == 'H' || cell == '◉' || cell == 'O' || cell == '☠' || cell == 'Ø') {
                                        if (++k == chos) {
                                            if (cell == '◉') {
                                                playerCastle.Collect_Gold(150);
                                            } else if (cell == '☠') {
                                                playerCastle.Collect_Gold(500);
                                            }
                                            else if (cell == 'Ø'){
                                                System.out.println("Вы наступили в токсичную лужу и ваш боец теряет 10hp");
                                                playerHero.takeDamage(10);
                                            }
                                            playerHero.move(x, y, newX, newY, map);
                                        }
                                    }
                                }
                            }
                        }


                        System.out.println("Хотите атаковать?\n1) Да\n2) Нет");
                        int question = scanner.nextInt();
                        if (question == 1) {
                            atackaHero(playerHero, aiHero, playerCastle,aiCastle, perk, map);
                        }
                    }
                    else if (perk!=100 && perk!=-100) {


                        System.out.println("Сделайте выбор клетки для перемещения:");
                        int x = playerHero.getArmy().get(perk - 1).get_locationx();
                        int y = playerHero.getArmy().get(perk - 1).get_locationy();
                        System.out.println("Координаты перка " + x + " " + y);
                        int trans = playerHero.getArmy().get(perk - 1).get_speed();
                        int k = 0;
                        for (int stp = 1; stp <= trans; stp++) {
                            int[][] directions = {{stp, 0}, {0, stp}, {-stp, 0}, {0, -stp}, {stp / 2, stp / 2}, {-stp / 2, -stp / 2}, {stp / 2, -stp / 2}, {-stp / 2, stp / 2}};

                            for (int[] dir : directions) {
                                boolean blocked = false;
                                for (int step = 1; step <= stp; step++) {
                                    int newX = x + dir[0] * step / stp, newY = y + dir[1] * step / stp;
                                    if (newX < 0 || newX >= map_width || newY < 0 || newY >= map_height || map.getMap(newX, newY) == 'X') {
                                        blocked = true;
                                        break;
                                    }
                                }
                                if (!blocked) {
                                    int newX = x + dir[0], newY = y + dir[1];
                                    char cell = map.getMap(newX, newY);
                                    if (cell == '#' || cell == 'H' || cell == '◉' || cell == 'O' || cell == '☠' || cell == 'Ø') {
                                        System.out.println(++k + ")" + newX + " " + newY);
                                    }
                                }
                            }
                        }

                        k = 0;
                        int chos = scanner.nextInt();

                        for (int stp = 1; stp <= trans; stp++) {
                            int[][] directions = {{stp, 0}, {0, stp}, {-stp, 0}, {0, -stp}, {stp / 2, stp / 2}, {-stp / 2, -stp / 2}, {stp / 2, -stp / 2}, {-stp / 2, stp / 2}};

                            for (int[] dir : directions) {
                                boolean blocked = false;
                                for (int step = 1; step <= stp; step++) {
                                    int newX = x + dir[0] * step / stp, newY = y + dir[1] * step / stp;
                                    if (newX < 0 || newX >= map_width || newY < 0 || newY >= map_height || map.getMap(newX, newY) == 'X') {
                                        blocked = true;
                                        break;
                                    }
                                }
                                if (!blocked) {
                                    int newX = x + dir[0], newY = y + dir[1];
                                    char cell = map.getMap(newX, newY);
                                    if (cell == '#' || cell == 'H' || cell == '◉' || cell == 'O' || cell == '☠' || cell == 'Ø') {
                                        if (++k == chos) {
                                            if (cell == '◉') {
                                                playerCastle.Collect_Gold(150);
                                            } else if (cell == '☠') {
                                                playerCastle.Collect_Gold(500);
                                            }
                                            else if (cell == 'Ø'){
                                                System.out.println("Вы наступили в токсичную лужу и ваш боец теряет 10hp");
                                                playerHero.getArmy().get(perk-1).hp-=10;
                                            }
                                            playerHero.getArmy().get(perk - 1).move(x, y, newX, newY, map);
                                        }
                                    }
                                }
                            }
                        }


                        System.out.println("Хотите атаковать?\n1) Да\n2) Нет");
                        int question = scanner.nextInt();
                        if (question == 1) {
                            atacka(playerHero, aiHero, playerCastle,aiCastle, perk, map);
                        }
                    }
                    map.printMap();
                    save("records.txt","map.txt","pHero.txt","iHero.txt","pCastle.txt","iCastle.txt","pUnit.txt","iUnit.txt",playerHero,aiHero,playerCastle,aiCastle,map);

                }
                else if (choice == 2) {
                    int perk = perk_chose(playerHero);
                    if(perk==100){atackaHero(playerHero,aiHero,playerCastle,aiCastle,perk,map);}
                    else if (perk==-100){return;}
                    else {atacka(playerHero,aiHero,playerCastle,aiCastle,perk,map);}

                }
                else if (choice == 3) {
                    playerCastle.build();
                }
                else if (choice == 4) {
                    playerCastle.recruit(playerHero,map);
                }
                else if (choice == 5) {
                    playerCastle.Casino();
                }
                else if (choice == 6) {
                    playerCastle.Lesopilka(playerHero);
                }
                else if (choice == 7) {



                    if(playerHero.study == true){
                        // Размеры карты
                        int mapWidth = map.Map_width();
                        int mapHeight = map.Map_height();

                        // Список доступных для вырубки клеток
                        List<int[]> availableCells = new ArrayList<>();

                        // Проверяем все 8 соседних клеток
                        for (int i = -1; i <= 1; i++) {
                            for (int j = -1; j <= 1; j++) {
                                if (i == 0 && j == 0) continue; // Пропускаем саму клетку героя

                                int neighborX = playerHero.get_locationx() + i;
                                int neighborY = playerHero.get_locationy() + j;

                                // Проверяем, что клетка существует на карте
                                if (neighborX >= 0 && neighborX < mapWidth && neighborY >= 0 && neighborY < mapHeight) {
                                    // Проверяем, можно ли вырубить клетку
                                    if (map.getMap(neighborX, neighborY) == 'X') {
                                        // Добавляем клетку в список доступных
                                        availableCells.add(new int[]{neighborX, neighborY});
                                    }
                                }
                            }
                        }

                        // Если есть доступные клетки для вырубки
                        if (!availableCells.isEmpty()) {
                            // Выводим список доступных клеток
                            System.out.println("Доступные клетки для вырубки:");
                            for (int idx = 0; idx < availableCells.size(); idx++) {
                                int[] cell = availableCells.get(idx);
                                System.out.println((idx + 1) + ") Клетка (" + cell[0] + ", " + cell[1] + ")");
                            }

                            // Даем игроку выбрать клетку

                            System.out.print("Выберите клетку для вырубки (введите номер): ");
                            choice = scanner.nextInt();

                            // Проверяем, что выбор корректен
                            if (choice > 0 && choice <= availableCells.size()) {
                                int[] selectedCell = availableCells.get(choice - 1);
                                int selectedX = selectedCell[0];
                                int selectedY = selectedCell[1];

                                // Вырубаем выбранную клетку
                                map.MapChange(selectedX, selectedY, 'Ø');
                                System.out.println("Клетка (" + selectedX + ", " + selectedY + ") вырублена!");
                            } else {
                                System.out.println("Неверный выбор.");
                            }
                        } else {
                            System.out.println("Нет доступных клеток для вырубки.");
                        }



                    }
                    else {System.out.println("Иди учись!");}
                }
                else if (choice == 8) {
                    save("records.txt","map.txt","pHero.txt","iHero.txt","pCastle.txt","iCastle.txt","pUnit.txt","iUnit.txt",playerHero,aiHero,playerCastle,aiCastle,map);
                    System.out.println("Сохранение прошло успешно!");
                }
                else if (choice ==9){

                    // Main.java

                            Hotel hotel = new Hotel();
                            Cafe cafe = new Cafe();

                            // Открываем заведения
                            hotel.open();
                            cafe.open();

                            // Создаем 10 NPC и 1 героя
                            for (int i = 1; i <= 10; i++) {
                                new NPC("NPC-" + i, hotel, cafe, false).start();
                            }
                            new NPC("Герой", hotel, cafe, true).start();

                            // Через 10 сек закрываем заведения
                            Thread.sleep(10000);
                            hotel.close();
                            cafe.close();

                }
            }}
        else {


            File mapf = new File("map.txt");
            BufferedReader br = new BufferedReader(new FileReader(mapf));
            Map<String, Unit> Soldiers = new HashMap<>();


            Soldiers.put("Копейщик", new Spearman());
            Soldiers.put("Арбалетчик", new Archer());
            Soldiers.put("Мечник", new Swordman());
            Soldiers.put("Кавалерист", new Cavalery());
            Soldiers.put("Паладин", new Paladin());
            GameMap map = new GameMap();
            map.loadmap(br.readLine());
            map.printMap();
            File pHero = new File("pHero.txt");
            BufferedReader pH = new BufferedReader(new FileReader(pHero));
            File iHero = new File("iHero.txt");
            BufferedReader iH = new BufferedReader(new FileReader(iHero));
            String[] pHero1 = pH.readLine().split(" ");
            Hero playerHero = new Hero("R",Integer.parseInt(pHero1[2]), Integer.parseInt(pHero1[3]));
            String[] iHero1 = iH.readLine().split(" ");
            Hero aiHero = new Hero("D",Integer.parseInt(iHero1[2]), Integer.parseInt(iHero1[3]));
            playerHero.setHp(Integer.parseInt(pHero1[0]));
            aiHero.setHp(Integer.parseInt(iHero1[0]));
            if (Boolean.parseBoolean(pHero1[1])) {playerHero.study();}

            File pUni = new File("pUnit.txt");
            File iUni = new File("iUnit.txt");
            BufferedReader pU = new BufferedReader(new FileReader(pUni));
            BufferedReader iU = new BufferedReader(new FileReader(iUni));
            for(int i = 0;i<Integer.parseInt(pHero1[4]);i++){
                String[] unit = pU.readLine().split(" ");
                Unit uni  = Soldiers.get(unit[0]);
                uni.hp = Integer.parseInt(unit[1]);
                uni.x = Integer.parseInt(unit[2]);
                uni.y = Integer.parseInt(unit[3]);
                playerHero.addUnit(uni,map,"player");
                System.out.println("d");
            }
            for(int i = 0;i<Integer.parseInt(iHero1[4]);i++){
                String[] unit = iU.readLine().split(" ");
                Unit uni  = Soldiers.get(unit[0]);
                uni.hp = Integer.parseInt(unit[1]);
                uni.x = Integer.parseInt(unit[2]);
                uni.y = Integer.parseInt(unit[3]);
                aiHero.
                        addUnit(uni,map,"ai");
            }
            File pCas = new File("pCastle.txt");
            File iCas = new File("iCastle.txt");
            BufferedReader pC = new BufferedReader(new FileReader(pCas));
            BufferedReader iC = new BufferedReader(new FileReader(iCas));
            String[] pCas1 = pC.readLine().split(" ");
            String[] iCas1 = iC.readLine().split(" ");
            Castle playerCastle = new Castle("Игрок");
            playerCastle.setGold(Integer.parseInt(pCas1[2]));
            playerCastle.setBuildings(Integer.parseInt(pCas1[0]));
            Castle aiCastle = new Castle("Компьютер");
            aiCastle.setGold(Integer.parseInt(iCas1[2]));
            aiCastle.setBuildings(Integer.parseInt(iCas1[0]));

            int map_height = map.Map_height();
            int map_width = map.Map_width();

            while (true) {
                aiCastle.buildAi();
                aiCastle.recruitAi(aiHero,map);
                botAttackAndMove(aiHero,playerHero,aiCastle,map);
                System.out.println("\nВаш ход!\n");
                System.out.println("Ваше золото: " + playerCastle.Get_Gold());
                System.out.println("Ваш ход: \n(1) Двигаться \n(2) Атаковать \n(3) Построить новое здание \n(4) Нанять бойца\n(5) Поиграть в кабаке(казино)\n(6) Отправить героя на лесопилку учиться рубить препятствия (300 ◉ )\n(7) Рубить препятствия\n(8) Сохраниться");
                choice = scanner.nextInt();

                if (choice == 1) {
                    int perk = perk_chose(playerHero);
                    if (perk == 100) {
                        System.out.println("Сделайте выбор клетки для перемещения:");

                        int x = playerHero.get_locationx();
                        int y = playerHero.get_locationy();
                        System.out.println("Координаты героя " + x + " " + y);
                        int trans = playerHero.get_speed();
                        int k = 0;
                        for (int stp = 1; stp <= trans; stp++) {
                            int[][] directions = {{stp, 0}, {0, stp}, {-stp, 0}, {0, -stp}, {stp / 2, stp / 2}, {-stp / 2, -stp / 2}, {stp / 2, -stp / 2}, {-stp / 2, stp / 2}};

                            for (int[] dir : directions) {
                                boolean blocked = false;
                                for (int step = 1; step <= stp; step++) {
                                    int newX = x + dir[0] * step / stp, newY = y + dir[1] * step / stp;
                                    if (newX < 0 || newX >= map_width || newY < 0 || newY >= map_height || map.getMap(newX, newY) == 'X') {
                                        blocked = true;
                                        break;
                                    }
                                }
                                if (!blocked) {
                                    int newX = x + dir[0], newY = y + dir[1];
                                    char cell = map.getMap(newX, newY);
                                    if (cell == '#' || cell == 'H' || cell == '◉' || cell == 'O' || cell == '☠'||cell == 'Ø') {
                                        System.out.println(++k + ")" + newX + " " + newY);
                                    }
                                }
                            }
                        }

                        k = 0;
                        int chos = scanner.nextInt();

                        for (int stp = 1; stp <= trans; stp++) {
                            int[][] directions = {{stp, 0}, {0, stp}, {-stp, 0}, {0, -stp}, {stp / 2, stp / 2}, {-stp / 2, -stp / 2}, {stp / 2, -stp / 2}, {-stp / 2, stp / 2}};

                            for (int[] dir : directions) {
                                boolean blocked = false;
                                for (int step = 1; step <= stp; step++) {
                                    int newX = x + dir[0] * step / stp, newY = y + dir[1] * step / stp;
                                    if (newX < 0 || newX >= map_width || newY < 0 || newY >= map_height || map.getMap(newX, newY) == 'X') {
                                        blocked = true;
                                        break;
                                    }
                                }
                                if (!blocked) {
                                    int newX = x + dir[0], newY = y + dir[1];
                                    char cell = map.getMap(newX, newY);
                                    if (cell == '#' || cell == 'H' || cell == '◉' || cell == 'O' || cell == '☠' || cell == 'Ø') {
                                        if (++k == chos) {
                                            if (cell == '◉') {
                                                playerCastle.Collect_Gold(150);
                                            } else if (cell == '☠') {
                                                playerCastle.Collect_Gold(500);
                                            }
                                            else if (cell == 'Ø'){
                                                System.out.println("Вы наступили в токсичную лужу и ваш боец теряет 10hp");
                                                playerHero.takeDamage(10);
                                            }
                                            playerHero.move(x, y, newX, newY, map);
                                        }
                                    }
                                }
                            }
                        }


                        System.out.println("Хотите атаковать?\n1) Да\n2) Нет");
                        int question = scanner.nextInt();
                        if (question == 1) {
                            atackaHero(playerHero, aiHero, playerCastle,aiCastle, perk, map);
                        }
                    }
                    else if (perk!=100 && perk!=-100) {


                        System.out.println("Сделайте выбор клетки для перемещения:");
                        int x = playerHero.getArmy().get(perk - 1).get_locationx();
                        int y = playerHero.getArmy().get(perk - 1).get_locationy();
                        System.out.println("Координаты перка " + x + " " + y);
                        int trans = playerHero.getArmy().get(perk - 1).get_speed();
                        int k = 0;
                        for (int stp = 1; stp <= trans; stp++) {
                            int[][] directions = {{stp, 0}, {0, stp}, {-stp, 0}, {0, -stp}, {stp / 2, stp / 2}, {-stp / 2, -stp / 2}, {stp / 2, -stp / 2}, {-stp / 2, stp / 2}};

                            for (int[] dir : directions) {
                                boolean blocked = false;
                                for (int step = 1; step <= stp; step++) {
                                    int newX = x + dir[0] * step / stp, newY = y + dir[1] * step / stp;
                                    if (newX < 0 || newX >= map_width || newY < 0 || newY >= map_height || map.getMap(newX, newY) == 'X') {
                                        blocked = true;
                                        break;
                                    }
                                }
                                if (!blocked) {
                                    int newX = x + dir[0], newY = y + dir[1];
                                    char cell = map.getMap(newX, newY);
                                    if (cell == '#' || cell == 'H' || cell == '◉' || cell == 'O' || cell == '☠' || cell == 'Ø') {
                                        System.out.println(++k + ")" + newX + " " + newY);
                                    }
                                }
                            }
                        }

                        k = 0;
                        int chos = scanner.nextInt();

                        for (int stp = 1; stp <= trans; stp++) {
                            int[][] directions = {{stp, 0}, {0, stp}, {-stp, 0}, {0, -stp}, {stp / 2, stp / 2}, {-stp / 2, -stp / 2}, {stp / 2, -stp / 2}, {-stp / 2, stp / 2}};

                            for (int[] dir : directions) {
                                boolean blocked = false;
                                for (int step = 1; step <= stp; step++) {
                                    int newX = x + dir[0] * step / stp, newY = y + dir[1] * step / stp;
                                    if (newX < 0 || newX >= map_width || newY < 0 || newY >= map_height || map.getMap(newX, newY) == 'X') {
                                        blocked = true;
                                        break;
                                    }
                                }
                                if (!blocked) {
                                    int newX = x + dir[0], newY = y + dir[1];
                                    char cell = map.getMap(newX, newY);
                                    if (cell == '#' || cell == 'H' || cell == '◉' || cell == 'O' || cell == '☠' || cell == 'Ø') {
                                        if (++k == chos) {
                                            if (cell == '◉') {
                                                playerCastle.Collect_Gold(150);
                                            } else if (cell == '☠') {
                                                playerCastle.Collect_Gold(500);
                                            }
                                            else if (cell == 'Ø'){
                                                System.out.println("Вы наступили в токсичную лужу и ваш боец теряет 10hp");
                                                playerHero.getArmy().get(perk-1).hp-=10;
                                            }
                                            playerHero.getArmy().get(perk - 1).move(x, y, newX, newY, map);
                                        }
                                    }
                                }
                            }
                        }


                        System.out.println("Хотите атаковать?\n1) Да\n2) Нет");
                        int question = scanner.nextInt();
                        if (question == 1) {
                            atacka(playerHero, aiHero, playerCastle,aiCastle, perk, map);
                        }
                    }
                    map.printMap();

                }
                else if (choice == 2) {
                    int perk = perk_chose(playerHero);
                    if(perk==100){atackaHero(playerHero,aiHero,playerCastle,aiCastle,perk,map);}
                    else if (perk==-100){return;}
                    else {atacka(playerHero,aiHero,playerCastle,aiCastle,perk,map);}

                }
                else if (choice == 3) {
                    playerCastle.build();
                }
                else if (choice == 4) {
                    playerCastle.recruit(playerHero,map);
                }
                else if (choice == 5) {
                    playerCastle.Casino();
                }
                else if (choice == 6) {
                    playerCastle.Lesopilka(playerHero);
                }
                else if (choice == 7) {



                    if(playerHero.study == true){
                        // Размеры карты
                        int mapWidth = map.Map_width();
                        int mapHeight = map.Map_height();

                        // Список доступных для вырубки клеток
                        List<int[]> availableCells = new ArrayList<>();

                        // Проверяем все 8 соседних клеток
                        for (int i = -1; i <= 1; i++) {
                            for (int j = -1; j <= 1; j++) {
                                if (i == 0 && j == 0) continue; // Пропускаем саму клетку героя

                                int neighborX = playerHero.get_locationx() + i;
                                int neighborY = playerHero.get_locationy() + j;

                                // Проверяем, что клетка существует на карте
                                if (neighborX >= 0 && neighborX < mapWidth && neighborY >= 0 && neighborY < mapHeight) {
                                    // Проверяем, можно ли вырубить клетку
                                    if (map.getMap(neighborX, neighborY) == 'X') {
                                        // Добавляем клетку в список доступных
                                        availableCells.add(new int[]{neighborX, neighborY});
                                    }
                                }
                            }
                        }

                        // Если есть доступные клетки для вырубки
                        if (!availableCells.isEmpty()) {
                            // Выводим список доступных клеток
                            System.out.println("Доступные клетки для вырубки:");
                            for (int idx = 0; idx < availableCells.size(); idx++) {
                                int[] cell = availableCells.get(idx);
                                System.out.println((idx + 1) + ") Клетка (" + cell[0] + ", " + cell[1] + ")");
                            }

                            // Даем игроку выбрать клетку

                            System.out.print("Выберите клетку для вырубки (введите номер): ");
                            choice = scanner.nextInt();

                            // Проверяем, что выбор корректен
                            if (choice > 0 && choice <= availableCells.size()) {
                                int[] selectedCell = availableCells.get(choice - 1);
                                int selectedX = selectedCell[0];
                                int selectedY = selectedCell[1];

                                // Вырубаем выбранную клетку
                                map.MapChange(selectedX, selectedY, 'Ø');
                                System.out.println("Клетка (" + selectedX + ", " + selectedY + ") вырублена!");
                            } else {
                                System.out.println("Неверный выбор.");
                            }
                        } else {
                            System.out.println("Нет доступных клеток для вырубки.");
                        }



                    }
                    else {System.out.println("Иди учись!");}
                }
                else if (choice == 8) {
                    save("records.txt","map.txt","pHero.txt","iHero.txt","pCastle.txt","iCastle.txt","pUnit.txt","iUnit.txt",playerHero,aiHero,playerCastle,aiCastle,map);
                    System.out.println("Сохранение прошло успешно!");
                }

            }
        }

    }

}
