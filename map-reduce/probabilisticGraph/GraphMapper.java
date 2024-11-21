package probabilisticGraph;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;

public class GraphMapper extends Mapper<LongWritable, Text, Text, DoubleWritable> {

    private Double T;
    @Override
    protected void setup(Context context) throws IOException, InterruptedException {
        // Retrieve threshold T from the configuration
        Configuration conf = context.getConfiguration();
        T = Double.parseDouble(conf.get("probGraph.t", "0"));
    }
    @Override
    public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        String line = value.toString();
        Text v1 = new Text(line.split(" ")[0]);
        Text v2 = new Text(line.split(" ")[1]);
        DoubleWritable prob = new DoubleWritable(Double.parseDouble(line.split(" ")[2]));
        if (prob.get() >= T) {
            context.write(v1, prob);
            context.write(v2, prob);
        }
    }
}
