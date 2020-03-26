package com.cscie599.gfn.views;

public class MeshtermCategoryView {
    private String categoryId;
    private String name;

    public MeshtermCategoryView(String categoryId, String name) {
        this.categoryId = categoryId;
        this.name = name;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public String getName() {
        return name;
    }
}
