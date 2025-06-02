import java.util.Scanner;

class Battle {
    public static void start(Hero hero1, Hero hero2) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Битва между " + hero1 + " и " + hero2);

        while (hero1.hasArmy() && hero2.hasArmy()) {
            System.out.println("Выберите действие: (1) Атаковать (2) Отступить");
            int action = scanner.nextInt();
            if (action == 1) {
                hero1.getArmy().get(0).attack(hero2.getArmy().get(0));
                if (!hero2.getArmy().get(0).isAlive()) {
                    hero2.getArmy().remove(0);
                    System.out.println("Юнит противника уничтожен!");
                }
            } else {
                System.out.println("Вы отступили!");
                break;
            }
        }

        if (!hero2.hasArmy()) {
            System.out.println("Победа!");
        }
    }
}
