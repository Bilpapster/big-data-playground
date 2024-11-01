package movieAnalytics;

import java.io.IOException;
import java.util.Iterator;

import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;

public class AnalyticsEngine extends MapReduceBase implements Reducer<Text, IntWritable, Text, IntWritable> {
    private final IntWritable result = new IntWritable();

    @Override
    public void reduce(Text key, Iterator<IntWritable> values, OutputCollector<Text, IntWritable> output, Reporter reporter) throws IOException {
        int sum = 0;
        for (Iterator<IntWritable> it = values; it.hasNext(); ) {
            IntWritable val = it.next();
            sum += val.get();
        }
        result.set(sum);
        output.collect(key, result);
    }
}