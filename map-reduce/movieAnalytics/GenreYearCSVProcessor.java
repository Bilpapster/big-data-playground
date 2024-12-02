package movieAnalytics;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.OutputCollector;

import java.io.IOException;

public class GenreYearCSVProcessor extends CSVProcessor {
    @Override
    public void sendMovieToCollector(CSVProcessor.Movie movie, OutputCollector<Text, IntWritable> collector) throws IOException {
        movie.sendGenreYearToCollector(collector);
    }
}
