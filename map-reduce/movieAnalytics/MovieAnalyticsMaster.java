package movieAnalytics;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.conf.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.util.*;
import util.Utilities;

// command line arguments: map-reduce/movieAnalytics/input/ map-reduce/movieAnalytics/out/
public class MovieAnalyticsMaster extends Configured implements Tool {
    private static String inputPath;
    private static String outputPath;

    public int run(String[] args) throws Exception {
        //creating a JobConf object and assigning a job name for identification purposes
        JobConf conf = new JobConf(getConf(), MovieAnalyticsMaster.class);
        conf.setJobName("MovieAnalyticsMaster");

        //Setting configuration object with the Data Type of output Key and Value
        conf.setOutputKeyClass(Text.class);
        conf.setOutputValueClass(IntWritable.class);

        //Providing the mapper and reducer class names
        conf.setMapperClass(CSVProcessor.class);
        conf.setReducerClass(AnalyticsEngine.class);

        //the hdfs input and output directory to be fetched from the command line
        FileInputFormat.addInputPath(conf, new Path(inputPath));
        FileOutputFormat.setOutputPath(conf, new Path(outputPath));

        JobClient.runJob(conf);
        return 0;
    }

    public static void main(String[] args) throws Exception {
        // parse CLI arguments
        inputPath = args[0];
        outputPath = args[1];

        // first, completely delete output folder, because if it already exists, Hadoop goes crazy
        Utilities.deleteDirectory(outputPath);
        // this main function will call run method defined above.
        int res = ToolRunner.run(new Configuration(), new MovieAnalyticsMaster(), args);
        System.exit(res);
    }
}