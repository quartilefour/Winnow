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
import java.util.Map;
import java.util.HashMap;

public class GenePublication {

    public static class Mapper1
            extends Mapper<Object, Text, Text, Text>{

        public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
            String line = value.toString();

            context.write(new Text("file"), new Text(line));
        }
    }

    public static class Reducer1
            extends Reducer<Text, Text, Text, Text> {

        public void reduce(Text key, Iterable<Text> values,
                           Context context) throws IOException, InterruptedException {

            // each publication is key, each value is a map of gene_ids
            HashMap<String, HashMap<String, String>> publications = new HashMap<String, HashMap<String, String>>();

            // each key is a human gene_id, each value is a map of orthologs gene_ids (same key, value)
            HashMap<String, HashMap<String, String>> orthologs = new HashMap<String, HashMap<String, String>>();

            // will only hold gene_ids that have tax_id == 9606 (human)
            HashMap<String, String> genes = new HashMap<String, String>();

            HashMap<String, String> humanPub = new HashMap<String, String>();

            // process the input values (build maps)
            for (Text val : values) {
                String inputLine = val.toString();


                String[] input = inputLine.split("\t");

                // only process non-headers
                if (! inputLine.contains("tax_id")) {
                    //System.out.println(Arrays.toString(input));

                    if (input.length == 3) {
                        // process gene2pubmed
                        if (publications.get(input[2]) == null) {
                            publications.put(input[2], new HashMap<String, String>());
                        } 
                        
                        publications.get(input[2]).put(input[1], input[1]);
                        
                        if (input[0].equals("9606")) {
                            humanPub.put(input[1], input[1]);
                        }

                    }
                    else if (input.length == 5) {
                        // process gene_ortholog
                        if (orthologs.get(input[1]) == null) {
                            orthologs.put(input[1], new HashMap<String, String>());
                        } 
                        
                        orthologs.get(input[1]).put(input[4], input[4]);

                    }
                    else if (input.length == 16) {
                        // process gene_info
                        if (input[0].equals("9606")) {
                            // System.out.println("9606 found " + input[1]);
                            genes.put(input[1], input[1]);
                        } 
                    }
                }
            }


            // iterate through each map:
            // identify which gene_id reducer to send everything to
            for (Map.Entry<String, String> geneEntry : genes.entrySet()) {
                // output will be format: "meshterm:gene_Letter"
                String geneId = geneEntry.getKey();
                //System.out.println("the gene id " + geneId);

                for (Map.Entry<String, HashMap<String, String>> pubEntry : publications.entrySet()) {
                   //System.out.println("the pub id " + pubEntry.getKey());
                   //System.out.println("the values " + pubEntry.getValue());
                    if (pubEntry.getValue().get(geneId) != null) {
                       // System.out.println("writing out for gene " + geneId);
                        // the human gene has a publication
                        context.write(new Text("publication:" + pubEntry.getKey()), new Text("gene:" + geneId));
                    }
                    else if (humanPub.get(geneId) == null) {
                        // we need to find publications for the orthologs
                        HashMap<String, String> orthos = orthologs.get(geneId);

                        if (orthos != null) {
                            for (Map.Entry<String, String> orthoEntry : orthos.entrySet()) {
                                if (pubEntry.getValue().get(orthoEntry.getValue()) != null) {
                                    //System.out.println("writing out for orthologs " + geneId);
                                    // write out the publication as if it were for the gene
                                    context.write(new Text("publication:" + pubEntry.getKey()), new Text("gene:" + geneId));
                                }
                            }
                        }    
                    }
                }
            }
        }
    }    
   

    public static void main(String[] args) throws Exception {
        /*
         * First job in a chain
         */
        Configuration conf = new Configuration();
        Job job1 = Job.getInstance(conf, "gene_publication part 1");
        job1.setJarByClass(GenePublication.class);

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
    }
}