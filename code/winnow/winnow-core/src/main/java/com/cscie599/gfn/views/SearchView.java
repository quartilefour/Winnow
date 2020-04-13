package com.cscie599.gfn.views;

import java.util.Date;
import java.util.HashMap;

public class SearchView {
    private Long searchId;
    private String searchName;
    private HashMap<String, Object> searchQuery;
    private Date createdDate;
    private Date updatedAt;

    public SearchView(Long searchId, String searchName, HashMap<String, Object> searchQuery, Date createdDate, Date updatedAt) {
        this.searchId = searchId;
        this.searchName = searchName;
        this.searchQuery = searchQuery;
        this.createdDate = createdDate;
        this.updatedAt = updatedAt;
    }

    public Long getSearchId() {
        return searchId;
    }

    public String getSearchName() {
        return searchName;
    }

    public HashMap<String, Object> getSearchQuery() {
        return searchQuery;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }
}
