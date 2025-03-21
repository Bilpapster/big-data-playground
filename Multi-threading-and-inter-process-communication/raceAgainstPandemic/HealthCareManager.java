package raceAgainstPandemic;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import static java.lang.System.exit;

public class HealthCareManager {
    // singleton design pattern
    private static HealthCareManager instance;
    private int availableBeds;
    private int totalNumberOfBeds;
    private int totalInfections = 0;
    private int totalTreatments = 0;
    private int currentlyOutOfICU = 0;
    private final long startTimeMillis = System.currentTimeMillis();
    private FileWriter fileWriter;
    private final String OUTPUT_PATH = "Multi-threading-and-inter-process-communication/raceAgainstPandemic/results/";
    private final String OUTPUT_FILE_NAME = "simulation_data.csv";

    private HealthCareManager(int availableBeds) {
        this.totalNumberOfBeds = availableBeds;
        this.availableBeds = availableBeds;
        try {
            new File(OUTPUT_PATH).mkdirs();
            this.fileWriter = new FileWriter(OUTPUT_PATH + OUTPUT_FILE_NAME);
            fileWriter.write("time,total_beds,in,out,treatments,infections\n");
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    } // intentionally private for compliance with the singleton design pattern

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
            this.writeSnapshotToFile();
            return;
        }
        this.handleTreatments(number);
        this.writeSnapshotToFile();
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

        if (!validTreatment) {
            number = inICU;
        }
        this.totalTreatments += number;
        boolean directOccupancy = this.currentlyOutOfICU >= number;
        if (directOccupancy) {
            this.currentlyOutOfICU -= number;
            return;
        }
        this.availableBeds += number - currentlyOutOfICU;
        this.currentlyOutOfICU = 0;
    }

    private synchronized void writeSnapshotToFile() {
        long timeOffset = System.currentTimeMillis() - this.startTimeMillis;
        try {
            this.fileWriter = new FileWriter(OUTPUT_PATH + OUTPUT_FILE_NAME, true);
            this.fileWriter.write(
                    timeOffset +
                            "," +
                            this.totalNumberOfBeds +
                            "," +
                            this.getCurrentlyInICU() +
                            "," +
                            this.getCurrentlyOutOfICU() +
                            "," +
                            this.getTotalTreatments() +
                            "," +
                            this.getTotalInfections() +
                            "\n"
            );
            this.fileWriter.close();
        } catch (IOException e) {
            System.out.println("An error occurred while trying to access the output data file.");
            e.printStackTrace();
            exit(-1);
        }

    }

    /**
     * Resets all counters and configurations.
     */
    public static void reset() {
        instance = null;
    }

}
