package com.cscie599.gfn.generator;

import com.cscie599.gfn.importer.analyzer.GeneMeshPub;
import com.cscie599.gfn.importer.analyzer.GeneRawStats;
import com.cscie599.gfn.importer.analyzer.MeshtermRawStats;
import com.google.common.annotations.VisibleForTesting;
import de.siegmar.fastcsv.writer.CsvAppender;
import de.siegmar.fastcsv.writer.CsvWriter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.numbers.gamma.RegularizedGamma;
import org.javatuples.Pair;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.zip.GZIPOutputStream;

/**
 * An implementation of {@java.lang.Runnable} that is used to compute chi squared tests between a given set of lists of genes and meshterms
 *
 * @author PulkitBhanot
 */
public class ChiSquaredRunnable implements Runnable {

    /**
     * Support lower bound.
     */
    public static final double SUPPORT_LO = 0;
    /**
     * Support upper bound.
     */
    public static final double SUPPORT_HI = Double.POSITIVE_INFINITY;

    protected static final Log logger = LogFactory.getLog(ChiSquaredRunnable.class);

    private final Pair<List<GeneRawStats>, List<MeshtermRawStats>> processPairs;

    // Latch to be notified when the processing across all the pairs allocated to this thread finishes.
    private final CountDownLatch countDownLatch;
    //Index of this thread across other threads. This is also used for the name of the file.
    private final int index;
    // Property to determine if pairs with 0 publications should also have chi-squared tests run and p-value's generated and saved in DB.
    private final boolean includePairsWith0Publications;
    // Reference to the all the gene mesh pairs with the counts of publications in common between them.
    private final Map<String, GeneMeshPub> cachedGeneMeshPubStats;
    // Output file where the chi-squared output should be stored.
    private final File outputFile;
    // CSV writer used to write to outputFile above
    private final CsvWriter csvWriter;
    // Decimal format for properly writing the double value and not using the inbuilt string representation that produces values having e.
    private final DecimalFormat df = new DecimalFormat("#");
    // Output file for the zipped file
    private final File outputZippedFile;

    public ChiSquaredRunnable(Pair<List<GeneRawStats>, List<MeshtermRawStats>> processPairs, CountDownLatch countDownLatch, int index, boolean includePairsWith0Publications, Map<String, GeneMeshPub> cachedGeneMeshPubStats, String outputDirectory) {
        this.processPairs = processPairs;
        this.countDownLatch = countDownLatch;
        this.index = index;
        this.includePairsWith0Publications = includePairsWith0Publications;
        this.cachedGeneMeshPubStats = cachedGeneMeshPubStats;
        this.outputFile = new File(outputDirectory + File.separator + "" + index + ".csv");
        this.outputZippedFile = new File(outputDirectory + File.separator + "" + index + ".csv.gz");
        this.csvWriter = new CsvWriter();
        df.setMaximumFractionDigits(20);
    }

    @Override
    public void run() {
        try {
            try (CsvAppender csvAppender = csvWriter.append(this.outputFile, StandardCharsets.UTF_8)) {

                logger.info("Size of genes to process " + processPairs.getValue0().size() + ". Size of meshterms to process " + processPairs.getValue1().size());
                long startTime = System.currentTimeMillis();
                processPairs.getValue0().forEach(geneRawStat -> {
                    processPairs.getValue1().forEach(meshtermRawStat -> {
                        if (logger.isDebugEnabled()) {
                            logger.debug("processing gene " + geneRawStat + " with mesh " + meshtermRawStat);
                        }
                        String lookupKey = new StringBuilder(geneRawStat.getGeneId()).append("-").append(meshtermRawStat.getMeshId()).toString();
                        GeneMeshPub geneMeshPub = cachedGeneMeshPubStats.get(lookupKey);
                        if (geneMeshPub != null || includePairsWith0Publications) {

                            Long row0col0 = geneMeshPub != null ? geneMeshPub.getCounter().get() : 0;
                            Long row0col1 = geneRawStat.getPublicationsWithGene() - row0col0;
                            Long row1col0 = meshtermRawStat.getPublicationsWithTerm() - row0col0;

                            Long row1col1 = (geneRawStat.getPublicationsWithGene() + geneRawStat.getPublicationsWithoutGene()) - row0col0 - row0col1 - row1col0;

                            // Additional validation to make sure we do not end up with any bad datasets
                            if (row0col0 < 0 || row0col1 < 0 || row1col0 < 0 || row1col1 < 0) {
                                logger.error("Invalid case found for row1col1 with " + geneRawStat + " --- " + meshtermRawStat + "------" + geneMeshPub + "," + row0col0 + "," + row0col1 + "," + row1col0 + "," + row1col1);
                            }
                            Pair<Double, Double> returnedPair = chiSquare(row0col0, row0col1, row1col0, row1col1);

                            try {
                                csvAppender.appendField(geneRawStat.getGeneId());
                                csvAppender.appendField(meshtermRawStat.getMeshId());
                                csvAppender.appendField(row0col0.toString());
                                csvAppender.appendField(row0col1.toString());
                                csvAppender.appendField(row1col0.toString());
                                csvAppender.appendField(row1col1.toString());
                                csvAppender.appendField(df.format(returnedPair.getValue0()));
                                csvAppender.appendField(df.format(returnedPair.getValue1()));
                                csvAppender.endLine();
                            } catch (IOException e) {
                                logger.error("Error writing to csv", e);
                            }
                        }
                    });
                });
                logger.info("Finished processing for index " + index + " timetaken " + (System.currentTimeMillis() - startTime));
            }
            zipOutputFile();
            logger.info("Output file zipped successfully");
        } catch (Exception ex) {
            logger.error("Unable to write to csv ", ex);
        } finally {
            countDownLatch.countDown();
        }
    }

    // this test only exists here for testing, there is a more optimized version overloaded version of this below which we use in the test.
    public static Pair<Double, Double> chiSquare(final long[][] counts) {
        int nRows = counts.length; //2
        int nCols = counts[0].length; //2
        // compute row, column and total sums
        double[] rowSum = new double[nRows];
        double[] colSum = new double[nCols];
        double total = 0.0d;
        for (int row = 0; row < nRows; row++) {
            for (int col = 0; col < nCols; col++) {
                rowSum[row] += counts[row][col];
                colSum[col] += counts[row][col];
                total += counts[row][col];
            }
        }
        // compute expected counts and chi-square
        double sumSq = 0.0d;
        double expected = 0.0d;
        for (int row = 0; row < nRows; row++) {
            for (int col = 0; col < nCols; col++) {
                expected = (rowSum[row] * colSum[col]) / total;

                sumSq += ((counts[row][col] - expected) *
                        (counts[row][col] - expected)) / expected;
            }
        }
        double df = ((double) counts.length - 1) * ((double) counts[0].length - 1);
        // The following code is directly using GammaDistribution which is internally being used by ChiSquaredDistribution here
        //https://github.com/apache/commons-statistics/blob/master/commons-statistics-distribution/src/main/java/org/apache/commons/statistics/distribution/ChiSquaredDistribution.java
        //GammaDistribution gamma = new GammaDistribution(df / 2, 2);
        double pval = 1 - cumulativeProbability(sumSq, df / 2, 2);
        return new Pair<Double, Double>(sumSq, pval);
    }

    /**
     * Function to perform the chi-squared test analysis and return the sum squared and p-value. Given the scenario only has 2x2 matrix,
     * we have flattened out the calculation to avoid unnecessary creation of intermediate objects and scale the execution.
     * <p>
     * Assumed there are total 100 publications, with Gene A occurring in 5, Mesh B occurring in 10,
     * and there are 2 publications having Gene A and Mesh B together.
     * <p>
     * |    Mesh B    |  Not Mesh B  |
     * |--------------|--------------|
     * Gene A      |      2(A)    |      3(B)    |
     * Not Gene A  |      8(C)    |     87(D)    |
     * |--------------|--------------|
     *
     * @param row0col0 Number of publications that have both the gene A and meshterm B referenced in it. Cell A above
     * @param row0col1 Number of publications having referenced gene A but not meshterm B. Cell B above
     * @param row1col0 Number of publications having referenced meshterm B but not gene A. Cell C above
     * @param row1col1 Number of publications not referencing meshterm B and gene A. Cell D above
     * @return A pair with sum squared as the first value and the p-value for the later.
     */
    @VisibleForTesting
    static Pair<Double, Double> chiSquare(Long row0col0, Long row0col1, Long row1col0, Long row1col1) {
        double total = row0col0 + row0col1 + row1col0 + row1col1;
        double rowSum0 = row0col0 + row0col1;
        double rowSum1 = row1col0 + row1col1;

        double colSum0 = row0col0 + row1col0;
        double colSum1 = row0col1 + row1col1;

        double sumSq = 0.0d;
        double expected = 0.0d;

        expected = (rowSum0 * colSum0) / total;
        sumSq += ((row0col0 - expected) * (row0col0 - expected)) / expected;

        expected = (rowSum0 * colSum1) / total;
        sumSq += ((row0col1 - expected) * (row0col1 - expected)) / expected;

        expected = (rowSum1 * colSum0) / total;
        sumSq += ((row1col0 - expected) * (row1col0 - expected)) / expected;

        expected = (rowSum1 * colSum1) / total;
        sumSq += ((row1col1 - expected) * (row1col1 - expected)) / expected;

        // The following code is directly using GammaDistribution which is internally being used by ChiSquaredDistribution here
        //https://github.com/apache/commons-statistics/blob/master/commons-statistics-distribution/src/main/java/org/apache/commons/statistics/distribution/ChiSquaredDistribution.java
        //GammaDistribution gamma = new GammaDistribution(df / 2, 2);
        double df = 1;

        double pval = 1 - cumulativeProbability(sumSq, df / 2, 2);
        return new Pair<Double, Double>(sumSq, pval);
    }

    /**
     * Returns the p-value using the {@href org.apache.commons.numbers.gamma.RegularizedGamma} api's.
     */
    @VisibleForTesting
    static double cumulativeProbability(double x, double shape, double scale) {
        if (x <= ChiSquaredRunnable.SUPPORT_LO) {
            return 0;
        } else if (x >= ChiSquaredRunnable.SUPPORT_HI) {
            return 1;
        }

        return RegularizedGamma.P.value(shape, x / scale);
    }

    /**
     * All the datasets by default are configured to be processed only as zip files.
     * This helper method takes the csv file and zips it. It also cleansup the previous unzipped file.
     *
     * @throws IOException
     */
    void zipOutputFile() throws IOException {
        GZIPOutputStream gzipOutputStream = new GZIPOutputStream(new FileOutputStream(this.outputZippedFile));
        byte[] buffer = new byte[2048];
        FileInputStream inputStream = new FileInputStream(this.outputFile);
        int bytes_read;

        while ((bytes_read = inputStream.read(buffer)) > 0) {
            gzipOutputStream.write(buffer, 0, bytes_read);
        }
        inputStream.close();
        gzipOutputStream.flush();
        gzipOutputStream.close();
        this.outputFile.delete();
    }
}
