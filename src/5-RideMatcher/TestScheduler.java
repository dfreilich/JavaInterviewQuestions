package interview2;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TestScheduler {
    public ScheduledExecutorService scheduler;
    public static void main(String[] args) {
        TestScheduler test = new TestScheduler();
        test.schedule();
        System.out.println("I'm here");
        System.out.println(test.scheduler.isShutdown());
    }

    public void schedule(){
        scheduler = Executors.newScheduledThreadPool(1);
        Runnable pollRunnable = new Runnable() {
            @Override
            public void run() {
                System.out.println("inside schedule task");
            }
        };

        try {
            scheduler.scheduleAtFixedRate(pollRunnable, 0L, 5L, TimeUnit.SECONDS);
            Thread.sleep(TimeUnit.SECONDS.toMillis(10));
        } catch (Exception e) {
            System.out.println("Something unexpected happened!");
            e.printStackTrace();
        }
    }

}
