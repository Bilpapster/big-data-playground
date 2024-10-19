public class HealthCareManager {
    // singleton design pattern
    private static HealthCareManager instance;

    private HealthCareManager(int availableBeds) {
        this.totalNumberOfBeds = availableBeds;
        this.availableBeds = availableBeds;
    } // intentionally private for compliance with the singleton design pattern

    private int availableBeds;
    private final int totalNumberOfBeds;
    private int totalInfections = 0;
    private int totalTreatments = 0;
    private int currentlyOutOfICU = 0;

    public static synchronized HealthCareManager getInstance(int availableBeds) {
        if (instance == null) {
            instance = new HealthCareManager(availableBeds);
        }
        return instance;
    }

    public int getCurrentlyInICU() {
        return this.totalNumberOfBeds - this.availableBeds;
    }

    public synchronized int getCurrentlyOutOfICU() {
        return this.currentlyOutOfICU;
    }

    public synchronized int getTotalInfections() {
        return this.totalInfections;
    }

    public synchronized int getTotalTreatments() {
        return this.totalTreatments;
    }

    public void handleNewInfections(int newInfections) {
        this.handleUpdate(newInfections, true);
    }

    public void handleNewTreatments(int newTreatments) {
        this.handleUpdate(newTreatments, false);
    }

    private synchronized void handleUpdate(int number, boolean isInfection) {
        // this is the critical part of our system, so it needs to be synchronized!
        if (isInfection) {
            this.handleInfections(number);
            return;
        }
        this.handleTreatments(number);
    }

    private void handleInfections(int infections) {
        this.totalInfections += infections;
        boolean enoughBeds = this.availableBeds >= infections;
        if (enoughBeds) {
            this.availableBeds -= infections;
            return;
        }
        // else if !enoughBeds
        this.currentlyOutOfICU += infections - availableBeds;
        this.availableBeds = 0;
    }

    private void handleTreatments(int number) {
        int inICU = this.getCurrentlyInICU();
        boolean validTreatment = inICU >= number;
        if (validTreatment) {
            this.totalTreatments += number;
            this.availableBeds += number;
            return;
        }
        // else if !validTreatment
        this.totalTreatments += inICU;
        this.availableBeds = this.totalNumberOfBeds;
    }

}
