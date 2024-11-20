package numeronyms;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.conf.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import util.Utilities;

// command line arguments: map-reduce/numeronyms/input/ map-reduce/numeronyms/out/
public class NumeronymsMaster {

    public static void main(String[] args) throws Exception {
        // parse CLI arguments
        String inputPath = args[0];
        String outputPath = args[1];
        Utilities.deleteDirectory(outputPath);
        
        int k = Integer.parseInt(args[2]);
        int minLength = 3;

        Configuration conf = new Configuration();
        conf.setInt("numeronyms.k", k);
        conf.setInt("numeronyms.l", minLength);

        Job job = new Job(conf, "numeronyms");
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);
        job.setMapperClass(NumeronymMapper.class);
        job.setReducerClass(NumeronymReducer.class);
        job.setInputFormatClass(TextInputFormat.class);
        job.setOutputFormatClass(TextOutputFormat.class);
        FileInputFormat.addInputPath(job, new Path(inputPath));
        FileOutputFormat.setOutputPath(job, new Path(outputPath));

        job.waitForCompletion(true);
    }
}