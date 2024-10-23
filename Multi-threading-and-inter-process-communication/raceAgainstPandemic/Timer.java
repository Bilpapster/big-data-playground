package raceAgainstPandemic;

public class Timer {
    private boolean running = true;

    public boolean isRunning() {
        return this.running;
    }

    public void stop() {
        this.running = false;
    }
}
