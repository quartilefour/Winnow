package com.cscie599.gfn.importer.meshterm;

import com.cscie599.gfn.importer.pubmed.PubmedArticle;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

public class MeshConverter implements Converter {
    @Override
    public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {

    }

    @Override
    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
        DescriptorRecord.TreeNumberList task = new DescriptorRecord.TreeNumberList();

        reader.moveDown();
        update(task,reader.getValue());
        reader.moveUp();
        while (reader.hasMoreChildren()) {
            reader.moveDown();
            update(task,reader.getValue());
            reader.moveUp();
        }
        return task;
    }

    public void update(DescriptorRecord.TreeNumberList task, String contentStr){
        task.getTreeNumbers().add(contentStr);
    }
    @Override
    public boolean canConvert(Class clazz) {
        return clazz.getName().equals(DescriptorRecord.TreeNumberList.class.getName());
    }
}
