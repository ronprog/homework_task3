abstract class Unit {
    protected String name;
    protected int hp, attack, speed, range,x,y,price;
    protected char icon;

    public Unit(String name, int hp, int attack, int speed,int x, int y, char icon,int range,int price) {
        this.name = name;
        this.hp = hp;
        this.attack = attack;
        this.speed = speed;
        this.icon = icon;
        this.x = x;
        this.y = y;
        this.range = range;
        this.price = price;
    }
    public void change_location(int x,int y){
        this.x = x;
        this.y = y;

    }
    public void move(int x,int y, int xt, int yt,GameMap map){
        this.x = xt;
        this.y = yt;

        map.MapChange(x,y,'H');
        map.MapChange(xt,yt,this.icon);
    }
    public int get_locationx(){
        return this.x;
    }
    public int get_locationy(){
        return this.y;
    }
    public int get_range(){
        return this.range;
    }
    public int get_speed(){

        return this.speed;
    }

    @Override
    public String toString() {
        return name;
    }
    public char get_icon(){
        return this.icon;
    }
    public abstract void attack(Unit target);

    public boolean isAlive() {
        return hp > 0;
    }
    public void checkUnit(String unit){

    }
    public boolean isHero() {
        return false; // По умолчанию юнит не является героем
    }
    public void takedamage(int damage){
        this.hp -= damage;
    }
}

class Spearman extends Unit {

    public Spearman() {
        super("Копейщик", 50, 10, 2,0,0, 'ᛘ', 1,0);
    }
    @Override
    public String toString() {
        return name;
    }
    public void attack(Unit target) {
        target.hp -= this.attack;
        System.out.println(name + " атакует " + target.name + " (-" + attack + " HP)");
    }


}
class Archer extends Unit {
    public Archer() {
        super("Арбалетчик", 30, 25, 2, 0,0,'➶',3,75);
    }
    @Override
    public String toString() {
        return name;
    }
    public void attack(Unit target) {
        target.hp -= this.attack;
        System.out.println(name + " стреляет в " + target.name + " (-" + attack + " HP)");
    }
}
class Swordman extends Unit {
    public Swordman() {
        super("Мечник", 60, 35, 2, 0,0,'⚔',3,100);
    }
    @Override
    public String toString() {
        return name;
    }
    public void attack(Unit target) {
        target.hp -= this.attack;
        System.out.println(name + " пронзает " + target.name + " (-" + attack + " HP)");
    }
}
class Cavalery extends Unit {
    public Cavalery() {
        super("Кавалерист", 30, 25, 4, 0,0,'♞',3,120);
    }
    @Override
    public String toString() {
        return name;
    }
    public void attack(Unit target) {
        target.hp -= this.attack;
        System.out.println(name + " на скорости ранит " + target.name + " (-" + attack + " HP)");
    }
}
class Paladin extends Unit {
    public Paladin() {
        super("Паладин", 30, 20, 2, 0,0,'☄',3,150);
    }
    @Override
    public String toString() {
        return name;
    }
    public void attack(Unit target) {
        target.hp -= this.attack;
        System.out.println(name + " кидает огненный шар " + target.name + " (-" + attack + " HP)");
    }
}
