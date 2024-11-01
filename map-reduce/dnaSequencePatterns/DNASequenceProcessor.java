package dnaSequencePatterns;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reporter;

import java.io.IOException;

public class DNASequenceProcessor extends MapReduceBase implements Mapper<Object, Text, Text, IntWritable> {

    private final IntWritable ONE = new IntWritable(1);

    @Override
    public void map(Object key, Text value, OutputCollector<Text, IntWritable> output, Reporter reporter) throws IOException {
        String line = value.toString();
        for (int subsequenceLength = 2; subsequenceLength <= 4; subsequenceLength++) {
            for (int slidingWindowStart = 0; slidingWindowStart + subsequenceLength < line.length(); slidingWindowStart++) {
                output.collect(new Text(line.substring(slidingWindowStart, slidingWindowStart + subsequenceLength)), ONE);
            }
        }
    }
}