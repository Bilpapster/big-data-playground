package numeronyms;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.*;

public class NumeronymReducer extends Reducer<Text, IntWritable, Text, IntWritable> {
    private int k;

    @Override
    protected void setup(Context context) throws IOException, InterruptedException {
        // Retrieve k from the configuration
        Configuration conf = context.getConfiguration();
        k = conf.getInt("numeronyms.k", 0);
    }

    @Override
    public void reduce(Text key, Iterable<IntWritable> values, Context context)
            throws IOException, InterruptedException {

        int sum = 0;
        for (IntWritable value : values) {
            sum += value.get();
        }

        if (sum >= k) {
            context.write(key, new IntWritable(sum));
        }
    }
}
