package com.cscie599.gfn

import org.apache.spark._
import org.apache.spark.sql.types._
import org.apache.spark.sql.functions.lit
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions.{col, desc, asc}

/**
 * This is a spark app that processed gene_orthologs, gene2pubmed and publication_meshterm datasets.
 *  1. From these datasests it first figures out top N orthologs for Human genes based on gene_orthologs.
 *  2. Then it joins the filtered genes with gene2pubmed to figure out the publications referencing those genes.
 *  3. For all the selected publications, normalizes gene to only represent the human gene.
 *    3.1 Also standardizing the count to 1 for occurences of a given gene against a publication.
 *  4. Then it joins the filtered set of publications with publication_meshterm to find the unique list of meshterms
 * that have been referenced in any of those publications.
 *  5. Joins the gene publication dataset with mesh publication datasets.
 *  6. Produces 4 datasets as putput
 *    6.1 gene_stats which for the given shortlisted set of genes and publications specifies publication counts containing and not containing that gene.
 *    6.2 mesh_stats which for the given shortlisted set of meshterms and publications specifies publication counts containing and not containing that meshterm.
 *    6.3 triplet of publication, meshterm and gene to be used for enrichment analysis.
 *    6.4 summary of the different datasets involved in the computation.
 *
 * The following code was initially created in a jupyter notebook and later on adopted to be used as a spark application.
 * @author PulkitBhanot
 */
object App {
  def main(args: Array[String]) {

    val spark = SparkSession.builder.appName("Winnow Batch Aggregator").config("spark.master", "yarn").getOrCreate()
    import spark.implicits._

    // Schema for the gene_orthologs dataset
    val GeneOrthologSchema = StructType(Array(
      StructField("tax_id", IntegerType, true),
      StructField("gene_id", IntegerType, true),
      StructField("relationship", StringType, true),
      StructField("other_tax_id", IntegerType, true),
      StructField("other_gene_id", IntegerType, true))
    )

    // Schema for the gene2pubmed dataset
    val GenePubmedSchema = StructType(Array(
      StructField("tax_id", IntegerType, true),
      StructField("gene_id", IntegerType, true),
      StructField("pubMed_id", IntegerType, true))
    )

    //Schema for the publication_meshterm dataset
    val Pubmed_meshterm_schema = StructType(Array(
      StructField("mesh_id", StringType, true),
      StructField("publication_id", IntegerType, true))
    )

    val df = spark.read.option("sep", "\t").option("header", true).schema(GeneOrthologSchema).csv("s3://cscie599/raw/gene_group/*.gz")

    df.printSchema()

    val total_gene_ortholog_count = df.count()
    println("Total gene_otholog_count %d", total_gene_ortholog_count)

    val df2 = spark.read.option("sep", "\t").option("header", true).schema(GenePubmedSchema).csv("s3://cscie599/raw/gene2pubmed/*.gz")

    val total_gene_pubmed_count = df2.count()
    println("Total gene_pubmed_count %d", total_gene_pubmed_count)

    //These are number of unique publications in gene2pubmed
    val total_unique_pub_count_genepubmed = df2.groupBy("pubMed_id").count().count()
    println("Total unique_pub_count_genepubmed %d", total_unique_pub_count_genepubmed)

    // Select top 40 other_tax_ids where genes are linked to human genes
    val filtered_tax_ids = df.filter($"tax_id" === 9606).groupBy("other_tax_id").count().orderBy(desc("count")).limit(40)

    filtered_tax_ids.show()

    //Create a record for the human gene
    val someDF = Seq(
      (9606, 50000)
    ).toDF("other_tax_id", "count")

    filtered_tax_ids.unionByName(someDF).show(50)

    val df_interesting_tax_ids = filtered_tax_ids.unionByName(someDF)

    df2.join(df_interesting_tax_ids, $"tax_id" === $"other_tax_id").show(10)

    val df_interesting_publications = df2.join(df_interesting_tax_ids, $"tax_id" === $"other_tax_id")

    df_interesting_publications.printSchema()

    //These are the number of publications left after selecting the top 30 orthologs for human genes
    val total_publication_selected_taxids = df_interesting_publications.groupBy("pubMed_id").count().count()

    println("Total total_publication_selected_taxids %s", total_publication_selected_taxids)

    //val df_interesting_other_genes = df.join(df_interesting_tax_ids, $"tax_id" === $"other_tax_id")
    df.join(df_interesting_tax_ids.withColumnRenamed("other_tax_id", "interested_other_tax_id").as("c"), df("tax_id") === $"c.interested_other_tax_id").show(10)

    df.join(df_interesting_tax_ids.withColumnRenamed("other_tax_id", "interested_other_tax_id").
      as("c"), df("tax_id") === $"c.interested_other_tax_id").
      select($"other_tax_id", $"other_gene_id").show(10)

    //val df_interesting_other_genes = df.join(df_interesting_tax_ids, $"tax_id" === $"other_tax_id")
    df.join(df_interesting_tax_ids.withColumnRenamed("other_tax_id", "interested_other_tax_id").
      as("c"), df("tax_id") === $"c.interested_other_tax_id").
      select($"other_tax_id", $"other_gene_id").show(10)

    df_interesting_tax_ids.show(100)

    val interested_other_gene_ids = df.join(df_interesting_tax_ids.withColumnRenamed("other_tax_id", "interested_other_tax_id").
      as("c"), df("tax_id") === $"c.interested_other_tax_id").
      select($"other_tax_id", $"other_gene_id", $"tax_id", $"gene_id")

    /*val interested_other_gene_ids = df.join(df_interesting_tax_ids.withColumnRenamed("other_tax_id", "interested_other_tax_id").
      as("c"), (df("tax_id") === $"c.interested_other_tax_id")).
      select($"other_tax_id", $"other_gene_id", $"tax_id", $"gene_id")*/

    interested_other_gene_ids.printSchema()

    df_interesting_tax_ids.show(100)

    //These are the genes which are refered to by the top N orthologs

    val filtered_gene_count_from_ortholog_post_tax_filter = interested_other_gene_ids.count()

    println("Total filtered_gene_count_from_ortholog_post_tax_filter %d", filtered_gene_count_from_ortholog_post_tax_filter)

    interested_other_gene_ids.show(10)

    interested_other_gene_ids.withColumnRenamed("tax_id", "interested_tax_id").withColumnRenamed("other_gene_id", "interested_gene_id").show(10)

    val interested_other_gene_ids_col_renamed = interested_other_gene_ids.withColumnRenamed("tax_id", "interested_tax_id").withColumnRenamed("gene_id", "interested_gene_id").withColumnRenamed("other_tax_id", "interested_other_tax_id")
    interested_other_gene_ids_col_renamed.show(10)

    // Now we will join the interested_other_gene_ids dataset with df_interesting_publications
    // on interested_other_gene_ids.other_gene_id = df_interesting_publications.gene_id
    // and interested_other_gene_ids.other_tax_id = df_interesting_publications.tax_id
    // for the joined dataset return interested_other_gene_ids.tax_id and interested_other_gene_ids.gene_id

    val human_mapped_non_human_gene_pubs = df_interesting_publications.join(interested_other_gene_ids_col_renamed,
      $"gene_id" === $"other_gene_id" &&
        $"tax_id" === $"interested_other_tax_id"
    ).select($"pubMed_id", $"interested_tax_id", $"interested_gene_id")
    human_mapped_non_human_gene_pubs.show(10)

    // These are the number of publications that have human mapped genes called out
    val total_human_ortholog_gene_count = human_mapped_non_human_gene_pubs.groupBy("pubMed_id").count().count()

    println("Total total_human_ortholog_gene_count %d", total_human_ortholog_gene_count)

    // These are number of publications that directly reference human genes
    val human_gene_pubmed = df2.filter($"tax_id" === 9606).select($"pubMed_id", $"gene_id", $"tax_id")
    human_gene_pubmed.show(10)

    human_mapped_non_human_gene_pubs.printSchema()

    val col_fixed_human_mapped_non_human_gene_pubs = human_mapped_non_human_gene_pubs.withColumnRenamed("interested_tax_id", "tax_id").withColumnRenamed("interested_gene_id", "gene_id")
    col_fixed_human_mapped_non_human_gene_pubs.show(10)

    val human_ortholog_unionized_genes = col_fixed_human_mapped_non_human_gene_pubs.unionByName(human_gene_pubmed)
    human_ortholog_unionized_genes.show(10)

    human_ortholog_unionized_genes.printSchema()

    // These are the number of publications of interest with genes of humans and other orthologs
    val interesting_publication_human_ortholog_genes_count = human_ortholog_unionized_genes.groupBy("pubMed_id").count().count()

    println("Total interesting_publication_human_ortholog_genes_count %d", interesting_publication_human_ortholog_genes_count)

    // This shows the skewness of data how some of the publications have thousands of human genes + ortholog genes listed.
    human_ortholog_unionized_genes.cache()
    human_ortholog_unionized_genes.groupBy("pubMed_id", "tax_id").count().orderBy(desc("count")).show()

    val pubmed_trimmed = spark.read.schema(Pubmed_meshterm_schema).option("header", false).csv(
      "s3://cscie599/raw-out/pubmed_meshterm_csv_gz/*.gz")

    pubmed_trimmed.cache()
    val total_pubmed_mesh_count = pubmed_trimmed.count()
    println("Total total_pubmed_mesh_count %d", total_pubmed_mesh_count)

    // This is meshterms joined with genes for humans + N human orthologs
    val joined_human_ortholog_mehsterm = pubmed_trimmed.join(human_ortholog_unionized_genes, $"publication_id" === $"pubMed_id").drop("creation_date").drop("update_date").drop("tax_id")

    // Normalize all the null records and remove the duplicates if any.
    val joined_human_ortholog_mehsterm_deduped = joined_human_ortholog_mehsterm.groupBy("publication_id", "gene_id", "mesh_id").count()

    joined_human_ortholog_mehsterm_deduped.count()


    joined_human_ortholog_mehsterm_deduped.printSchema()

    //These are all the publications which had human + ortholog genes which have meshterms annotated with them.
    // We need to count based on unique publications
    val total_mesh_publications = joined_human_ortholog_mehsterm_deduped.groupBy("publication_id").count().count()

    joined_human_ortholog_mehsterm_deduped.show(10)


    val unique_meshterms = joined_human_ortholog_mehsterm_deduped.groupBy("publication_id", "mesh_id").count().groupBy("mesh_id").count().orderBy(asc("mesh_id"))
    val unique_mesh_pub_counts = unique_meshterms.withColumn(
      "pub_without_mesh", lit(total_mesh_publications) - col("count")).withColumn(
      "pub_with_mesh", col("count")).drop("count").withColumn(
      "uniq_mesh_id", col("mesh_id")).drop("mesh_id").orderBy(asc("uniq_mesh_id"))

    unique_mesh_pub_counts.cache()
    val uniq_publication_mesh_count = unique_mesh_pub_counts.count()
    unique_mesh_pub_counts.show(10)

    // This shows the skewness of meshterm datasets
    unique_meshterms.orderBy(desc("count")).show(50)

    unique_mesh_pub_counts.orderBy(asc("pub_without_mesh")).show(10)

    unique_mesh_pub_counts.orderBy(desc("pub_without_mesh")).show(10)

    // Write the meshterms to storage,it would have a count of publications where the mesh existed and publications where the mesh didnot exist
    // +----------------+-------------+------------+
    //|pub_without_mesh|pub_with_mesh|uniq_mesh_id|
    //+----------------+-------------+------------+
    //|          891876|          441|     D000001|
    //|          892316|            1|     D000003|
    //|          892316|            1|     D000004|
    //|          892078|          239|     D000005|
    //|          892312|            5|     D000006|
    //|          892313|            4|     D000007|
    unique_mesh_pub_counts.coalesce(1).write.format(
      "com.databricks.spark.csv").option("delimiter", ",").option("codec", "org.apache.hadoop.io.compress.GzipCodec").option("header", "true").mode(
      "overwrite").option("header", "false").save("s3://cscie599/raw/mesh_stats/")


    import org.apache.spark.sql.functions.lit

    // We only need to know the unique publication count having genes here.
    val total_gene_publication = joined_human_ortholog_mehsterm_deduped.groupBy("publication_id").count().count()

    val unique_genes = joined_human_ortholog_mehsterm_deduped.groupBy("publication_id", "gene_id").count().groupBy("gene_id").count().orderBy(asc("gene_id"))
    // We add an extra column to each gene which is the number of publications not having that gene.
    val unique_gene_pub_counts = unique_genes.withColumn(
      "pub_without_gene", lit(total_gene_publication) - col("count")).withColumn(
      "pub_with_gene", col("count")).drop("count").withColumn(
      "uniq_gene_id", col("gene_id")).drop("gene_id").orderBy(asc("uniq_gene_id"))

    unique_gene_pub_counts.cache()
    val uniq_publication_gene_count = unique_gene_pub_counts.count()
    unique_gene_pub_counts.show(10)

    unique_gene_pub_counts.orderBy(desc("pub_without_gene")).show(10)

    unique_gene_pub_counts.orderBy(asc("pub_without_gene")).show(10)

    //Write all the genes to storage, for all geneid it would have a count of publications where the gene existed and publications where the gene didnot exist
    // +----------------+-------------+------------+
    //|pub_without_gene|pub_with_gene|uniq_gene_id|
    //+----------------+-------------+------------+
    //|          892241|           76|           1|
    //|          891695|          622|           9|
    //|          892290|           27|          12|
    //|          892056|          261|          13|
    //|          891988|          329|          14|
    unique_gene_pub_counts.coalesce(1).write.format(
      "com.databricks.spark.csv").option("delimiter", ",").option("codec", "org.apache.hadoop.io.compress.GzipCodec").option("header", "true").mode(
      "overwrite").option("header", "false").save("s3://cscie599/raw/gene_stats/")


    // This dataset is the co-occuring genes and meshterm
    // +--------------+-------+-------+-----+
    //|publication_id|gene_id|mesh_id|count|
    //+--------------+-------+-------+-----+
    //|          7240|   9606|D000478|    2|
    //|          7240|   9606|D000595|    2|
    //|          7240|   9606|D002268|    2|
    //|          7240|   9606|D002498|    2|
    //|          7240|   9606|D002845|    2|
    //|          7240|   9606|D002847|    2|
    //|          7240|   9606|D003172|    2|

    joined_human_ortholog_mehsterm_deduped.coalesce(5).write.format(
      "com.databricks.spark.csv").option("delimiter", ",").option("codec", "org.apache.hadoop.io.compress.GzipCodec").option("header", "true").mode(
      "overwrite").option("header", "false").save("s3://cscie599/raw/gene_mesh_pub_stats/")

    unique_mesh_pub_counts.show(10)

    unique_gene_pub_counts.show(10)

    joined_human_ortholog_mehsterm_deduped.show(10)

    val summaryDF = Seq(
      ("total_gene_ortholog_count", total_gene_ortholog_count),
      ("total_gene_pubmed_count", total_gene_pubmed_count),
      ("total_publication_selected_taxids", total_publication_selected_taxids),
      ("total_human_ortholog_gene_count", total_human_ortholog_gene_count),
      ("interesting_publication_human_ortholog_genes_count", interesting_publication_human_ortholog_genes_count),
      ("total_pubmed_mesh_count", total_pubmed_mesh_count),
      ("total_mesh_publications", total_mesh_publications),
      ("total_gene_publication", total_gene_publication),
      ("uniq_publication_gene_count", uniq_publication_gene_count),
      ("uniq_publication_mesh_count", uniq_publication_mesh_count)
    ).toDF("key", "value")

    summaryDF.coalesce(1).write.format(
      "com.databricks.spark.csv").option("delimiter", ",").option("header", "true").option("codec", "org.apache.hadoop.io.compress.GzipCodec").mode(
      "overwrite").save("s3://cscie599/raw-out/summary/")
    spark.stop()
  }
}