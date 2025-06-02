import java.util.ArrayList;
import java.util.List;

class Hero {
    private  int damage;
    private int hp;
    private String name;
    private List<Unit> army = new ArrayList<>();
    private int x, y,speed; // Координаты на карте
    char icon;
    boolean study;
    public Hero(String name, int x, int y) {
        this.name = name;
        this.x = x;
        this.y = y;
        this.speed = 2;
        this.icon = 'R';
        this.hp = 1000;
        this.damage = 40;
        this.study = false;

    }
    @Override
    public String toString() {
        return super.toString();
    }
    public void setHp(int hp){
        this.hp = hp;
    }
    public void study(){this.study = true;}
    public int get_locationx(){
        return this.x;
    }

    public boolean isHero() {
        return true; // Герой возвращает true
    }

    public int get_locationy(){
        return this.y;
    }
    public int get_speed(){
        return this.speed;
    }
    public int hp(){
        return this.hp;
    }
    public void move(int x,int y, int xt, int yt,GameMap map){
        this.x = xt;
        this.y = yt;

        map.MapChange(x,y,'H');
        map.MapChange(xt,yt,this.icon);
    }
    public void attackHero(Hero enemyHero) {

            int damage = this.damage; // Метод для получения силы атаки
            enemyHero.takeDamage(damage);
            System.out.println(this.name + " наносит " + damage + " урона " + enemyHero.name);


    }
    public void attackUnit(Unit enemyUnit){
        int damage = this.damage; // Метод для получения силы атаки
        enemyUnit.takedamage(damage);
        System.out.println(this.name + " наносит " + damage + " урона " + enemyUnit.name);
    }

    public void takeDamage(int damage) {
        this.hp = (this.hp - damage);
        if (this.hp <= 0) {
            System.out.println(this.name + " пал в бою!");
        }
    }

    public void addUnit(Unit unit,GameMap map,String flag) {

        army.add(unit);
        if (flag == "player"){
            for(int i = 0; i < map.Map_height();i++){
                if (map.getMap(0 + i,0) == 'X' || map.getMap(0 + i,0) == 'H' || map.getMap(0 + i,0) == '◉' ){
                    map.MapChange(0+i,0, unit.icon);
                    unit.change_location(0+i,0);
                    break;
                }
                else if (map.getMap(0 ,0+ i) == 'X' || map.getMap(0,0 + i) == 'H' || map.getMap(0 ,0+ i) == '◉' ){
                    map.MapChange(0,0+i, unit.icon);
                    unit.change_location(0,0+i);
                    break;
                }
                else if (map.getMap(0 + i,0+ i) == 'X' || map.getMap(0 + i,0+ i) == 'H' || map.getMap(0 + i,0+ i) == '◉' ){
                    map.MapChange(0+i,0+ i, unit.icon);
                    unit.change_location(0+i,0+i);
                    break;
                }

            }


        }
        if (flag == "ai"){
            for (int distance = 1; distance <= 2; distance++) {
                for (int i = 0; i < map.Map_height(); i++) {
                    int row = map.Map_height() - 1 - i;
                    int col = map.Map_width() - 1;

                    // Проверяем все соседние клетки на текущем расстоянии
                    for (int dr = -distance; dr <= distance; dr++) {
                        for (int dc = -distance; dc <= distance; dc++) {
                            int newRow = row + dr;
                            int newCol = col + dc;

                            // Проверяем границы карты
                            if (newRow >= 0 && newRow < map.Map_height() && newCol >= 0 && newCol < map.Map_width()) {
                                char cell = map.getMap(newRow, newCol);
                                if (cell == 'X' || cell == 'H' || cell == '◉') {
                                    map.MapChange(newRow, newCol, unit.icon);
                                    unit.change_location(newRow, newCol);


                                    return;
                                }
                            }
                        }
                    }
                }
            }





        }

    }
    public boolean isAlive() {
        return hp > 0;
    }




    public boolean hasArmy() {
        return !army.isEmpty();
    }
    public void death(GameMap map) {
        map.MapChange(this.get_locationx(),this.get_locationy(),'H');
    }
    public void smert(int perk,GameMap map){
        if (this.army.get(perk).price > 0){
            map.MapChange(this.army.get(perk).get_locationx(),this.army.get(perk).get_locationy(),'☠');
        }
        else{
            map.MapChange(this.army.get(perk).get_locationx(),this.army.get(perk).get_locationy(),'H');
        }
        this.army.remove(perk);

    }
    public List<Unit> getArmy() {  // <--- Добавляем этот метод
        return army;
    }
}
