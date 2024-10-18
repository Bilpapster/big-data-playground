public class RaceAgainstPandemic {
    public static void main(String[] args) {

        final int SIMULATION_DURATION_SEC = 15;

        final int DISEASE_PERIOD_SEC = 1;
        final int DISEASE_MAX_NEW_INFECTIONS = 20;
        final int DISEASE_ITERATIONS = 10;

        final int HOSPITAL_PERIOD_SEC = 2;
        final int HOSPITAL_MAX_NEW_TREATMENTS = 8;
//        final int HOSPITAL_ITERATIONS = DISEASE_ITERATIONS * (DISEASE_PERIOD_SEC / HOSPITAL_PERIOD_SEC);
        final int HOSPITAL_ITERATIONS = 5;

        final int HEALTH_CARE_SYSTEM_AVAILABLE_BEDS = 20;


        HealthCareManager healthCareManager = HealthCareManager.getInstance(HEALTH_CARE_SYSTEM_AVAILABLE_BEDS);
        Disease disease = new Disease(
                DISEASE_PERIOD_SEC,
                DISEASE_MAX_NEW_INFECTIONS,
                DISEASE_ITERATIONS,
                healthCareManager
        );
        Hospital hospital = new Hospital(
                HOSPITAL_PERIOD_SEC,
                HOSPITAL_MAX_NEW_TREATMENTS,
                HOSPITAL_ITERATIONS,
                healthCareManager
        );
        disease.start();
        hospital.start();

//        try {
//            Thread.sleep(SIMULATION_DURATION_SEC);
//        } catch (InterruptedException e) {
//            System.out.println("Exception: Thread main was interrupted while sleeping:");
//            e.printStackTrace();
//        }
//
//        disease.finishSimulation();
//        hospital.finishSimulation();

        try {
            disease.join();
            hospital.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        int currentlyInICU = healthCareManager.getCurrentlyInICU();
        int currentlyOutOfICU = healthCareManager.getCurrentlyOutOfICU();
        int totalTreatments = healthCareManager.getTotalTreatments();
        int sum =  currentlyInICU + currentlyOutOfICU + totalTreatments;

        System.out.println("In     ICU      : "+ healthCareManager.getCurrentlyInICU());
        System.out.println("Out of ICU      : "+ healthCareManager.getCurrentlyOutOfICU());
        System.out.println("Total treatments: "+ healthCareManager.getTotalTreatments());
        System.out.println("Sum             : "+ sum);
        System.out.println("Total infections: "+ healthCareManager.getTotalInfections());
    }
}
