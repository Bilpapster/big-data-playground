import org.junit.Test;

import static org.junit.Assert.assertEquals;

import raceAgainstPandemic.Disease;
import raceAgainstPandemic.HealthCareManager;
import raceAgainstPandemic.Hospital;
import raceAgainstPandemic.Timer;
import util.ProgressBar;

import java.util.Random;

public class TestRaceAgainstPandemic {
    @Test
    public void testThreadSafeAccessToCounters() {
        Random rand = new Random();
        int totalSimulations = 10;

        for (int i = 0; i < totalSimulations; i++) {
            int durationSec = rand.nextInt( 1191) + 10; // random in range [10, 1200], i.e. 10 sec to 20 min
            int availableBeds = rand.nextInt(10 ^ 5) + 20; // random in range [20, 10^5+20]
            int diseasePeriod = rand.nextInt(10) + 1; // random in range [1, 10]
            int diseaseMaxNewInfections = rand.nextInt(availableBeds / 2) + 2; // random in range [2, available_beds/2]
            int hospitalPeriod = rand.nextInt(10) + 1; // // random in range [1, 10]
            int hospitalMaxNewTreatments = rand.nextInt(diseaseMaxNewInfections) + 1; // random in range [1, maxNewInfections]

            HealthCareManager healthCareManager = HealthCareManager.getInstance(availableBeds);

            Timer sharedTimer = new Timer();
            Disease disease = new Disease(
                    diseasePeriod,
                    diseaseMaxNewInfections,
                    healthCareManager,
                    sharedTimer
            );
            Hospital hospital = new Hospital(
                    hospitalPeriod,
                    hospitalMaxNewTreatments,
                    healthCareManager,
                    sharedTimer
            );
            disease.start();
            hospital.start();
            new ProgressBar("Running simulation "  + (i+1) + " of " + totalSimulations + " ("+ durationSec +"sec)"  ,
                    100, (long) (durationSec* 9.9)).start();
            try {
                Thread.sleep(durationSec * 1000L);
            } catch (InterruptedException e) {
                System.out.println("Exception: Thread was interrupted while sleeping:");
                e.printStackTrace();
            }

            sharedTimer.stop();
            try {
                disease.join();
                hospital.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            int currentlyInICU = healthCareManager.getCurrentlyInICU();
            int currentlyOutOfICU = healthCareManager.getCurrentlyOutOfICU();
            int totalTreatments = healthCareManager.getTotalTreatments();
            int sum = currentlyInICU + currentlyOutOfICU + totalTreatments;
            int totalInfections = healthCareManager.getTotalInfections();
            assertEquals(
                    "(In_ICU + Out_of_ICU + Treated) must be equal to the total number of infections. Otherwise" +
                            " simulation is not thread-safe!",
                    sum, totalInfections
            );
            System.out.println();
            HealthCareManager.reset();
        }
    }

    @Test
    public void testMultiDiseaseMultiHospitalVersion() {
        Random rand = new Random();
        int totalSimulations = 10;

        for (int i = 0; i < totalSimulations; i++) {
            int durationSec = rand.nextInt( 1191) + 10; // random in range [10, 1200], i.e. 10 sec to 20 min
            int availableBeds = rand.nextInt(10 ^ 5) + 20; // random in range [20, 10^5+20]
            int diseasePeriod = rand.nextInt(10) + 1; // random in range [1, 10]
            int diseaseMaxNewInfections = rand.nextInt(availableBeds / 2) + 2; // random in range [2, available_beds/2]
            int hospitalPeriod = rand.nextInt(10) + 1; // // random in range [1, 10]
            int hospitalMaxNewTreatments = rand.nextInt(diseaseMaxNewInfections) + 1; // random in range [1, maxNewInfections]

            HealthCareManager healthCareManager = HealthCareManager.getInstance(availableBeds);

            Timer sharedTimer = new Timer();
            Disease disease1 = new Disease(
                    diseasePeriod,
                    diseaseMaxNewInfections,
                    healthCareManager,
                    sharedTimer
            );
            Disease disease2 = new Disease(
                    diseasePeriod,
                    diseaseMaxNewInfections,
                    healthCareManager,
                    sharedTimer
            );
            Disease disease3 = new Disease(
                    diseasePeriod,
                    diseaseMaxNewInfections,
                    healthCareManager,
                    sharedTimer
            );
            Hospital hospital1 = new Hospital(
                    hospitalPeriod,
                    hospitalMaxNewTreatments,
                    healthCareManager,
                    sharedTimer
            );
            Hospital hospital2 = new Hospital(
                    hospitalPeriod,
                    hospitalMaxNewTreatments,
                    healthCareManager,
                    sharedTimer
            );
            Hospital hospital3 = new Hospital(
                    hospitalPeriod,
                    hospitalMaxNewTreatments,
                    healthCareManager,
                    sharedTimer
            );
            disease1.start();
            disease2.start();
            disease3.start();
            hospital1.start();
            hospital2.start();
            hospital3.start();
            new ProgressBar("Running simulation "  + (i+1) + " of " + totalSimulations + " ("+ durationSec +"sec)"  ,
                    100, (long) (durationSec* 9.9)).start();
            try {
                Thread.sleep(durationSec * 1000L);
            } catch (InterruptedException e) {
                System.out.println("Exception: Thread was interrupted while sleeping:");
                e.printStackTrace();
            }

            sharedTimer.stop();
            try {
                disease1.join();
                disease2.join();
                disease3.join();
                hospital1.join();
                hospital2.join();
                hospital3.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            int currentlyInICU = healthCareManager.getCurrentlyInICU();
            int currentlyOutOfICU = healthCareManager.getCurrentlyOutOfICU();
            int totalTreatments = healthCareManager.getTotalTreatments();
            int sum = currentlyInICU + currentlyOutOfICU + totalTreatments;
            int totalInfections = healthCareManager.getTotalInfections();
            assertEquals(
                    "(In_ICU + Out_of_ICU + Treated) must be equal to the total number of infections. Otherwise" +
                            " simulation is not thread-safe!",
                    sum, totalInfections
            );
            System.out.println("Total infections: " + totalInfections);
            HealthCareManager.reset();
        }
    }
}
