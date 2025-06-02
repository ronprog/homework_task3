import java.util.*;

class Castle {
    private String owner;
    private Set<String> ownbuildings = new HashSet<>();
    public ArrayList buildingCount = new ArrayList(5);
    public ArrayList soldiersCount = new ArrayList(5);

    private int gold = 1000;
    private int osad = 10;
    public Castle(String owner) {
        this.owner = owner;
        this.ownbuildings.add("Таверна");
        this.ownbuildings.add("Сторожевой пост");

        this.soldiersCount.add("1");
        for (int i=0;i<5;i++){this.soldiersCount.add("0");}
        for (int i=0;i<5;i++){this.buildingCount.add("0");}

    }

    public int Get_Gold(){
        return this.gold;
    }
    public int GetOsad(){return this.osad;}
    public void GetDamage(){
        this.osad-=1;

    }

    public String getBuildingCount() {
        String buildingCount = "";

        for (int i=0;i<this.buildingCount.size();i++){buildingCount = buildingCount + this.buildingCount.get(i);}
        System.out.println(buildingCount);
        return buildingCount;
    }

    public void setGold(int gold) {
        this.gold = gold;
    }
    public void setBuildings(Integer buildCount){
        int k = buildCount;
        if (k%10 == 1){
            this.ownbuildings.add("Собор");
        }
        k = k/10;
        if (k%10 == 1){
            this.ownbuildings.add("Арена");
        }
        k = k/10;
        if (k%10 == 1){
            this.ownbuildings.add("Оружейная");
        }
        k = k/10;
        if (k%10 == 1){
            this.ownbuildings.add("Башня арбалетчиков");
        }
        k = k/10;
        if (k%10 == 1){
            this.ownbuildings.add("Сторожевой пост");
        }
        k = k/10;

    }


    public void recruitAi(Hero aiHero, GameMap map) {

        Map<String, Integer> Soldiers = new HashMap<>();
        Map<String, String> Asoc = new HashMap<>();

        Soldiers.put("Копейщик", 0);
        Soldiers.put("Арбалетчик", 75);
        Soldiers.put("Мечник", 100);
        Soldiers.put("Кавалерист", 120);
        Soldiers.put("Паладин", 150);
        Asoc.put("Копейщик", "Сторожевой пост");
        Asoc.put("Паладин", "Собор");
        Asoc.put("Мечник", "Оружейная");
        Asoc.put("Кавалерист", "Арена");
        Asoc.put("Арбалетчик", "Башня арбалетчиков");

        // Список уже имеющихся у AI юнитов
        Set<String> ownedUnits = new HashSet<>();
        for (Unit unit : aiHero.getArmy()) {
            ownedUnits.add(unit.name);
        }

        String bestUnit = null;
        int highestPrice = 0;

        for (Map.Entry<String, Integer> entry : Soldiers.entrySet()) {
            String unit = entry.getKey();
            int price = entry.getValue();

            // Проверяем, что юнита нет в армии, есть нужное здание (если требуется), хватает золота
            if (!ownedUnits.contains(unit) &&
                    (Asoc.containsKey(unit) ? ownbuildings.contains(Asoc.get(unit)) : true) &&
                    this.gold >= price &&
                    price > highestPrice) {

                bestUnit = unit;
                highestPrice = price;
            }
        }

        if (bestUnit != null) {
            this.gold -= highestPrice;

            switch (bestUnit) {
                case "Копейщик":
                    aiHero.addUnit(new Spearman(), map, "ai");
                    soldiersCount.set(2, "1");
                    break;
                case "Арбалетчик":
                    aiHero.addUnit(new Archer(), map, "ai");
                    soldiersCount.set(0, "1");
                    break;
                case "Мечник":
                    aiHero.addUnit(new Swordman(), map, "ai");
                    soldiersCount.set(1, "1");
                    break;
                case "Кавалерист":
                    aiHero.addUnit(new Cavalery(), map, "ai");
                    soldiersCount.set(4, "1");

                    break;
                case "Паладин":
                    aiHero.addUnit(new Paladin(), map, "ai");
                    soldiersCount.set(3, "1");

                    break;
            }

            System.out.println("\nAI нанял: " + bestUnit);
            map.printMap();
        } else {
            System.out.println("\nAI не может нанять юнита (все уже куплены или не хватает золота).\n");
        }
    }




    public void recruit(Hero playerHero,GameMap map){

        Map<String, Integer> Soldiers = new HashMap<>();
        Set<String> ownSoldiers = new HashSet<>();
        Soldiers.put("Копейщик", 0);
        Soldiers.put("Арбалетчик", 75);
        Soldiers.put("Мечник", 100);
        Soldiers.put("Кавалерист", 120);
        Soldiers.put("Паладин", 150);
        for(int i = 0;i<playerHero.getArmy().size();i++){
            ownSoldiers.add(playerHero.getArmy().get(i).name.toString());
        }


        Scanner scanner = new Scanner(System.in);


        while (true) {
            System.out.println("\nДоступные бойы:");
            for (Map.Entry<String, Integer> entry : Soldiers.entrySet()) {

                if (!ownSoldiers.contains(entry.getKey())) {
                    System.out.println(entry.getKey() + ": " + entry.getValue() + " золота");
                }
            }

            System.out.print("Выберите бойца для покупки (или введите 'выход' для завершения): ");

            String choice = scanner.nextLine();

            if (choice.equalsIgnoreCase("выход")) {
                break;
            }

            if (ownSoldiers.contains(choice)) {
                System.out.println("Вы уже купили " + choice + ".");
                continue;
            }

            if (Soldiers.containsKey(choice)) {
                int price = Soldiers.get(choice);
                if (this.gold >= price) {
                    if(choice.equals("Копейщик")){
                        if(ownbuildings.contains("Сторожевой пост")){
                            this.gold -= price;
                            ownSoldiers.add(choice);
                            soldiersCount.set(2, "1");
                            playerHero.addUnit(new Spearman(),map,"player");
                            System.out.println("Вы успешно купили " + choice + ". Осталось золота: " + this.gold);
                            map.printMap();}


                        else{System.out.println("Купите" +  " Сторожевой пост");}
                    }
                    if(choice.equals("Арбалетчик")){
                        if(ownbuildings.contains("Башня арбалетчиков")){
                        this.gold -= price;
                        ownSoldiers.add(choice);
                            soldiersCount.set(0, "1");
                        playerHero.addUnit(new Archer(),map,"player");
                        System.out.println("Вы успешно купили " + choice + ". Осталось золота: " + this.gold);
                        map.printMap();}


                        else{System.out.println("Купите" +  " Башню арбалетчиков");}
                    }
                    if(choice.equals("Мечник")){
                        if(ownbuildings.contains("Оружейная")){
                            this.gold -= price;
                            ownSoldiers.add(choice);
                            soldiersCount.set(1, "1");
                            playerHero.addUnit(new Swordman(),map,"player");
                            System.out.println("Вы успешно купили " + choice + ". Осталось золота: " + this.gold);
                            map.printMap();}


                        else{System.out.println("Купите" +  " Оружейную");}
                    }
                    if(choice.equals("Кавалерист")){
                        if(ownbuildings.contains("Арена")){
                            this.gold -= price;
                            ownSoldiers.add(choice);
                            soldiersCount.set(4, "1");
                            playerHero.addUnit(new Cavalery(),map,"player");
                            System.out.println("Вы успешно купили " + choice + ". Осталось золота: " + this.gold);
                            map.printMap();}


                        else{System.out.println("Купите" +  " Арену");}
                    }
                    if(choice.equals("Паладин")){
                        if(ownbuildings.contains("Собор")){
                            this.gold -= price;
                            ownSoldiers.add(choice);
                            soldiersCount.set(3, "1");
                            playerHero.addUnit(new Paladin(),map,"player");
                            System.out.println("Вы успешно купили " + choice + ". Осталось золота: " + this.gold);
                            map.printMap();}


                        else{System.out.println("Купите" +  " Собор");}
                    }



                } else {
                    System.out.println("Недостаточно золота для покупки " + choice + ".");
                }
            } else {
                System.out.println("Такого бойца нет в списке.");
            }
        }

        System.out.println("\nВаши бойцы: " + ownSoldiers);
    }

    public String getSoldiersCount() {
        String soldiersCount = "";
        for (int i = 0; i < this.soldiersCount.size(); i++) {
            soldiersCount += this.soldiersCount.get(i);
        }
        return soldiersCount;
    }

    public void Casino(){
        Scanner scanner = new Scanner(System.in);
        System.out.println("Добро пожаловать в клуб средневековых лудоманов!");
        while (true) {
            System.out.println("Желаете сделать ставку?\n(1) Да\n(2) Нет(выход)");
            int choice = scanner.nextInt();
            if (choice == 1) {
                System.out.println("Введите размер ставки: ");
                int stavka = scanner.nextInt();
                if (stavka <= this.gold) {
                    System.out.println("Отлично! Ставка принята.....");
                    try {
                        Thread.sleep(1000); // Задержка на 1 секунду
                        System.out.println("Сейчас мы проверим твое везение!");
                        Thread.sleep(1000); // Задержка на 1 секунду
                        System.out.println("3......");
                        Thread.sleep(1000); // Задержка на 1 секунду
                        System.out.println("2......");
                        Thread.sleep(1000); // Задержка на 1 секунду
                        System.out.println("1......");
                        Thread.sleep(1000); // Задержка на 1 секунду
                        Random random = new Random();
                        int randomNumber = random.nextInt(100) + 1;
                        if (randomNumber > 70) {
                            System.out.println("Поздравляю! Ты выиграл " + stavka * 2 + " золота!");
                            this.gold += stavka * 2;
                            System.out.println("Теперь у тебя " + this.gold + " золота!");
                        } else {
                            System.out.println("К сожалению, не в этот раз(");
                            this.gold -= stavka;
                            System.out.println("Теперь у тебя " + this.gold + " золота....");

                        }
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }


                }
                else {
                    System.out.println("Не хватает золота, уничтожь пару вражеских бойцов и возвращайся!");
                }
            }
            else if (choice != 1) {
                break;
            }
        }
    }public void Lesopilka(Hero playerHero){
        Scanner scanner = new Scanner(System.in);
        System.out.println("Добро пожаловать на лесопилку, здесь вы научитесь рубить лес, если очень постараетесь!");
        while (true) {
            if (playerHero.study==false) {
                System.out.println("Желаете начать обучение ?\n(1) Да\n(2) Нет(выход)");
                int choice = scanner.nextInt();
                if (choice == 1) {

                    int oplata = 300;
                    if (oplata <= this.gold) {
                        System.out.println("Отлично! Начинаем учиться.....");
                        try {
                            Thread.sleep(1000); // Задержка на 1 секунду
                            System.out.println("Читаем теорию...");
                            Thread.sleep(1000); // Задержка на 1 секунду
                            System.out.println("Изучаем бензопилу...");
                            Thread.sleep(1000); // Задержка на 1 секунду
                            System.out.println("Срубаем первое дерево...");
                            Thread.sleep(1000); // Задержка на 1 секунду
                            System.out.println("Практикуемся...");
                            Thread.sleep(1000); // Задержка на 1 секунду
                            Random random = new Random();
                            int randomNumber = random.nextInt(100) + 1;
                            if (randomNumber > 50) {
                                System.out.println("Поздравляю! Ты прошел обучение !");
                                playerHero.study();
                                System.out.println("Теперь ты можешь вырубать лес !");
                                break;
                            } else {
                                System.out.println("К сожалению, ты провалили обучение и поранился");
                                playerHero.takeDamage(100);
                                System.out.println("Теперь у тебя на 100 здоровья меньше");

                            }
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }


                    } else {
                        System.out.println("Не хватает золота, уничтожь пару вражеских бойцов и возвращайся!");
                    }
                } else if (choice != 1) {
                    break;
                }
            }
            else {
                System.out.println("Ты уже умеешь рубить препятсвия");
                break;
            }
        }
    }
    public void buildAi() {
        Map<String, Integer> buildings = new HashMap<>();

        buildings.put("Таверна", 0);
        buildings.put("Сторожевой пост", 150);
        buildings.put("Башня арбалетчиков", 500);
        buildings.put("Оружейная", 1000);
        buildings.put("Арена", 1500);
        buildings.put("Собор", 2500);

        String bestBuilding = null;
        int highestPrice = 0;

        for (Map.Entry<String, Integer> entry : buildings.entrySet()) {
            String building = entry.getKey();
            int price = entry.getValue();

            // Проверяем, что здание ещё не построено и хватает ли золота
            if (!this.ownbuildings.contains(building) && this.gold >= price) {
                if (price > highestPrice) {
                    bestBuilding = building;
                    highestPrice = price;
                }
            }
        }

        if (bestBuilding != null) {
            this.gold -= highestPrice;
            this.ownbuildings.add(bestBuilding);
            System.out.println("AI построил: " + bestBuilding + ". Осталось золота: " + this.gold);
        } else {
            System.out.println("AI не может построить здание.");
        }
    }

    public void build() {
        Map<String, Integer> buildings = new HashMap<>();
        Map<String, Integer> buildingss = new HashMap<>();

        buildings.put("Сторожевой пост", 150);
        buildings.put("Башня арбалетчиков", 500);
        buildings.put("Оружейная", 1000);
        buildings.put("Арена", 1500);
        buildings.put("Собор", 2500);

        buildingss.put("Сторожевой пост", 0);
        buildingss.put("Башня арбалетчиков", 1);
        buildingss.put("Оружейная", 2);
        buildingss.put("Арена", 3);
        buildingss.put("Собор", 4);

        Scanner scanner = new Scanner(System.in);


        while (true) {
            System.out.println("\nДоступные здания:");
            for (Map.Entry<String, Integer> entry : buildings.entrySet()) {
                if (!this.ownbuildings.contains(entry.getKey())) {
                    System.out.println(entry.getKey() + ": " + entry.getValue() + " золота");
                }
            }

            System.out.print("Выберите здание для покупки (или введите 'выход' для завершения): ");

            String choice = scanner.nextLine();

            if (choice.equalsIgnoreCase("выход")) {
                break;
            }

            if (this.ownbuildings.contains(choice)) {
                System.out.println("Вы уже купили " + choice + ".");
                continue;
            }

            if (buildings.containsKey(choice)) {
                int price = buildings.get(choice);
                if (this.gold >= price) {
                    this.gold -= price;
                    this.ownbuildings.add(choice);
                    buildingCount.set(buildingss.get(choice),"1");
                    System.out.println("Вы успешно купили " + choice + ". Осталось золота: " + this.gold);
                } else {
                    System.out.println("Недостаточно золота для покупки " + choice + ".");
                }
            } else {
                System.out.println("Такого здания нет в списке.");
            }
        }

        System.out.println("\nВаши здания: " + this.ownbuildings);

    }
    public void Collect_Gold(int gold){
        this.gold+=gold;
        System.out.println(gold  + " золота в копилку!");
    }

}

class Building {
    private String name;

    public Building(String name) {
        this.name = name;
    }
}
