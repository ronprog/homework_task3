// Hotel.java
import java.util.concurrent.Semaphore;

class Hotel {
    private final Semaphore rooms = new Semaphore(5);
    private volatile boolean isOpen = false;
    private final Semaphore heroPriority = new Semaphore(1);

    public void open() { isOpen = true; }
    public void close() { isOpen = false; }

    public void useService(String serviceType, NPC visitor) throws InterruptedException {
        if (!isOpen) {
            System.out.println(visitor.getName() + " видит, что отель закрыт");
            return;
        }


        if (visitor.isHero()) {
            heroPriority.acquire();
            rooms.acquire();
            heroPriority.release();
        } else {
            rooms.acquire();
        }

        Thread.sleep(1000);
        rooms.release();
    }
}