package com.cscie599.gfn.importer.pubmed.converter;

import com.cscie599.gfn.importer.pubmed.PubmedArticle;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

/**
 *
 * @author PulkitBhanot
 */
public class MeshHeadingConverter  implements Converter {
    @Override
    public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {

    }

    @Override
    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
        PubmedArticle.MeshHeading task = new PubmedArticle.MeshHeading();

        reader.moveDown();
        update(task,reader.getNodeName(), reader.getAttribute("UI"),Boolean.valueOf(reader.getAttribute("MajorTopicYN")),reader.getValue());
        reader.moveUp();
        while (reader.hasMoreChildren()) {
            reader.moveDown();
            update(task,reader.getNodeName(), reader.getAttribute("UI"),Boolean.valueOf(reader.getAttribute("MajorTopicYN")),reader.getValue());
            reader.moveUp();
        }
        return task;
    }

    public void update(PubmedArticle.MeshHeading task, String nodeName, String attributeValue, Boolean majorTopic, String contentStr){
        PubmedArticle.MeshHeadingContent content =new PubmedArticle.MeshHeadingContent();
        content.setUI(attributeValue);
        content.setMajorTopic(majorTopic);
        content.setContent(contentStr);
        switch (nodeName){
            case "DescriptorName":
                task.getDescriptorName().add(content);
                break;
            case "QualifierName":
                task.getQualifierName().add(content);
                break;
        }
    }
    @Override
    public boolean canConvert(Class clazz) {
        return clazz.getName().equals(PubmedArticle.MeshHeading.class.getName());
    }
}
