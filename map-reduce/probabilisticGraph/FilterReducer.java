package probabilisticGraph;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;

public class FilterReducer extends Reducer<Text, DoubleWritable, Text, DoubleWritable> {
    private double mean;

    @Override
    protected void setup(Context context) {
        // Retrieve k from the configuration
        Configuration conf = context.getConfiguration();
        mean = Double.parseDouble(conf.get("filter.mean", "0"));
    }

    @Override
    public void reduce(Text key, Iterable<DoubleWritable> values, Context context)
            throws IOException, InterruptedException {

        double value = values.iterator().next().get();

        if (value >= mean) {
            context.write(key, new DoubleWritable(value));
        }
    }
}
