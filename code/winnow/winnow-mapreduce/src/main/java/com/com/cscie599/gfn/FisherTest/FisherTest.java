import java.io.IOException;
import java.util.StringTokenizer;

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
import java.util.Map;
import java.util.HashMap;
import java.lang.StringBuilder;

public class FisherTest {

    public static class Mapper1
            extends Mapper<Object, Text, Text, Text>{

        public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
            String line = value.toString();
            if (! line.contains("publication_id")) {
                context.write(new Text("file"), new Text(line));
            }
        }
    }

    public static class Reducer1
            extends Reducer<Text, Text, Text, IntWritable> {

        public void reduce(Text key, Iterable<Text> values,
                           Context context) throws IOException, InterruptedException {

            // hold all the individual publications, meshterms, and genes to iterate through
            HashMap<String, String> publications = new HashMap<String, String>();
            HashMap<String, HashMap<String, String>> meshterms = new HashMap <String, HashMap<String, String>>();
            HashMap<String, HashMap<String, String>> genes = new HashMap <String, HashMap<String, String>>();

            // process the input values (build maps)
            for (Text val : values) {
                String inputLine = val.toString().trim();
                String [] input = null;
                String pub = "";
                String newEntry = "";

                if (inputLine.contains("publication:")) {
                    input = inputLine.split("\t");

                    pub = input[0].split(":")[1].trim();
                    publications.put(pub, pub);

                    // either the meshterm or publication
                    newEntry = input[1].split(":")[1].trim();
                }
                else {
                    input = inputLine.split(",");

                    pub = input[0].trim();
                    publications.put(pub, pub);

                    // either the meshterm or publication
                    newEntry = input[1];
                }
                
                
                if (inputLine.contains("D")) {
                    // meshterm is not being already tracked
                    if (meshterms.get(newEntry) == null) {

                        // make an entry with publication appended to new map
                        meshterms.put(newEntry, new HashMap<String, String>());
                    }

                    // add the publication to that meshterm's map
                    meshterms.get(newEntry).put(pub, pub);
                }
                else {

                    // gene is not being already tracked
                    if (genes.get(newEntry) == null) {

                        // make an entry with publication appended to new map
                        genes.put(newEntry, new HashMap<String, String>());
                    }

                    // add the publication to that gene's map
                    genes.get(newEntry).put(pub, pub);

                }

            }

            // iterate through each map:
            // identify 4 publication categories for each meshterm_gene combo
            int pubSize = publications.size();
            int meshSize = meshterms.size();
            int geneSize = genes.size();
            for (Map.Entry<String, String> pubEntry : publications.entrySet()) {
                for (Map.Entry<String, HashMap<String, String>> meshPubEntry : meshterms.entrySet()) {
                    for (Map.Entry<String, HashMap<String, String>> genePubEntry : genes.entrySet()) {
                        // output will be format: "meshterm:gene_Letter"
                        StringBuilder outputKey = meshPubEntry.getKey();
                        outputkey.append(":");
                        outputkey.append(genePubEntry.getKey());
                        //System.out.println("publication: " + pubEntry.getKey());
                        //System.out.println("gene: " + genePubEntry.getKey());
                        //System.out.println("mesh: " + meshPubEntry.getKey());

                        if (genePubEntry.getValue().get(pubEntry.getValue()) != null) {

                            if (meshPubEntry.getValue().get(pubEntry.getValue()) != null) {
                                // publication is associated with both meshterm and gene
                                outputKey.append("_A");
                                context.write(new Text(outputKey.toString()), new IntWritable(1));
                            }
                            else {
                                // publication is for gene only
                                outputKey.append("_C");
                                context.write(new Text(outputKey.toString()), new IntWritable(1));
                            }
                        }
                        else if (meshPubEntry.getValue().get(pubEntry.getValue()) != null) {
                            // publication is for mesh only
                            outputKey.append("_B");
                            context.write(new Text(outputKey.toString()), new IntWritable(1));
                        }
                        else {
                            // publication is for neither
                            outputKey.append("_D");
                            context.write(new Text(outputKey.toString()), new IntWritable(1));
                        }
                    }
                }
            }
        }
    }

    public static class Mapper2
            extends Mapper<Object, Text, Text, IntWritable>{

        public void map(Object key, Text value, Context context
        ) throws IOException, InterruptedException {
            String [] line = value.toString().split("\t");

            // output key:"meshterm:gene_Letter", int
            context.write(new Text(line[0].toString()), new IntWritable(Integer.parseInt(line[1])));
        }
    }

    public static class Reducer2
            extends Reducer<Text, IntWritable, Text, Text> {
        private IntWritable result = new IntWritable();

        public void reduce(Text key, Iterable<IntWritable> values,
                           Context context
        ) throws IOException, InterruptedException {
            long sum = 0;

            for (IntWritable val : values) {
                sum += val.get();
            }

            String [] keySplit = key.toString().split("_");
            StringBuilder outputKey = new StringBuilder (keySplit[0]);
            StringBuilder outputValue = new StringBuilder(Long.toString(sum));
            outputValue.append("_");
            outputValue.append(keySplit[1]);

            // output: "meshterm:gene", "int_Letter" (i.e. 3_A)
            context.write(new Text(outputKey.toString()), new Text(outputValue.toString()));
        }
    }

    public static class Mapper3
            extends Mapper<Object, Text, Text, Text>{

        public void map(Object key, Text value, Context context
        ) throws IOException, InterruptedException {
            String [] line = value.toString().split("\t");

            context.write(new Text(line[0].toString()), new Text(line[1].toString()));
        }
    }

    public static class Reducer3
            extends Reducer<Text, Text, Text, DoubleWritable> {
        
        public void reduce(Text key, Iterable<Text> values, Context context)
                throws IOException, InterruptedException {
            long A = 0;
            long B = 0;
            long C = 0;
            long D = 0;

            for (Text val : values) {
                String [] inputSplit = val.toString().split("_");
                if (inputSplit[1].equals("A")) {
                    A = Long.parseLong(inputSplit[0]);
                }
                else if (inputSplit[1].equals("B")) {
                    B = Long.parseLong(inputSplit[0]);
                }
                else if (inputSplit[1].equals("C")) {
                    C = Long.parseLong(inputSplit[0]);
                }
                else {
                    D = Long.parseLong(inputSplit[0]);
                }
            }

            CalculateFisher fisher = new CalculateFisher(A, B, C, D);
            double result = fisher.calculateFisher();
            String [] keySplit = key.toString().split(":");
            StringBuilder outputKey = new StringBuilder (keySplit[0]); 
            outputKey.append("\t");
            outputKey.append(keySplit[1]);
            outputKey.append("\t");
            outputKey.append(A);
            
            context.write(new Text(outputKey.toString()), new DoubleWritable(result));
        }

         public class CalculateFisher {
            private long A;
            private long B;
            private long C;
            private long D;

            public CalculateFisher () {
                this.A = 0;
                this.B = 0;
                this.C = 0;
                this.D = 0;
            }

            public CalculateFisher (long A, long B, long C, long D) {
                this.A = A;
                this.B = B;
                this.C = C;
                this.D = D;
            }

            public double calculateFisher () {
                long numerator = 0;
                long denominator = 0;
                long total = this.A + this.B + this.C + this.D;

                long component1 = factorial(this.A + this.B);
                //System.out.println("component1 " + component1);
                long component2 = factorial(this.C + this.D);
                //System.out.println("component2 " + component2);
                long component3 = factorial(this.A + this.C);
                //System.out.println("component3 " + component3);
                long component4 = factorial(this.B + this.D);
                //System.out.println("component4 " + component4);

                numerator = component1 * component2 * component3 * component4;

                denominator = (factorial(this.A) * factorial(this.B) *
                        factorial(this.C) * factorial(this.D) * factorial(total));

                double result = ((double) numerator/denominator);
                //System.out.println("results is " + result);
               // System.out.println("numerator is " + numerator + " and denominator is " + denominator);
                return result;
            }

            public long factorial (long value) {
                //System.out.println("inside factorial value is " + value);
                if (value <= 0) {
                    return 1;
                }
                return value * factorial(value - 1);
            }
        }
    }

   

    public static void main(String[] args) throws Exception {
        /*
         * First job in a chain
         */
        Configuration conf = new Configuration();
        Job job1 = Job.getInstance(conf, "fisher part 1");
        job1.setJarByClass(FisherTest.class);

        job1.setMapperClass(Mapper1.class);
        job1.setReducerClass(Reducer1.class);

        job1.setOutputKeyClass(Text.class);
        job1.setOutputValueClass(IntWritable.class);
        job1.setMapOutputKeyClass(Text.class);
        job1.setMapOutputValueClass(Text.class);

        job1.setInputFormatClass(TextInputFormat.class);
        FileInputFormat.addInputPath(job1, new Path(args[0]));
        FileOutputFormat.setOutputPath(job1, new Path(args[1]));

        job1.waitForCompletion(true);


        /*
         * Second job in a chain of two jobs
         */
        conf = new Configuration();
        Job job2 = Job.getInstance(conf, "fisher part 2");
        job2.setJarByClass(FisherTest.class);

        job2.setMapperClass(Mapper2.class);
        job2.setReducerClass(Reducer2.class);

        job2.setOutputKeyClass(Text.class);
        job2.setOutputValueClass(Text.class);
        job2.setMapOutputKeyClass(Text.class);
        job2.setMapOutputValueClass(IntWritable.class);

        job2.setInputFormatClass(TextInputFormat.class);
        FileInputFormat.addInputPath(job2, new Path(args[1]));
        FileOutputFormat.setOutputPath(job2, new Path(args[2]));

        job2.waitForCompletion(true);

        /*
         * Third part of the job: Fisher's exact test
         */
        conf = new Configuration();
        Job job3 = Job.getInstance(conf, "fisher part 3");
        job3.setJarByClass(FisherTest.class);

        job3.setMapperClass(Mapper3.class);
        job3.setReducerClass(Reducer3.class);

        job3.setOutputKeyClass(Text.class);
        job3.setOutputValueClass(DoubleWritable.class);
        job3.setMapOutputKeyClass(Text.class);
        job3.setMapOutputValueClass(Text.class);

        job3.setInputFormatClass(TextInputFormat.class);
        FileInputFormat.addInputPath(job3, new Path(args[2]));
        FileOutputFormat.setOutputPath(job3, new Path(args[3]));

        job3.waitForCompletion(true);
    }
}