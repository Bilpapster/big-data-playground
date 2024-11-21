package probabilisticGraph;

import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;

public class MeanMapper extends Mapper<LongWritable, Text, Text, DoubleWritable> {
    @Override
    public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        context.write(new Text("count"), new DoubleWritable(1));
        context.write(new Text("sum"), new DoubleWritable(Double.parseDouble(value.toString().split("\t")[1])));
    }
}
