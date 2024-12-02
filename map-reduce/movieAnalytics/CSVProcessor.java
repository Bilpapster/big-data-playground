package movieAnalytics;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reporter;

import java.io.IOException;

public abstract class CSVProcessor extends MapReduceBase implements Mapper<Object, Text, Text, IntWritable> {

    private final static IntWritable one = new IntWritable(1);

    static class Movie {
        private final int ILLEGAL_VALUE = -1;

        private int duration = ILLEGAL_VALUE;
        private String[] involvedCountries = null;
        private String[] genre = null;
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
            this.involvedCountries = this.extractNestedFieldsWithQuotes(involvedCountriesAsString);
            return this;
        }

        public Movie withGenre(String genreAsString) {
            if (genreAsString.isEmpty()) {
                return this;
            }

            if (!genreAsString.startsWith("\"")) {
                this.genre = new String[]{genreAsString};
                return this;
            }
            this.genre = this.extractNestedFieldsWithQuotes(genreAsString);
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

        private String[] extractNestedFieldsWithQuotes(String stringWithQuotes) {
            String stringWithoutQuotes = stringWithQuotes.substring(1, stringWithQuotes.length() - 1); // remove quotes
            String[] nestedFields = stringWithoutQuotes.split(",");
            for (int i = 0; i < nestedFields.length; i++) {
                nestedFields[i] = nestedFields[i].trim();
            }
            return nestedFields;
        }

        public void sendCountriesDurationToCollector(OutputCollector<Text, IntWritable> collector) throws IOException {
            if (this.involvedCountries == null || this.duration == ILLEGAL_VALUE) {
                return;
            }

            for (String involvedCountry : this.involvedCountries) {
                collector.collect(new Text(involvedCountry), new IntWritable(this.duration));
            }
        }

        public void sendGenreYearToCollector(OutputCollector<Text, IntWritable> collector) throws IOException {
            if (this.genre == null || this.year == ILLEGAL_VALUE || this.score <= 8) {
                return;
            }

            for (String individualGenre : this.genre) {
                collector.collect(new Text(this.year + "_" + individualGenre), new IntWritable(1));
            }
        }
    }

    @Override
    public void map(Object key, Text value, OutputCollector<Text, IntWritable> output, Reporter reporter) throws IOException {

        String[] usefulFields = extractUsefulFields(splitCSVLine(value), 9);
        if (usefulFields.length == 0) {
            return;
        }
        this.sendMovieToCollector(
                new Movie().withDuration(usefulFields[0])
                .withInvolvedCountries(usefulFields[1])
                .withGenre(usefulFields[2])
                .withYear(usefulFields[3])
                .withScore(usefulFields[4]),
                output
        );
    }

    public abstract void sendMovieToCollector(Movie movie, OutputCollector<Text, IntWritable> collector) throws IOException;

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
                tokens[3].split(" ")[0],  // duration
                tokens[8],                      // country or countries
                tokens[4],                      // genre
                tokens[2],                      // year
                tokens[6]                       // score
        };
    }
}