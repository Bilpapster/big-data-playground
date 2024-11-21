package probabilisticGraph;

import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;

public class MeanReducer extends Reducer<Text, DoubleWritable, Text, DoubleWritable> {
    @Override
    public void reduce(Text key, Iterable<DoubleWritable> values, Context context) throws IOException, InterruptedException {
        double sum = 0;
        int count = 0;

        for (DoubleWritable value : values) {
            sum += value.get();
            count++;
        }

        if (key.toString().equals("sum")) {
            context.write(new Text("sum"), new DoubleWritable(sum));
        } else if (key.toString().equals("count")) {
            context.write(new Text("count"), new DoubleWritable(count));
        }
    }
}