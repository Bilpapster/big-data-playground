package movieAnalytics;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reporter;

import java.io.IOException;

public class CSVProcessor extends MapReduceBase implements Mapper<Object, Text, Text, IntWritable> {

    private final static IntWritable one = new IntWritable(1);

    private static class Movie {
        private final int ILLEGAL_VALUE = -1;

        private int duration = ILLEGAL_VALUE;
        private String[] involvedCountries = null;
        private String genre = null;
        private int year = ILLEGAL_VALUE;
        private int score = ILLEGAL_VALUE;

        public Movie withDuration(String durationAsString) {
            try {
                this.duration = Integer.parseInt(durationAsString);
            } catch (NumberFormatException ignored) {
            }
            return this;
        }

        public Movie withInvolvedCountries(String involvedCountriesAsString) {
            if (involvedCountriesAsString.isEmpty()) {
                return this;
            }

            if (!involvedCountriesAsString.startsWith("\"")) {
                this.involvedCountries = new String[]{involvedCountriesAsString};
                return this;
            }

            involvedCountriesAsString = involvedCountriesAsString.substring(1, involvedCountriesAsString.length() - 1); // remove quotes
            this.involvedCountries = involvedCountriesAsString.split(",");
            for (int i = 0; i < this.involvedCountries.length; i++) {
                this.involvedCountries[i] = this.involvedCountries[i].trim();
            }
            return this;
        }

        public Movie withGenre(String genre) {
            if (!genre.isEmpty()) {
                this.genre = genre;
            }
            return this;
        }

        public Movie withYear(String yearAsString) {
            try {
                this.year = Integer.parseInt(yearAsString);
            } catch (NumberFormatException ignored) {
            }
            return this;
        }

        public Movie withScore(String scoreAsString) {
            try {
                this.score = Integer.parseInt(scoreAsString);
            } catch (NumberFormatException ignored) {
            }
            return this;
        }

        public void sendToCollector(OutputCollector<Text, IntWritable> collector) throws IOException {
            this.sendCountriesDurationToCollector(collector);
//            this.sendGenreYearToCollector(collector);
        }

        private void sendCountriesDurationToCollector(OutputCollector<Text, IntWritable> collector) throws IOException {
            if (this.involvedCountries == null || this.duration == ILLEGAL_VALUE) {
                return;
            }

            for (String involvedCountry : this.involvedCountries) {
                collector.collect(new Text(involvedCountry), new IntWritable(this.duration));
            }
        }

        private void sendGenreYearToCollector(OutputCollector<Text, IntWritable> collector) throws IOException {
            if (this.genre == null || this.year == ILLEGAL_VALUE) {
                return;
            }
            collector.collect(new Text(this.genre + "_" + this.year), new IntWritable(this.score));
        }

    }

    @Override
    public void map(Object key, Text value, OutputCollector<Text, IntWritable> output, Reporter reporter) throws IOException {

        String[] usefulFields = extractUsefulFields(splitCSVLine(value), 22);
        if (usefulFields.length == 0) {
            return;
        }
        new Movie().withDuration(usefulFields[0])
                .withInvolvedCountries(usefulFields[1])
                .withGenre(usefulFields[2])
                .withYear(usefulFields[3])
                .withScore(usefulFields[4])
                .sendToCollector(output);
    }

    private String[] splitCSVLine(Text value) {
        String line = value.toString();
        // "spaghetti" regex is used to parse correctly nested quotes with commas inside
        return line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
    }

    private String[] extractUsefulFields(String[] tokens, int expectedNumberOfFields) {
        if (tokens.length != expectedNumberOfFields) {
            System.out.println("Invalid number of fields " + tokens.length);
            return new String[0];
        }
        return new String[]{
                tokens[6], // duration
                tokens[7], // country or countries
                tokens[5], // genre
                tokens[3], // year
                tokens[3]  // score
        };
    }
}