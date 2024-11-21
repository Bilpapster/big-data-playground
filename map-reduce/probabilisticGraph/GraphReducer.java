package probabilisticGraph;

import java.io.IOException;
import java.util.Map;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.*;

public class GraphReducer extends Reducer<Text, DoubleWritable, Text, DoubleWritable> {
    private int totalCount;
    private double totalSum;

    @Override
    protected void setup(Context context) throws IOException, InterruptedException {
        totalCount = 0;
        totalSum = 0;
    }

    @Override
    public void reduce(Text key, Iterable<DoubleWritable> values, Context context) throws IOException, InterruptedException {
        double sum = 0;
        for (DoubleWritable value : values) {
            sum += value.get();
        }
        totalSum += sum;
        totalCount++;

        context.write(key, new DoubleWritable(sum));
    }

//    @Override
//    protected void cleanup(Context context) throws IOException, InterruptedException {
//        double average = totalSum / totalCount;
//        context.getConfiguration().setStrings("global.average", Double.toString(average));
//    }
}
