package movieAnalytics;

import java.io.File;
import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import util.Utilities;

// command line arguments: map-reduce/movieAnalytics/input/ map-reduce/movieAnalytics/out/
public class WordCount {

    public static class TokenizerMapper
            extends Mapper<Object, Text, Text, IntWritable> {

        private final static IntWritable one = new IntWritable(1);
        private Text word = new Text();

        public void map(Object key, Text value, Context context
        ) throws IOException, InterruptedException {

            String line = value.toString();
            String[] countries = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1); // to parse correctly nested quotes with commas inside
            for (String token : countries) {
                if (!token.startsWith("\"") || token.length() < 3) {
//                    System.out.println(token);
                    context.write(new Text(token), one);
                    continue;
                }

                token = token.substring(1, token.length() - 1); // trim leading and trailing quotation marks
                String[] nestedTokens = token.split(",");
                for (String nestedToken : nestedTokens) {
                    nestedToken = nestedToken.trim();  // trim any possible leading and trailing whitespaces
//                    System.out.print(nestedToken);
//                    System.out.print(" and ");
                }
//                System.out.println();
            }
        }
    }

    public static class IntSumReducer
            extends Reducer<Text, IntWritable, Text, IntWritable> {
        private IntWritable result = new IntWritable();

        public void reduce(Text key, Iterable<IntWritable> values,
                           Context context
        ) throws IOException, InterruptedException {
            int sum = 0;
            for (IntWritable val : values) {
                sum += val.get();
            }
            result.set(sum);
            context.write(key, result);
        }
    }

    public static void main(String[] args) throws Exception {
        // parse CLI arguments
        String inputPath = args[0];
        String outputPath = args[1];

        // first, completely delete output folder, because if it already exists, Hadoop goes crazy
        Utilities.deleteDirectory(outputPath);

        // then, configure Hadoop
        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf, "word count");
        job.setJarByClass(WordCount.class);
        job.setMapperClass(TokenizerMapper.class);
        job.setCombinerClass(IntSumReducer.class);
        job.setReducerClass(IntSumReducer.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);
        FileInputFormat.addInputPath(job, new Path(inputPath));
        FileOutputFormat.setOutputPath(job, new Path(outputPath));
        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}