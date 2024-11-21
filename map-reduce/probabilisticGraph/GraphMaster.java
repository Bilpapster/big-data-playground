package probabilisticGraph;

import org.apache.hadoop.fs.*;
import org.apache.hadoop.conf.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import util.Utilities;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

// command line arguments: map-reduce/probabilisticGraph/input/ map-reduce/probabilisticGraph/out/
public class GraphMaster {

    public static void main(String[] args) throws Exception {
        // parse CLI arguments
        String inputPath = args[0];
        String outputPath = args[1];

        if (!inputPath.endsWith("/"))
            inputPath += "/";
        if (!outputPath.endsWith("/"))
            outputPath += "/";

        // Delete previous output if exists
        Utilities.deleteDirectory(outputPath);

        double T = Double.parseDouble(args[2]);

        Configuration conf = new Configuration();
        conf.set("probGraph.t", Double.toString(T));

        // Find degrees
        Job job1 = new Job(conf, "findGraphDegrees");
        job1.setOutputKeyClass(Text.class);
        job1.setOutputValueClass(DoubleWritable.class);
        job1.setMapperClass(GraphMapper.class);
        job1.setReducerClass(GraphReducer.class);
        job1.setInputFormatClass(TextInputFormat.class);
        job1.setOutputFormatClass(TextOutputFormat.class);
        FileInputFormat.addInputPath(job1, new Path(inputPath));
        FileOutputFormat.setOutputPath(job1, new Path(outputPath + "phase1/"));
        job1.waitForCompletion(true);

        // Find mean degree
        Job job2 = new Job(conf, "findMeanDegree");
        job2.setOutputKeyClass(Text.class);
        job2.setOutputValueClass(DoubleWritable.class);
        job2.setMapperClass(MeanMapper.class);
        job2.setReducerClass(MeanReducer.class);
        job2.setInputFormatClass(TextInputFormat.class);
        job2.setOutputFormatClass(TextOutputFormat.class);
        FileInputFormat.addInputPath(job2, new Path(outputPath + "phase1/"));
        FileOutputFormat.setOutputPath(job2, new Path(outputPath + "phase2/"));
        job2.waitForCompletion(true);

        // Filter vertices on mean degree
        double mean = getMean(outputPath);
        System.out.println(mean);
        conf.set("filter.mean", Double.toString(mean));

        // Run the third MapReduce job
        Job job3 = Job.getInstance(conf, "filterByMean");
        job3.setOutputKeyClass(Text.class);
        job3.setOutputValueClass(DoubleWritable.class);
        job3.setMapperClass(FilterMapper.class);
        job3.setReducerClass(FilterReducer.class);

        job3.setInputFormatClass(TextInputFormat.class);
        job3.setOutputFormatClass(TextOutputFormat.class);
        FileInputFormat.addInputPath(job3, new Path(outputPath + "phase1/"));
        FileOutputFormat.setOutputPath(job3, new Path(outputPath + "phase3/"));
        job3.waitForCompletion(true);

        Utilities.moveDirectoryContents(outputPath + "phase3", outputPath);
        Utilities.deleteDirectory(outputPath + "phase1/");
        Utilities.deleteDirectory(outputPath + "phase2/");
        Utilities.deleteDirectory(outputPath + "phase3/");
    }

    private static double getMean(String outputPath) throws IOException {
        double mean = 0;
        int count = 0;

        // Access the file system and list the files in the directory
        Configuration conf = new Configuration();
        Path phase2Path = new Path(outputPath + "phase2/");
        FileSystem fs = FileSystem.get(conf);

        // Iterate over files in the phase2 directory
        for (Path file : Utilities.getFilesInDirectory(fs, phase2Path)) {
            if (!fs.isFile(file)) continue; // Skip non-file entries

            // Open each file and parse its contents
            try (BufferedReader br = new BufferedReader(new InputStreamReader(fs.open(file)))) {
                String line;
                while ((line = br.readLine()) != null) {
                    String[] parts = line.split("\t");
                    if (parts[0].equals("sum")) {
                        mean += Double.parseDouble(parts[1]);
                    } else if (parts[0].equals("count")) {
                        count += (int) Double.parseDouble(parts[1]);
                    }
                }
            }
        }

        if (count == 0) {
            throw new IOException("Count is zero, mean calculation not possible.");
        }

        mean /= count; // Calculate the mean
        return mean;
    }

}