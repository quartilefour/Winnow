import java.io.IOException;
import java.util.StringTokenizer;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.lang.StringBuilder;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class FisherTest {

    public static class Mapper1
            extends Mapper<Object, Text, Text, Text>{

        public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
            String line = value.toString();

            // skip headers
            if (! line.contains("publication_id")) {
                StringBuilder input = new StringBuilder("");
                StringBuilder pub = new StringBuilder("");
                StringBuilder newEntry = new StringBuilder("");

                // process the publication from the gene-publication file
                if (line.contains("publication:")) {
                    String [] inputArray = (line.split("\t"));
                    pub.append(inputArray[0].split(":")[1].trim());

                    // track the gene
                    newEntry.append(inputArray[1].split(":")[1].trim());
                }
                else {
                    // process the publication from the meshterm-publication file
                    String [] inputArray = line.split(",");
                    pub.append(inputArray[0].trim());

                    // track the meshterm
                    newEntry.append(inputArray[1].trim());
                }

                // write out the publication to calculate the total
                context.write(new Text("total"), new Text(pub.toString()));

                // write out either the gene or the meshterm with the publicatoin
                context.write(new Text(newEntry.toString()), new Text(pub.toString()));
            }
        }
    }

    public static class Reducer1
            extends Reducer<Text, Text, Text, Text> {

        public void reduce(Text key, Iterable<Text> values,
                           Context context) throws IOException, InterruptedException {
            // if contains 'total'
            if (key.toString().equals("total")) {
                Set<String> publications = new HashSet<String>();

                // get a set of all publications (unique)
                for (Text val : values) {
                    publications.add(val.toString());
                }

                // write out the total count (length of set) to next phase
                context.write(new Text("total_count"), new Text(Integer.toString(publications.size())));
            }
            else {
                // if is meshterm or gene
                StringBuilder allPublications = new StringBuilder("");

                for (Text val : values) {
                    allPublications.append(val.toString());
                    allPublications.append(" ");
                }

                // write out the gene or meshterm and all of the associated publications
                context.write(new Text(key.toString()), new Text(allPublications.toString()));
            }
        }
    }

    public static class Mapper2
            extends Mapper<Object, Text, Text, Text>{

        public void map(Object key, Text value, Context context
        ) throws IOException, InterruptedException {
            String [] line = value.toString().split("\t");
            StringBuilder output = new StringBuilder(line[0]);
            output.append("\t");
            output.append(line[1]);
            context.write(new Text("file"), new Text(output.toString()));
        }
    }

    public static class Reducer2
            extends Reducer<Text, Text, Text, Text> {

        public void reduce(Text key, Iterable<Text> values,
                           Context context
        ) throws IOException, InterruptedException {
            long count = 0;
            List<StringBuilder> meshtermPublications = new LinkedList<StringBuilder>();
            List<StringBuilder> genePublications = new LinkedList<StringBuilder>();

            for (Text val : values) {

                String input = val.toString();
                String [] intputValue = val.toString().split("\t");


                if (input.indexOf("D") == 0){
                    //System.out.println("meshterm found: " + intputValue[0] + " values: " + intputValue[1]);
                    // if input line is for meshterms, add it to the meshterm list
                    StringBuilder meshterm = new StringBuilder(intputValue[0]);
                    meshterm.append(":");
                    meshterm.append(new StringBuilder(intputValue[1].trim()));
                    meshtermPublications.add(meshterm);
                }
                else if (input.indexOf("total_count") == 0) {
                    //System.out.println("count found: " + intputValue[1]);
                    // if input line contains 'total_count'
                    count = Long.parseLong(intputValue[1]);
                }
                else {
                    //System.out.println("gene found: " + intputValue[0] + " values: " + intputValue[1]);
                    // if input line is for genes, add it to the gene list
                    StringBuilder gene = new StringBuilder(intputValue[0]);
                    gene.append(":");
                    gene.append(new StringBuilder(intputValue[1].trim()));
                    genePublications.add(gene);
                }
            }

            int meshListSize = meshtermPublications.size();
            int geneListSize = genePublications.size();
            //System.out.println("genePublications " + Arrays.toString(genePublications.toArray()));
            //System.out.println("meshtermPublications " + Arrays.toString(meshtermPublications.toArray()));

            // output each gene-mesh pair with publications and total count (n)
            for (int i = 0; i < meshListSize; i++) {
                for (int j = 0; j < geneListSize; j++) {

                    String [] meshValues = meshtermPublications.get(i).toString().split(":");
                    String [] geneValues = genePublications.get(j).toString().split(":");

                    StringBuilder outputKey = new StringBuilder(meshValues[0].trim());
                    outputKey.append("_");
                    outputKey.append(geneValues[0].trim());

                    StringBuilder outputValue = new StringBuilder(meshValues[1].trim());
                    outputValue.append(":");
                    outputValue.append(geneValues[1].trim());

                    outputValue.append(":");
                    outputValue.append(Long.toString(count));
                    //System.out.println("outputKey is " + outputKey + " and outputValue is " + outputValue);

                    context.write(new Text(outputKey.toString()), new Text(outputValue.toString()));
                }
            }
        }
    }

    public static class Mapper3
            extends Mapper<Object, Text, Text, Text>{

        public void map(Object key, Text value, Context context
        ) throws IOException, InterruptedException {
            String [] line = value.toString().split("\t");

            // output key:"meshterm:gene_Letter", int
            context.write(new Text(line[0].toString()), new Text(line[1]));
        }
    }

    public static class Reducer3
            extends Reducer<Text, Text, Text, Text> {

        public void reduce(Text key, Iterable<Text> values, Context context)
                throws IOException, InterruptedException {
            //System.out.println("inside reducer3 key is " + key.toString());

            String [] outputArray = key.toString().split("_");
            //System.out.println("outputArray is " + Arrays.toString(outputArray));
            StringBuilder outputKey = new StringBuilder(outputArray[0]);
            outputKey.append(":");
            outputKey.append(outputArray[1]);

            for (Text val : values) {
                Set<String> intersectSet;
                Set<String> meshPubSet;
                Set<String> genePubSet;
                String [] inputLine;

                inputLine = val.toString().split(":");
                //System.out.println("val " + val.toString());
                //System.out.println("inputLine " + Arrays.toString(inputLine));

                List<String> meshPubList = new ArrayList<String>(Arrays.asList(inputLine[0].split(" ")));
                meshPubSet = new HashSet<String>(meshPubList);
                //System.out.println("meshPubSet " + meshPubSet.toString());

                List<String> genePubList = new ArrayList<String>(Arrays.asList(inputLine[1].split(" ")));
                genePubSet = new HashSet<String>(genePubList);
                //System.out.println("genePubSet " + genePubSet.toString());

                Set justGene = new HashSet<String>(genePubSet);
                justGene.removeAll(meshPubSet);
                Set justMesh = new HashSet<String>(meshPubSet);
                justMesh.removeAll(genePubSet);

                intersectSet = new HashSet<String>(genePubSet);
                intersectSet.retainAll(meshPubSet);
                //System.out.println("intersectSet " + intersectSet.toString());

                int A = intersectSet == null ? 0 : intersectSet.size();
                int B = justGene == null ? 0 : justGene.size();
                int C = justMesh == null ? 0 : justMesh.size();

                //System.out.println("sizes: intersect-" + A + " gene-" + B + " mesh-" + C + " total-" + inputLine[2]);
                StringBuilder outputValue = new StringBuilder("A_" + A);
                outputValue.append(" ");
                outputValue.append("B_" + Integer.toString(B));
                outputValue.append(" ");
                outputValue.append("C_" + Integer.toString(C));
                outputValue.append(" ");
                outputValue.append("D_" + (Integer.parseInt(inputLine[2]) - (A + B + C)));

                context.write(new Text(outputKey.toString()), new Text(outputValue.toString()));
            }
        }
    }

    public static class Mapper4
            extends Mapper<Object, Text, Text, Text>{

        public void map(Object key, Text value, Context context
        ) throws IOException, InterruptedException {
            String [] line = value.toString().split("\t");
            //System.out.println("inside mapper4 line is " + Arrays.toString(line));

            context.write(new Text(line[0].toString()), new Text(line[1].toString()));
        }
    }

    public static class Reducer4
            extends Reducer<Text, Text, Text, DoubleWritable> {

        public void reduce(Text key, Iterable<Text> values, Context context)
                throws IOException, InterruptedException {
            long A = 0;
            long B = 0;
            long C = 0;
            long D = 0;

            for (Text val : values) {
                String [] inputSplit = val.toString().split(" ");
                A = Long.parseLong(inputSplit[0].split("_")[1]);
                B = Long.parseLong(inputSplit[1].split("_")[1]);
                C = Long.parseLong(inputSplit[2].split("_")[1]);
                D = Long.parseLong(inputSplit[3].split("_")[1]);
            }

            CalculateFisher fisher = new CalculateFisher(A, B, C, D);
            double result = fisher.calculateFisher();
            String [] keySplit = key.toString().split(":");
            StringBuilder outputKey = new StringBuilder (keySplit[0]);
            outputKey.append("\t");
            outputKey.append(keySplit[1]);
            outputKey.append("\t");
            outputKey.append(A);
            //System.out.println("outputKey is " + outputKey.toString());

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
        job1.setOutputValueClass(Text.class);
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
        job2.setMapOutputValueClass(Text.class);

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
        job3.setOutputValueClass(Text.class);
        job3.setMapOutputKeyClass(Text.class);
        job3.setMapOutputValueClass(Text.class);

        job3.setInputFormatClass(TextInputFormat.class);
        FileInputFormat.addInputPath(job3, new Path(args[2]));
        FileOutputFormat.setOutputPath(job3, new Path(args[3]));

        job3.waitForCompletion(true);

        /*
         * Fourth part of the job: Fisher's exact test
         */
        conf = new Configuration();
        Job job4 = Job.getInstance(conf, "fisher part 4");
        job4.setJarByClass(FisherTest.class);

        job4.setMapperClass(Mapper4.class);
        job4.setReducerClass(Reducer4.class);

        job4.setOutputKeyClass(Text.class);
        job4.setOutputValueClass(DoubleWritable.class);
        job4.setMapOutputKeyClass(Text.class);
        job4.setMapOutputValueClass(Text.class);

        job4.setInputFormatClass(TextInputFormat.class);
        FileInputFormat.addInputPath(job4, new Path(args[3]));
        FileOutputFormat.setOutputPath(job4, new Path(args[4]));

        job4.waitForCompletion(true);
    }
}