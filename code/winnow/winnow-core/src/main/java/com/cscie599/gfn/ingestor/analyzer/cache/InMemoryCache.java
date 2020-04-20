package com.cscie599.gfn.analyzer;

import com.cscie599.gfn.analyzer.entities.GeneMeshPub;
import com.cscie599.gfn.analyzer.entities.GeneRawStats;
import com.cscie599.gfn.analyzer.entities.MeshtermRawStats;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author PulkitBhanot
 */
public class InMemoryCache {

    private final Map<String, GeneRawStats> cachedGeneStats;

    private final Map<String, MeshtermRawStats> cachedMeshStats;

    private final Map<String, GeneMeshPub> cachedGeneMeshPubStats;

    private static InMemoryCache INSTANCE = new InMemoryCache(20000, 25000, 40000000);

    private InMemoryCache(int geneCacheSize, int meshCacheSize, int genMeshCacheSize) {
        cachedGeneStats = new ConcurrentHashMap<>(geneCacheSize);
        cachedMeshStats = new ConcurrentHashMap<>(meshCacheSize);
        cachedGeneMeshPubStats = new ConcurrentHashMap<>(genMeshCacheSize);
    }

    public static InMemoryCache getInstance() {
        return INSTANCE;
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
