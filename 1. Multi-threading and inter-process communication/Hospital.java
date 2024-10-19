import java.util.Random;

public class Hospital extends Thread {
    private final int period;
    private final int maxNewTreatments;
    private final Timer timer;
    private final HealthCareManager healthCareManager;
    private final Random rand = new Random();

    public Hospital(int period, int maxNewTreatments, HealthCareManager healthCareManager, Timer timer) {
        this.period = period;
        this.maxNewTreatments = maxNewTreatments;
        this.healthCareManager = healthCareManager;
        this.timer = timer;
    }

    @Override
    public void run() {
        while (timer.isRunning()) {
            int currentlyInICU = healthCareManager.getCurrentlyInICU();  // check if there are patients to treat
            if (currentlyInICU > 0) {
                int newTreatments = rand.nextInt(getNewTreatmentsUpperBound());
                healthCareManager.handleNewTreatments(newTreatments);
            }
            this.sleepUntilNextInfectionRound();
        }
    }

    /***
     * Calculates and returns the upper bound of new infections for this round. The upper bound is the minimum between
     * the current number of patients in ICU and the max possible number of new treatments per round. This rational is
     * used to avoid "over-treatments", i.e., treating more patients than the actual number of patients in ICU.
     * Finally, adding 1 to the result, since rand.nextInt() is not including upper bound.
     * @return
     */
    private int getNewTreatmentsUpperBound() {
        return Math.min(healthCareManager.getCurrentlyInICU(), maxNewTreatments) + 1;
    }

    private void sleepUntilNextInfectionRound() {
        try {
            sleep(period * 1000L);
        } catch (InterruptedException e) {
            System.out.println("Exception: Thread HOSPITAL was interrupted while sleeping:");
            e.printStackTrace();
        }
    }
}
