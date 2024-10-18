import java.util.Random;

public class Hospital extends Thread {
    private final int period;
    private final int maxNewTreatments;
    private final int iterations;
    private boolean flag = true;
    private HealthCareManager healthCareManager;
    private final Random rand = new Random();

    public Hospital(int period, int maxNewTreatments, int iterations, HealthCareManager healthCareManager) {
        this.period = period;
        this.maxNewTreatments = maxNewTreatments;
        this.iterations = iterations;
        this.healthCareManager = healthCareManager;
    }

    @Override
    public void run() {
        for (int i = 0; i < iterations; i++) {
//        while (flag) {
            int currentlyInICU = healthCareManager.getCurrentlyInICU();
            if (currentlyInICU > 0) {
//            int newTreatments = 5;
                int newTreatments = rand.nextInt(Math.min(healthCareManager.getCurrentlyInICU(), maxNewTreatments));
                healthCareManager.handleNewTreatments(newTreatments);
            } else {
                System.out.println("No treatments " + currentlyInICU);
            }
            try {
                sleep(period);
            } catch (InterruptedException e) {
                System.out.println("Exception: Thread HOSPITAL was interrupted while sleeping:");
                e.printStackTrace();
            }
        }
        System.out.println("DISEASE has successfully finished " + iterations + " rounds of infection.");
    }

    public void finishSimulation() {
        this.flag = false;
    }
}
