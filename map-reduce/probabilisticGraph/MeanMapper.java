package probabilisticGraph;

import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;

public class MeanMapper extends Mapper<LongWritable, Text, Text, DoubleWritable> {
    private static final Text COUNT_KEY = new Text("count");
    private static final Text SUM_KEY = new Text("sum");

    @Override
    public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        context.write(COUNT_KEY, new DoubleWritable(1));
        context.write(SUM_KEY, new DoubleWritable(Double.parseDouble(value.toString().split("\t")[1])));
    }
}