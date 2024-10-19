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
        // this is the critical part of our system, so it needs to be synchronized
        if (isInfection) {
            this.totalInfections += number;
            boolean enoughBeds = this.availableBeds >= number;
            if (enoughBeds) {
                this.availableBeds -= number;
//                this.currentlyInICU += number;
                return;
            }
            // else if !enoughBeds
            this.currentlyOutOfICU += number - availableBeds;
            this.availableBeds = 0;
            return;
        }
        // else if we have new treatments
        int inICU = this.getCurrentlyInICU();
        boolean validTreatment = inICU >= number;
        if (validTreatment) {
            this.totalTreatments += number;
//            this.currentlyInICU -= number;
            this.availableBeds += number;
            return;
        }
        // else if !validTreatment
        this.totalTreatments += inICU;
//        this.currentlyInICU = 0;
        this.availableBeds = this.totalNumberOfBeds;
    }

}
