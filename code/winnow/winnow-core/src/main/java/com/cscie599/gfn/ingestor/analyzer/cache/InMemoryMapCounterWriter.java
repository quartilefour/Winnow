package com.cscie599.gfn.ingestor.analyzer.cache;

import com.cscie599.gfn.importer.analyzer.GeneMeshPub;
import org.springframework.batch.item.ItemWriter;

import java.util.List;
import java.util.Map;

/**
 * @author PulkitBhanot
 */
public class InMemoryMapCounterWriter implements ItemWriter<GeneMeshPub> {

    private Map<String, GeneMeshPub> cache;
    public InMemoryMapCounterWriter(Map<String, GeneMeshPub> mapReference) {
        cache = mapReference;
    }

    @Override
    public void write(List<? extends GeneMeshPub> items) throws Exception {
        items.forEach( (item) -> {
            GeneMeshPub existingItem = cache.putIfAbsent(item.getKey(), item);
            if(existingItem != null){
                existingItem.getCounter().incrementAndGet();
            }
        });
    }
}
