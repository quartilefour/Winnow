package com.cscie599.gfn.importer.goterm;

import java.util.List;

public class Root
{
    private List<Graphs> graphs;

    public void setGraphs(List<Graphs> graphs){
        this.graphs = graphs;
    }
    public List<Graphs> getGraphs(){
        return this.graphs;
    }


public static class Graphs {
    private List<Nodes> nodes;

    private List<Edges> edges;

    private String id;

    private Meta meta;

    private List<String> equivalentNodesSets;

    private List<LogicalDefinitionAxioms> logicalDefinitionAxioms;

    private List<String> domainRangeAxioms;

    private List<PropertyChainAxioms> propertyChainAxioms;

    public void setNodes(List<Nodes> nodes) {
        this.nodes = nodes;
    }

    public List<Nodes> getNodes() {
        return this.nodes;
    }

    public void setEdges(List<Edges> edges) {
        this.edges = edges;
    }

    public List<Edges> getEdges() {
        return this.edges;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return this.id;
    }

    public void setMeta(Meta meta) {
        this.meta = meta;
    }

    public Meta getMeta() {
        return this.meta;
    }

    public void setEquivalentNodesSets(List<String> equivalentNodesSets) {
        this.equivalentNodesSets = equivalentNodesSets;
    }

    public List<String> getEquivalentNodesSets() {
        return this.equivalentNodesSets;
    }

    public void setLogicalDefinitionAxioms(List<LogicalDefinitionAxioms> logicalDefinitionAxioms) {
        this.logicalDefinitionAxioms = logicalDefinitionAxioms;
    }

    public List<LogicalDefinitionAxioms> getLogicalDefinitionAxioms() {
        return this.logicalDefinitionAxioms;
    }

    public void setDomainRangeAxioms(List<String> domainRangeAxioms) {
        this.domainRangeAxioms = domainRangeAxioms;
    }

    public List<String> getDomainRangeAxioms() {
        return this.domainRangeAxioms;
    }

    public void setPropertyChainAxioms(List<PropertyChainAxioms> propertyChainAxioms) {
        this.propertyChainAxioms = propertyChainAxioms;
    }

    public List<PropertyChainAxioms> getPropertyChainAxioms() {
        return this.propertyChainAxioms;
    }
}

public static class Definition {
        private String val;

        private List<String> xrefs;

        public void setVal(String val) {
            this.val = val;
        }

        public String getVal() {
            return this.val;
        }

        public void setXrefs(List<String> xrefs) {
            this.xrefs = xrefs;
        }

        public List<String> getXrefs() {
            return this.xrefs;
        }
    }

    public static class Xrefs {
        private String val;

        public void setVal(String val) {
            this.val = val;
        }

        public String getVal() {
            return this.val;
        }
    }

    public static class Synonyms {
        private String pred;

        private String val;

        private List<String> xrefs;

        public void setPred(String pred) {
            this.pred = pred;
        }

        public String getPred() {
            return this.pred;
        }

        public void setVal(String val) {
            this.val = val;
        }

        public String getVal() {
            return this.val;
        }

        public void setXrefs(List<String> xrefs) {
            this.xrefs = xrefs;
        }

        public List<String> getXrefs() {
            return this.xrefs;
        }
    }

    public static class BasicPropertyValues {
        private String pred;

        private String val;

        public void setPred(String pred) {
            this.pred = pred;
        }

        public String getPred() {
            return this.pred;
        }

        public void setVal(String val) {
            this.val = val;
        }

        public String getVal() {
            return this.val;
        }
    }

    public static class Meta {
        private Definition definition;

        private List<String> comments;

        private List<String> subsets;

        private List<Xrefs> xrefs;

        private List<Synonyms> synonyms;

        private List<BasicPropertyValues> basicPropertyValues;

        public void setDefinition(Definition definition) {
            this.definition = definition;
        }

        public Definition getDefinition() {
            return this.definition;
        }

        public void setComments(List<String> comments) {
            this.comments = comments;
        }

        public List<String> getComments() {
            return this.comments;
        }

        public void setSubsets(List<String> subsets) {
            this.subsets = subsets;
        }

        public List<String> getSubsets() {
            return this.subsets;
        }

        public void setXrefs(List<Xrefs> xrefs) {
            this.xrefs = xrefs;
        }

        public List<Xrefs> getXrefs() {
            return this.xrefs;
        }

        public void setSynonyms(List<Synonyms> synonyms) {
            this.synonyms = synonyms;
        }

        public List<Synonyms> getSynonyms() {
            return this.synonyms;
        }

        public void setBasicPropertyValues(List<BasicPropertyValues> basicPropertyValues) {
            this.basicPropertyValues = basicPropertyValues;
        }

        public List<BasicPropertyValues> getBasicPropertyValues() {
            return this.basicPropertyValues;
        }
    }

    public static class Nodes {
        private String id;

        private Meta meta;

        private String type;

        private String lbl;

        public void setId(String id) {
            this.id = id;
        }

        public String getId() {
            return this.id;
        }

        public void setMeta(Meta meta) {
            this.meta = meta;
        }

        public Meta getMeta() {
            return this.meta;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getType() {
            return this.type;
        }

        public void setLbl(String lbl) {
            this.lbl = lbl;
        }

        public String getLbl() {
            return this.lbl;
        }
    }

    public static class Edges {
        private String sub;

        private String pred;

        private String obj;

        public void setSub(String sub) {
            this.sub = sub;
        }

        public String getSub() {
            return this.sub;
        }

        public void setPred(String pred) {
            this.pred = pred;
        }

        public String getPred() {
            return this.pred;
        }

        public void setObj(String obj) {
            this.obj = obj;
        }

        public String getObj() {
            return this.obj;
        }
    }


    public static class Restrictions {
        private String propertyId;

        private String fillerId;

        public void setPropertyId(String propertyId) {
            this.propertyId = propertyId;
        }

        public String getPropertyId() {
            return this.propertyId;
        }

        public void setFillerId(String fillerId) {
            this.fillerId = fillerId;
        }

        public String getFillerId() {
            return this.fillerId;
        }
    }

    public static class LogicalDefinitionAxioms {
        private String definedClassId;

        private List<String> genusIds;

        private List<Restrictions> restrictions;

        public void setDefinedClassId(String definedClassId) {
            this.definedClassId = definedClassId;
        }

        public String getDefinedClassId() {
            return this.definedClassId;
        }

        public void setGenusIds(List<String> genusIds) {
            this.genusIds = genusIds;
        }

        public List<String> getGenusIds() {
            return this.genusIds;
        }

        public void setRestrictions(List<Restrictions> restrictions) {
            this.restrictions = restrictions;
        }

        public List<Restrictions> getRestrictions() {
            return this.restrictions;
        }
    }

    public static class PropertyChainAxioms {
        private String predicateId;

        private List<String> chainPredicateIds;

        public void setPredicateId(String predicateId) {
            this.predicateId = predicateId;
        }

        public String getPredicateId() {
            return this.predicateId;
        }

        public void setChainPredicateIds(List<String> chainPredicateIds) {
            this.chainPredicateIds = chainPredicateIds;
        }

        public List<String> getChainPredicateIds() {
            return this.chainPredicateIds;
        }
    }


}