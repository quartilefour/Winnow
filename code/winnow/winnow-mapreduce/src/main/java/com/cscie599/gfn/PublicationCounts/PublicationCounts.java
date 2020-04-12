import java.io.IOException;
import java.util.StringTokenizer;
import java.util.Arrays;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.MultipleOutputs;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import java.util.Map;
import java.util.HashMap;

public class PublicationCounts {

    public static class Mapper1
            extends Mapper<Object, Text, Text, IntWritable>{ 

        public void map(Object key, Text value, Context context) throws IOException, InterruptedException {

            String line = value.toString();
            // get the meshterm:gene separated from the counts
            String [] lineSplit = line.split("\t");

            // get the individual counts first: a is 0, B is 1, C is 2, D is 3
            String [] counts = lineSplit[1].split(" ");
            System.out.println("counts is " + Arrays.toString(counts));

            // get the gene separated from the meshterm
            String [] outKeys = lineSplit[0].split(":");
            System.out.println("outKeys is " + Arrays.toString(outKeys));

            
            // if A write out for both meshterm and gene
            context.write(new Text(outKeys[0] + " meshterm"), new IntWritable(Integer.parseInt(counts[0].split("_")[1])));
            context.write(new Text(outKeys[1] + " gene"), new IntWritable(Integer.parseInt(counts[0].split("_")[1])));
        
        
            // if B, only write out for meshterm
            context.write(new Text(outKeys[0] + " meshterm"), new IntWritable(Integer.parseInt(counts[2].split("_")[1])));
        
            // if C, only write out for gene
            context.write(new Text(outKeys[1] + " gene"), new IntWritable(Integer.parseInt(counts[1].split("_")[1])));
            
        }
    }

    public static class Reducer1
            extends Reducer<Text, IntWritable, Text, IntWritable> {

        private static final String GENES = "GENES";
        private static final String MESHTERMS = "MESHTERMS";
        MultipleOutputs out;

        public void setup(Context context) {
            out = new MultipleOutputs(context);
        }

        public void reduce(Text key, Iterable<IntWritable> values,
                           Context context) throws IOException, InterruptedException {

            // determine the maxium value sent to the reducer:
            // i.e. for a gene_1, we get all possible counts for this gene and keep highest
            String [] newKey = key.toString().split(" ");
            int max = 0;
            
            for (IntWritable val : values) {
                int value = val.get();
                if (max < value) {
                    max = value;
                }
            }

            if (newKey[1].equals("gene")) {
                out.write(GENES, new Text(newKey[0]), new IntWritable(max), "GENES/");
            }
            else {
                out.write(MESHTERMS, new Text(newKey[0]), new IntWritable(max), "MESHTERMS/");   
            }
            
        }


        public void cleanup(Context context) throws IOException, InterruptedException {
            out.close();
        }
    }    
   

    public static void main(String[] args) throws Exception {
        /*
         * First job in a chain
         */
        Configuration conf = new Configuration();
        Job job1 = Job.getInstance(conf, "publication counts");
        job1.setJarByClass(PublicationCounts.class);

        job1.setMapperClass(Mapper1.class);
        job1.setReducerClass(Reducer1.class);

        job1.setOutputKeyClass(Text.class);
        job1.setOutputValueClass(IntWritable.class);
        job1.setMapOutputKeyClass(Text.class);
        job1.setMapOutputValueClass(IntWritable.class);

        job1.setInputFormatClass(TextInputFormat.class);
        FileInputFormat.addInputPath(job1, new Path(args[0]));
        FileOutputFormat.setOutputPath(job1, new Path(args[1]));

        // multiple file output configuration
        MultipleOutputs.addNamedOutput(job1, "GENES", TextOutputFormat.class, Text.class, IntWritable.class);
        MultipleOutputs.addNamedOutput(job1, "MESHTERMS", TextOutputFormat.class, Text.class, IntWritable.class);

        job1.waitForCompletion(true);
    }
}