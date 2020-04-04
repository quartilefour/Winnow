package com.cscie599.gfn.views;

import java.util.Date;
import java.util.List;

public class SearchView {
    private Long searchId;
    private String searchName;
    private List<String> searchQuery;
    private String queryType;
    private String queryFormat;
    private Date createdDate;
    private Date updatedAt;

    public SearchView(Long searchId, String searchName, List<String> searchQuery, String queryType, String queryFormat,
                      Date createdDate, Date updatedAt) {
        this.searchId = searchId;
        this.searchName = searchName;
        this.searchQuery = searchQuery;
        this.queryType = queryType;
        this.queryFormat = queryFormat;
        this.createdDate = createdDate;
        this.updatedAt = updatedAt;
    }

    public Long getSearchId() {
        return searchId;
    }

    public String getSearchName() {
        return searchName;
    }

    public List<String> getSearchQuery() {
        return searchQuery;
    }

    public String getQueryType() {
        return queryType;
    }

    public String getQueryFormat() {
        return queryFormat;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }
}
