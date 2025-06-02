// Cafe.java
import java.util.Random;
import java.util.concurrent.Semaphore;

class Cafe {
    private final Semaphore waiters = new Semaphore(3);
    private final Semaphore pigPen = new Semaphore(5);
    private volatile boolean isOpen = false;

    public void open() { isOpen = true; }
    public void close() { isOpen = false; }

    public void useService(String service, NPC visitor) throws InterruptedException {
        if (!isOpen) {
            System.out.println(visitor.getName() + " видит, что кафе закрыто");
            return;
        }

        if (service.equals("Выпить зелье")) {
            int doses = 1 + new Random().nextInt(3);
            System.out.println(visitor.getName() + " превращается в свинью на " + doses + " доз");
            pigPen.acquire();
            visitor.becomePig(doses);
            Thread.sleep(doses * 1000);
            pigPen.release();
            visitor.revertForm();
        } else {
            waiters.acquire();
            int time = service.equals("Просто перекус") ? 1500 : 3000;
            Thread.sleep(time);
            waiters.release();
        }
    }
}