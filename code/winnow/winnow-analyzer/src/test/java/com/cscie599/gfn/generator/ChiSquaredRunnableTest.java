package com.cscie599.gfn.generator;

import com.cscie599.gfn.importer.analyzer.GeneMeshPub;
import com.cscie599.gfn.importer.analyzer.GeneRawStats;
import com.cscie599.gfn.importer.analyzer.MeshtermRawStats;
import de.siegmar.fastcsv.reader.CsvContainer;
import de.siegmar.fastcsv.reader.CsvReader;
import de.siegmar.fastcsv.reader.CsvRow;
import org.javatuples.Pair;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.jupiter.api.Test;
import org.junit.rules.TemporaryFolder;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.zip.GZIPInputStream;

/**
 * @author PulkitBhanot
 */
class ChiSquaredRunnableTest {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Test
    void testChiSquareWithArray() {
        long[][] input = {{2, 3}, {8, 87}};
        Pair<Double, Double> returned = ChiSquaredRunnable.chiSquare(input);
        Assert.assertEquals(5.2632D, returned.getValue0(), .0001);
        Assert.assertEquals(.021781D, returned.getValue1(), .00001);
    }

    @Test
    void testChiSquare() {
        Pair<Double, Double> returned = ChiSquaredRunnable.chiSquare(2l, 3l, 8l, 87l);
        Assert.assertEquals(5.2632D, returned.getValue0(), .0001);
        Assert.assertEquals(.021781D, returned.getValue1(), .00001);
    }

    @Test
    void testChiSquareRunnableWithPublication() throws Exception {
        folder.create();
        File outputdir = folder.newFolder();
        List<GeneRawStats> geneRawStats = Arrays.asList(new GeneRawStats(889961, 235, "3209"));
        List<MeshtermRawStats> meshtermRawStats = Arrays.asList(new MeshtermRawStats(837743, 54574, "D000483"), new MeshtermRawStats(837743, 54574, "D000232483"));

        Map<String, GeneMeshPub> cache = new HashMap<>();
        GeneMeshPub geneMeshPub = new GeneMeshPub("3209", "109570", "D000483");
        geneMeshPub.getCounter().incrementAndGet();
        geneMeshPub.getCounter().incrementAndGet();
        geneMeshPub.getCounter().incrementAndGet();
        cache.put(geneMeshPub.getKey(), geneMeshPub);

        Pair<List<GeneRawStats>, List<MeshtermRawStats>> input = new Pair<>(geneRawStats, meshtermRawStats);
        CountDownLatch latch = new CountDownLatch(1);
        Thread t1 = new Thread(new ChiSquaredRunnable(input, latch, 1, false, cache, outputdir.getAbsolutePath()));
        t1.start();
        latch.await(30, TimeUnit.SECONDS);
        File outputFile = new File(outputdir.getAbsolutePath() + File.separator + "1.csv");
        File outputFileZipped = new File(outputdir.getAbsolutePath() + File.separator + "1.csv.gz");
        unzipFile(outputFileZipped, outputFile);
        CsvReader reader = new CsvReader();
        CsvContainer container = reader.read(outputFile, StandardCharsets.UTF_8);
        CsvRow row = container.getRow(0);
        Assert.assertEquals("3209", row.getField(0));
        Assert.assertEquals("D000483", row.getField(1));
        Assert.assertEquals("4", row.getField(2));
        Assert.assertEquals("231", row.getField(3));
        Assert.assertEquals("54570", row.getField(4));
        Assert.assertEquals("835391", row.getField(5));
        Assert.assertEquals(8.010473D, Double.parseDouble(row.getField(6)), .0001D);
        Assert.assertEquals(.0046507D, Double.parseDouble(row.getField(7)), .000001D);
    }

    @Test
    void testChiSquareRunnableWith0Publication() throws Exception {
        folder.create();
        File outputdir = folder.newFolder();
        List<GeneRawStats> geneRawStats = Arrays.asList(new GeneRawStats(889961, 235, "3209"));
        List<MeshtermRawStats> meshtermRawStats = Arrays.asList(new MeshtermRawStats(837743, 54574, "D000483"));

        Map<String, GeneMeshPub> cache = new HashMap<>();
        GeneMeshPub geneMeshPub = new GeneMeshPub("3209", "109570", "D000434");
        geneMeshPub.getCounter().incrementAndGet();
        geneMeshPub.getCounter().incrementAndGet();
        geneMeshPub.getCounter().incrementAndGet();
        cache.put(geneMeshPub.getKey(), geneMeshPub);

        Pair<List<GeneRawStats>, List<MeshtermRawStats>> input = new Pair<>(geneRawStats, meshtermRawStats);
        CountDownLatch latch = new CountDownLatch(1);
        Thread t1 = new Thread(new ChiSquaredRunnable(input, latch, 1, true, cache, outputdir.getAbsolutePath()));
        t1.start();
        latch.await(30, TimeUnit.SECONDS);
        File outputFile = new File(outputdir.getAbsolutePath() + File.separator + "1.csv");
        File outputFileZipped = new File(outputdir.getAbsolutePath() + File.separator + "1.csv.gz");
        unzipFile(outputFileZipped, outputFile);
        CsvReader reader = new CsvReader();
        CsvContainer container = reader.read(outputFile, StandardCharsets.UTF_8);
        CsvRow row = container.getRow(0);
        System.out.println(row);
        Assert.assertEquals("3209", row.getField(0));
        Assert.assertEquals("D000483", row.getField(1));
        Assert.assertEquals("0", row.getField(2));
        Assert.assertEquals("235", row.getField(3));
        Assert.assertEquals("54574", row.getField(4));
        Assert.assertEquals("835387", row.getField(5));
        Assert.assertEquals(15.3517697D, Double.parseDouble(row.getField(6)), .0001D);
        Assert.assertEquals(.0000892D, Double.parseDouble(row.getField(7)), .000001D);
    }

    private void unzipFile(File zippedFile, File extractedFile) throws IOException {
        byte[] buffer = new byte[1024];

        GZIPInputStream gzis =
                new GZIPInputStream(new FileInputStream(zippedFile));

        FileOutputStream out =
                new FileOutputStream(extractedFile);
        int len;
        while ((len = gzis.read(buffer)) > 0) {
            out.write(buffer, 0, len);
        }
        gzis.close();
        out.close();
    }
}