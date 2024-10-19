package raceAgainstPandemic;

import java.util.Random;

public class Disease extends Thread {
    private final int maxNewInfections;
    private final int period;
    private final Timer timer;
    private final HealthCareManager healthCareManager;
    private final Random rand = new Random();

    public Disease(int period, int maxNewInfections, HealthCareManager healthCareManager, Timer timer) {
        this.period = period;
        this.maxNewInfections = maxNewInfections;
        this.healthCareManager = healthCareManager;
        this.timer = timer;
    }

    @Override
    public void run() {
        while (timer.isRunning()) {
            int newInfections = rand.nextInt(maxNewInfections) + 1;
            this.healthCareManager.handleNewInfections(newInfections);
            try {
                sleep(period * 1000L);
            } catch (InterruptedException e) {
                System.out.println("Exception: Thread DISEASE was interrupted while sleeping:");
                e.printStackTrace();
            }
        }
    }
}
