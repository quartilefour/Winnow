package com.cscie599.gfn.importer.pubmed.converter;

import com.cscie599.gfn.importer.pubmed.PubmedArticle;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

public class PMIDConverter implements Converter {
    @Override
    public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {

    }

    @Override
    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
        PubmedArticle.PMID task = new PubmedArticle.PMID();

        task.setID(reader.getValue());

        return task;
    }


    @Override
    public boolean canConvert(Class clazz) {
        return clazz.getName().equals(PubmedArticle.PMID.class.getName());
    }
}
