package numeronyms;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;
import java.util.*;


public class NumeronymMapper extends Mapper<LongWritable, Text, Text, IntWritable> {
    private final static IntWritable one = new IntWritable(1);
    private int minLength;
    private Text word = new Text();

    @Override
    protected void setup(Context context) throws IOException, InterruptedException {
        // Retrieve min length from the configuration
        Configuration conf = context.getConfiguration();
        minLength = conf.getInt("numeronyms.l", 3);
    }
    @Override
    public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        String line = value.toString();
        StringTokenizer tokenizer = new StringTokenizer(line);
        while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken();
            // Remove punctuation from the token
            String cleanedWord = token.replaceAll("[^a-zA-Z]", "");

            // If the word is not empty after removing punctuation
            if (!cleanedWord.isEmpty()) {
                // Convert the word to lowercase and get its length
                String sWord = cleanedWord.toLowerCase();
                int length = sWord.length();

                // Only process the word if its length is greater than or equal to the minimum length
                if (length >= minLength) {
                    Text numeronym = new Text();
                    numeronym.set(sWord.substring(0, 1) + length + sWord.substring(length - 1));
                    context.write(numeronym, one);
                }
            }
        }
    }
}
