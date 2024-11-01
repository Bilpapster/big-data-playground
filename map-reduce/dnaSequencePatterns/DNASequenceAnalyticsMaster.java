package dnaSequencePatterns;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.FileInputFormat;
import org.apache.hadoop.mapred.FileOutputFormat;
import org.apache.hadoop.mapred.JobClient;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import util.Utilities;

// command line arguments: map-reduce/dnaSequencePatterns/input/ map-reduce/dnaSequencePatterns/out/
public class DNASequenceAnalyticsMaster extends Configured implements Tool {
    private static String inputPath;
    private static String outputPath;

    public int run(String[] args) throws Exception {
        //creating a JobConf object and assigning a job name for identification purposes
        JobConf conf = new JobConf(getConf(), DNASequenceAnalyticsMaster.class);
        conf.setJobName("DNASequenceAnalyticsMaster");

        //Setting configuration object with the Data Type of output Key and Value
        conf.setOutputKeyClass(Text.class);
        conf.setOutputValueClass(IntWritable.class);

        //Providing the mapper and reducer class names
        conf.setMapperClass(DNASequenceProcessor.class);
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
        int res = ToolRunner.run(new Configuration(), new DNASequenceAnalyticsMaster(), args);
        System.exit(res);
    }
}