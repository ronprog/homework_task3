import java.util.Scanner;

public class GameMap {
    private  int width = 6;
    private  int height = 6;
    private char[][] map;

    public GameMap() {
        map = new char[height][width];

        generateMap();
    }
    public void MapChange(int x, int y, char elem){

        map[y][x] = elem;
    }
    public int Map_height(){
        return this.height;
    }
    public int Map_width(){
        return this.width;
    }
    public char getMap(int x, int y){
        return this.map[y][x];
    }
    private void generateMap() {
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (j == 0 && i==0) map[i][j] = '⚑'; // Замок игрока
                else if (j == width -1 && i == height-1) map[i][j] = '⚐'; // Замок компьютера
                else if (i > 0 && j >= i && j <= i+1) map[i][j] = 'H'; // Дорога
                else if (Math.random() < 0.7) map[i][j] = 'X'; // Препятствия
                else map[i][j] ='◉';//ЗОЛОТОООООО
            }
        }
    }
    public int loadmap(String mapa){
        int k = 0;
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                map[i][j] = mapa.charAt(k);

                k+=1;
            }
        }
        return k;
    }
    public void createMap(){
        Scanner sc = new Scanner(System.in);
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                System.out.print("Клетка" + i + "," + j + "\n" + "Выберите символ: ◉ H X" );

                this.map[i][j] = sc.next().charAt(0);
                printMap();


            }
        }
        this.map[0][0] = '⚑';
        this.map[5][5] = '⚐';



    }
    public void printMap() {
        System.out.print(" ");
        for(int i = 0;i<map.length;i++) {
            System.out.printf("%3s ", i);
        }
        System.out.print("\n");
        for(int i = 0;i<map.length;i++) {
            System.out.print(i);
            for (int j = 0;j<map[i].length;j++) {
                System.out.printf("%3s ",map[i][j]);

            }
            System.out.print("\n");
        }
    }
}
