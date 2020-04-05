package com.cscie599.gfn.views;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class PublicationView {
    private String publicationId;
    private Date completedDate;
    private Date dateRevised;
    private String title;
    private List<HashMap> authors;

    public PublicationView(String publicationId, Date completedDate, Date dateRevised, String title, List<HashMap> authors) {
        this.publicationId = publicationId;
        this.completedDate = completedDate;
        this.dateRevised = dateRevised;
        this.title = title;
        this.authors = authors;
    }

    public String getPublicationId() {
        return publicationId;
    }

    public Date getCompletedDate() {
        return completedDate;
    }

    public Date getDateRevised() {
        return dateRevised;
    }

    public String getTitle() {
        return title;
    }

    public List<HashMap> getAuthors() {
        return authors;
    }

}
