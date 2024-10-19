public class RaceAgainstPandemic {
    public static void main(String[] args) {


        final int SIMULATION_DURATION_SEC = 30;
        boolean[] flag = new boolean[1];
        flag[0] = true;

        final int DISEASE_PERIOD_SEC = 1;
        final int DISEASE_MAX_NEW_INFECTIONS = 20;
        final int DISEASE_ITERATIONS = 10;

        final int HOSPITAL_PERIOD_SEC = 2;
        final int HOSPITAL_MAX_NEW_TREATMENTS = 8;
        final int HOSPITAL_ITERATIONS = 5;

        final int HEALTH_CARE_SYSTEM_AVAILABLE_BEDS = 56;
        System.out.println("-------------");
        System.out.println("Simulation parameters");
        System.out.printf("%40s: %5s sec\n", "Simulation duration: ", SIMULATION_DURATION_SEC);
        System.out.println("  -- Disease");
        System.out.printf("%40s: %5s sec\n", "New infections every: ", DISEASE_PERIOD_SEC);
        System.out.printf("%40s: %5s people\n", "Max new infections per step: ", DISEASE_MAX_NEW_INFECTIONS);
        System.out.println("  -- Hospital");
        System.out.printf("%40s: %5s sec\n", "New treatments every: ", HOSPITAL_PERIOD_SEC);
        System.out.printf("%40s: %5s people\n", "Max new treatments per step: ", HOSPITAL_MAX_NEW_TREATMENTS);
        System.out.println("-------------");


        HealthCareManager healthCareManager = HealthCareManager.getInstance(HEALTH_CARE_SYSTEM_AVAILABLE_BEDS);
        Disease disease = new Disease(
                DISEASE_PERIOD_SEC,
                DISEASE_MAX_NEW_INFECTIONS,
                DISEASE_ITERATIONS,
                healthCareManager,
                flag
        );
        Hospital hospital = new Hospital(
                HOSPITAL_PERIOD_SEC,
                HOSPITAL_MAX_NEW_TREATMENTS,
                HOSPITAL_ITERATIONS,
                healthCareManager,
                flag
        );
        disease.start();
        hospital.start();
        System.out.println("Simulation running ...");

        try {
            Thread.sleep(SIMULATION_DURATION_SEC * 1000);
        } catch (InterruptedException e) {
            System.out.println("Exception: Thread main was interrupted while sleeping:");
            e.printStackTrace();
        }
        flag[0] = false;

        try {
            disease.join();
            hospital.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println("Simulation completed");
        System.out.println("-------------");
        System.out.println("Results");

        int currentlyInICU = healthCareManager.getCurrentlyInICU();
        int currentlyOutOfICU = healthCareManager.getCurrentlyOutOfICU();
        int totalTreatments = healthCareManager.getTotalTreatments();
        int sum = currentlyInICU + currentlyOutOfICU + totalTreatments;

        System.out.println("In     ICU      : " + healthCareManager.getCurrentlyInICU());
        System.out.println("Out of ICU      : " + healthCareManager.getCurrentlyOutOfICU());
        System.out.println("Total treatments: " + healthCareManager.getTotalTreatments());
        System.out.println("Sum             : " + sum);
        System.out.println("Total infections: " + healthCareManager.getTotalInfections());
    }
}
