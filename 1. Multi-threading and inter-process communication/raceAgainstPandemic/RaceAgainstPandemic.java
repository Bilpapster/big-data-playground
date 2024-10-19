package raceAgainstPandemic;

import util.ProgressBar;

public class RaceAgainstPandemic {

    // simulation-level arguments
    private static int SIMULATION_DURATION_SEC;
    private static int HEALTH_CARE_SYSTEM_AVAILABLE_BEDS;

    // disease-level arguments
    private static int DISEASE_PERIOD_SEC;
    private static int DISEASE_MAX_NEW_INFECTIONS;

    // hospital-level arguments
    private static int HOSPITAL_PERIOD_SEC;
    private static int HOSPITAL_MAX_NEW_TREATMENTS;

    // threads and threads' management objects
    private static Disease disease;
    private static Hospital hospital;
    private static HealthCareManager healthCareManager;
    private static Timer sharedTimer;

    /**
     * Parse the program arguments, expected in the following order:
     * - simulation duration,
     * - available ICU beds,
     * - disease: time between infection steps (sec),
     * - disease: max new infections per step,
     * - hospital: time between treatment steps (sec),
     * - hospital: max new treatments per step.
     * Throws exception if not enough arguments are provided or at least one argument cannot be parsed as integer.
     *
     * @param args the CLI arguments
     */
    private static void parseArguments(String[] args) {
        if (args.length != 6) {
            throw new IllegalArgumentException("Requires exactly 6 integers for: \n" +
                    "- simulation duration\n" +
                    "- available ICU beds\n" +
                    "- disease: time between infection steps (sec)\n" +
                    "- disease: max new infections per step\n" +
                    "- hospital: time between treatment steps (sec)\n" +
                    "- hospital: max new treatments per step\n" +
                    "Command line arguments must be in the form <duration> <beds> <disease_period> <infections> <hospital_period> <treatments>"
            );
        }
        try {
            SIMULATION_DURATION_SEC = Integer.parseInt(args[0]);
            HEALTH_CARE_SYSTEM_AVAILABLE_BEDS = Integer.parseInt(args[1]);
            DISEASE_PERIOD_SEC = Integer.parseInt(args[2]);
            DISEASE_MAX_NEW_INFECTIONS = Integer.parseInt(args[3]);
            HOSPITAL_PERIOD_SEC = Integer.parseInt(args[4]);
            HOSPITAL_MAX_NEW_TREATMENTS = Integer.parseInt(args[5]);
        } catch (NumberFormatException e) {
            System.out.println("Error in parsing CLI arguments. All arguments must be integers.");
            e.printStackTrace();
        }
    }

    /**
     * Contains all actions performed before starting the simulation. For simplicity, it just prints the simulation
     * configurations to the console.
     */
    private static void beforeSimulationStart() {
        final String parameterPlaceholder = "%30s: ";
        System.out.println("-------------");
        System.out.println("Simulation parameters");
        System.out.printf(parameterPlaceholder + "%5s sec\n", "Simulation duration", SIMULATION_DURATION_SEC);
        System.out.printf(parameterPlaceholder + "%5s beds\n", "Available ICU beds", HEALTH_CARE_SYSTEM_AVAILABLE_BEDS);
        System.out.println("  -- raceAgainstPandemic.Disease");
        System.out.printf(parameterPlaceholder + "%5s sec\n", "New infections every", DISEASE_PERIOD_SEC);
        System.out.printf(parameterPlaceholder + "%5s people\n", "Max new infections per step", DISEASE_MAX_NEW_INFECTIONS);
        System.out.println("  -- raceAgainstPandemic.Hospital");
        System.out.printf(parameterPlaceholder + "%5s sec\n", "New treatments every", HOSPITAL_PERIOD_SEC);
        System.out.printf(parameterPlaceholder + "%5s people\n", "Max new treatments per step", HOSPITAL_MAX_NEW_TREATMENTS);
        System.out.println("-------------");
    }

    /**
     * Starts the simulation by spawning a disease and a hospital thread. Passes to both of them a shared timer object
     * to handle simulation stop later on.
     */
    private static void startSimulation() {
        healthCareManager = HealthCareManager.getInstance(HEALTH_CARE_SYSTEM_AVAILABLE_BEDS);
        sharedTimer = new Timer();
        disease = new Disease(
                DISEASE_PERIOD_SEC,
                DISEASE_MAX_NEW_INFECTIONS,
                healthCareManager,
                sharedTimer
        );
        hospital = new Hospital(
                HOSPITAL_PERIOD_SEC,
                HOSPITAL_MAX_NEW_TREATMENTS,
                healthCareManager,
                sharedTimer
        );
        disease.start();
        hospital.start();

        new ProgressBar("", 100, (long) (SIMULATION_DURATION_SEC* 9.9)).start();
    }

    /**
     * Contains all actions to be performed on simulation start. For simplicity, it just prints a message in the console.
     */
    private static void onSimulationStart() {
        System.out.println("Simulation running ...");
    }

    /**
     * Makes the current thread sleep for the specified duration of seconds. Throws exception if interrupted while
     * sleeping.
     *
     * @param durationSec the duration in seconds to make the current thread sleep.
     */
    private static void sleepFor(int durationSec) {
        try {
            Thread.sleep(durationSec * 1000L);
        } catch (InterruptedException e) {
            System.out.println("Exception: Thread was interrupted while sleeping:");
            e.printStackTrace();
        }
    }

    /**
     * Stops the simulation by modifying the `running` attribute of the shared timer. Waits for both threads (disease
     * and hospital) to join, before returning.
     */
    private static void stopSimulation() {
        sharedTimer.stop();
        try {
            disease.join();
            hospital.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Contains all actions to be performed after the end of the simulation. Here it simply prints the results to the
     * console.
     */
    private static void afterSimulationEnd() {
        System.out.println("Simulation completed successfully!");
        System.out.println("-------------");
        System.out.println("Results");

        int currentlyInICU = healthCareManager.getCurrentlyInICU();
        int currentlyOutOfICU = healthCareManager.getCurrentlyOutOfICU();
        int totalTreatments = healthCareManager.getTotalTreatments();
        int sum = currentlyInICU + currentlyOutOfICU + totalTreatments;

        final String resultsPlaceholder = "%20s: ";
        System.out.printf(resultsPlaceholder + "%5s people\n", "In ICU", healthCareManager.getCurrentlyInICU());
        System.out.printf(resultsPlaceholder + "%5s people\n", "Out of ICU", healthCareManager.getCurrentlyOutOfICU());
        System.out.printf(resultsPlaceholder + "%5s people\n", "Total treatments", healthCareManager.getTotalTreatments());
        System.out.printf(resultsPlaceholder + "%5s people\n", "Sum", sum);
        System.out.printf(resultsPlaceholder + "%5s people\n", "Total infections", healthCareManager.getTotalInfections());
    }


    public static void main(String[] args) {
        parseArguments(args);
        beforeSimulationStart();
        startSimulation();
        onSimulationStart();
        sleepFor(SIMULATION_DURATION_SEC);
        stopSimulation();
        afterSimulationEnd();
    }
}
