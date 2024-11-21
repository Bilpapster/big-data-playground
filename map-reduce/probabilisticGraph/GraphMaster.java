package probabilisticGraph;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.conf.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import util.Utilities;

// command line arguments: map-reduce/probabilisticGraph/input/ map-reduce/probabilisticGraph/out/
public class GraphMaster {

    public static void main(String[] args) throws Exception {
        // parse CLI arguments
        String inputPath = args[0];
        String outputPath = args[1];

        // Delete previous output if exists
//        Utilities.deleteDirectory("map-reduce/probabilisticGraph/temp/");
        Utilities.deleteDirectory(outputPath);

        double T = Double.parseDouble(args[2]);

        Configuration conf = new Configuration();
        conf.setStrings("probGraph.t", Double.toString(T));

        Job job1 = new Job(conf, "probGraph");
        job1.setOutputKeyClass(Text.class);
        job1.setOutputValueClass(DoubleWritable.class);
        job1.setMapperClass(GraphMapper.class);
        job1.setReducerClass(GraphReducer.class);
        job1.setInputFormatClass(TextInputFormat.class);
        job1.setOutputFormatClass(TextOutputFormat.class);
        FileInputFormat.addInputPath(job1, new Path(inputPath));
        FileOutputFormat.setOutputPath(job1, new Path(outputPath));
//        FileOutputFormat.setOutputPath(job1, new Path("map-reduce/probabilisticGraph/tmp/"));

        job1.waitForCompletion(true);
//        Job job2 = Job.getInstance(conf, "probGraphFiltered");
//        job2.setMapperClass(FilterMapper.class);
//        job2.setReducerClass(Reducer.class); // Identity Reducer
//        job2.setOutputKeyClass(Text.class);
//        job2.setOutputValueClass(DoubleWritable.class);
//        job2.setInputFormatClass(TextInputFormat.class);
//        FileInputFormat.addInputPath(job2, new Path("map-reduce/probabilisticGraph/tmp/"));
//        FileOutputFormat.setOutputPath(job2, new Path(outputPath));
//
//        job2.waitForCompletion(true);
//
//        Utilities.deleteDirectory("map-reduce/probabilisticGraph/tmp/");
    }
}