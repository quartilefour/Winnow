package com.cscie599.gfn.ingestor.analyzer.cache;

import com.cscie599.gfn.importer.analyzer.GeneMeshPub;
import com.cscie599.gfn.importer.analyzer.GeneRawStats;
import com.cscie599.gfn.importer.analyzer.MeshtermRawStats;
import com.cscie599.gfn.ingestor.analyzer.GeneRawStatsIngester;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author PulkitBhanot
 */
@Component
public class InMemoryCache {

    protected static final Log logger = LogFactory.getLog(InMemoryCache.class);

    private final Map<String, GeneRawStats> cachedGeneStats;

    private final Map<String, MeshtermRawStats> cachedMeshStats;

    private final Map<String, GeneMeshPub> cachedGeneMeshPubStats;

    @Value("${cache.genes.size:30}")
    private int geneCacheSize;

    @Value("${cache.mesh.size:30}")
    private int meshCacheSize;

    @Value("${cache.genes-mesh.size:30}")
    private int geneMeshCacheSize;

    @Autowired
    public InMemoryCache(@Value("${cache.genes.size:30}") int geneCacheSize, @Value("${cache.mesh.size:30}") int meshCacheSize, @Value("${cache.genes-mesh.size:30}") int geneMeshCacheSize) {
        logger.info("Creating inmemory cache with values geneCacheSize " + geneCacheSize + " meshCacheSize " + meshCacheSize + " geneMeshCacheSize " + geneMeshCacheSize);
        cachedGeneStats = new ConcurrentHashMap<>(geneCacheSize);
        cachedMeshStats = new ConcurrentHashMap<>(meshCacheSize);
        cachedGeneMeshPubStats = new ConcurrentHashMap<>(geneMeshCacheSize);
    }

    public Map<String, GeneRawStats> getCachedGeneStats() {
        return cachedGeneStats;
    }

    public Map<String, MeshtermRawStats> getCachedMeshStats() {
        return cachedMeshStats;
    }

    public Map<String, GeneMeshPub> getCachedGeneMeshPubStats() {
        return cachedGeneMeshPubStats;
    }
}
