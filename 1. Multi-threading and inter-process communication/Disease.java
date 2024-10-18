import java.util.Random;

public class Disease extends Thread {
    private final int maxNewInfections;
    private final int period;
    private final int iterations;
    private boolean flag = true;
    private final HealthCareManager healthCareManager;
    private final Random rand = new Random();

    public Disease(int period, int maxNewInfections, int iterations, HealthCareManager healthCareManager) {
        this.period = period;
        this.maxNewInfections = maxNewInfections;
        this.iterations = iterations;
        this.healthCareManager = healthCareManager;
    }

    @Override
    public void run() {
        for (int i = 0; i < iterations; i++) {
//        while (flag) {
            int newInfections = rand.nextInt(maxNewInfections) + 1;
//            int newInfections = 4;
            this.healthCareManager.handleNewInfections(newInfections);
            try {
                sleep(period);
            } catch (InterruptedException e) {
                System.out.println("Exception: Thread DISEASE was interrupted while sleeping:");
                e.printStackTrace();
            }
        }
        System.out.println("DISEASE has successfully finished " + iterations + " rounds of infection.");
    }

    public void finishSimulation() {
        this.flag = false;
    }
}
