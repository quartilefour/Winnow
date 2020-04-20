package com.cscie599.gfn.service;

/**
 * @author PulkitBhanot
 */
public interface IngestionService {

    boolean ingestBaseData();

    boolean ingestDataForEnrichmentAnalysis();

    boolean ingestEnrichedData();
}
