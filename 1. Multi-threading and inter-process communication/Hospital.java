import java.util.Random;

public class Hospital extends Thread {
    private final int period;
    private final int maxNewTreatments;
    private final int iterations;
    private boolean[] flag;
    private HealthCareManager healthCareManager;
    private final Random rand = new Random();

    public Hospital(int period, int maxNewTreatments, int iterations, HealthCareManager healthCareManager, boolean[] flag) {
        this.period = period;
        this.maxNewTreatments = maxNewTreatments;
        this.iterations = iterations;
        this.healthCareManager = healthCareManager;
        this.flag = flag;
    }

    @Override
    public void run() {
        while (flag[0]) {
            int currentlyInICU = healthCareManager.getCurrentlyInICU();
            if (currentlyInICU > 0) {
                int newTreatments = rand.nextInt(Math.min(healthCareManager.getCurrentlyInICU(), maxNewTreatments) + 1);
                healthCareManager.handleNewTreatments(newTreatments);
            }
            try {
                sleep(period * 1000);
            } catch (InterruptedException e) {
                System.out.println("Exception: Thread HOSPITAL was interrupted while sleeping:");
                e.printStackTrace();
            }
        }
    }
}
