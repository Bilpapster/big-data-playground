// credits to https://medium.com/javarevisited/how-to-display-progressbar-on-the-standard-console-using-java-18f01d52b30e

import java.util.stream.Stream;

public class ProgressBar extends Thread {

    private final String message;
    private final int length;
    private final long timeInterval;

    public ProgressBar(String message, int length, long timeInterval) {
        this.message = message;
        this.length = length;
        this.timeInterval = timeInterval;
    }

    @Override
    public void run() {
        char incomplete = '░'; // U+2591 Unicode Character
        char complete = '█'; // U+2588 Unicode Character
        StringBuilder builder = new StringBuilder();
        Stream.generate(() -> incomplete).limit(length).forEach(builder::append);
        System.out.println(message);
        for (int i = 0; i < length; i++) {
            builder.replace(i, i + 1, String.valueOf(complete));
            String progressBar = "\r" + builder;
            System.out.print(progressBar);
            try {
                Thread.sleep(timeInterval);
            } catch (InterruptedException ignored) {
            }
        }
        System.out.println();
    }
}
