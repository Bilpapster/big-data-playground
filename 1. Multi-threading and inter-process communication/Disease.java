import java.util.Random;

public class Disease extends Thread {
    private final int maxNewInfections;
    private final int period;
    private final int iterations;
    private boolean[] flag;
    private final HealthCareManager healthCareManager;
    private final Random rand = new Random();

    public Disease(int period, int maxNewInfections, int iterations, HealthCareManager healthCareManager, boolean[] flag) {
        this.period = period;
        this.maxNewInfections = maxNewInfections;
        this.iterations = iterations;
        this.healthCareManager = healthCareManager;
        this.flag = flag;
    }

    @Override
    public void run() {
        while (flag[0]) {
            int newInfections = rand.nextInt(maxNewInfections) + 1;
            this.healthCareManager.handleNewInfections(newInfections);
            try {
                sleep(period * 1000);
            } catch (InterruptedException e) {
                System.out.println("Exception: Thread DISEASE was interrupted while sleeping:");
                e.printStackTrace();
            }
        }
    }
}
