package com.cscie599.gfn.ingestor.analyzer.cache;

import com.cscie599.gfn.importer.CacheableEntity;
import org.springframework.batch.item.ItemWriter;

import java.util.List;
import java.util.Map;

/**
 * @author PulkitBhanot
 */
public class InMemoryMapWriter<T extends CacheableEntity> implements ItemWriter<T> {

    private Map<String, T> cache;
    public InMemoryMapWriter(Map<String, T> mapReference) {
        cache = mapReference;
    }

    @Override
    public void write(List<? extends T> items) throws Exception {
        items.forEach( (item) -> {
            cache.put(item.getKey(), item);
        });
    }
}
