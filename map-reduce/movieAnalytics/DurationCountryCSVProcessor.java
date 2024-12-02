package movieAnalytics;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.OutputCollector;

import java.io.IOException;

public class DurationCountryCSVProcessor extends CSVProcessor {
    @Override
    public void sendMovieToCollector(Movie movie, OutputCollector<Text, IntWritable> collector) throws IOException {
        movie.sendCountriesDurationToCollector(collector);
    }
}
