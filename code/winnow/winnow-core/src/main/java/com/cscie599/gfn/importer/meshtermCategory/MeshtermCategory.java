package com.cscie599.gfn.importer.meshtermCategory;

public class MeshtermCategory {
    String categoryId;
    String name;

    public String getCategoryId() {
        return categoryId;
    }

    public String getName() {
        return name;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "MeshtermCategory{" +
                "categoryId='" + categoryId + '\'' +
                ", name='" + name;
    }
}
