import java.util.Random;

public class NPC extends Thread {
    private final Hotel hotel;
    private final Cafe cafe;
    private final Random random;
    private boolean isPig = false;
    private boolean isHero = false;
    private int pigDoses = 0;


    public NPC(String name, Hotel hotel, Cafe cafe, boolean isHero) {
        super(name);
        this.hotel = hotel;
        this.cafe = cafe;
        this.random = new Random();
        this.isHero = isHero;
    }

    public void becomePig(int doses) {
        isPig = true;
        pigDoses = doses;
        System.out.println(getName() + " становится свиньей!");
    }

    public void revertForm() {
        isPig = false;
        System.out.println(getName() + " снова стал человеком");
    }

    public boolean isHero() { return isHero; }

    @Override
    public void run() {
        try {
            while (!isInterrupted()) {
                if (isPig) {
                    Thread.sleep(pigDoses * 1000);
                    continue;
                }

                int choice = random.nextInt(2);
                switch (choice) {
                    case 0 -> visitHotel();
                    case 1 -> visitCafe();
                }

                Thread.sleep(1000 + random.nextInt(4000));
            }
        } catch (InterruptedException e) {
            interrupt();
            System.out.println(getName() + " завершил деятельность");
        }
    }

    private void visitHotel() throws InterruptedException {
        String service = random.nextBoolean() ? "Короткий отдых" : "Длинный отдых";
        System.out.println(getName() + " идет в отель: " + service);
        hotel.useService(service, this);
    }

    private void visitCafe() throws InterruptedException {
        String[] services = {"Просто перекус", "Плотный обед", "Выпить зелье"};
        String service = services[random.nextInt(3)];
        System.out.println(getName() + " идет в кафе: " + service);
        cafe.useService(service, this);
    }
}