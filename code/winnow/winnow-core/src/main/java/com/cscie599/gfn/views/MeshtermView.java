package com.cscie599.gfn.views;

public class MeshtermView {
    private String meshId;
    private String name;

    public MeshtermView(String meshId, String name) {
        this.meshId = meshId;
        this.name = name;
    }

    public String getMeshId() {
        return meshId;
    }

    public String getName() {
        return name;
    }
}
