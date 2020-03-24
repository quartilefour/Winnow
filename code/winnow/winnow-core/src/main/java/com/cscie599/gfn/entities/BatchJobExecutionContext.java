/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cscie599.gfn.entities;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 *
 * @author PulkitBhanot
 */
@Entity
@Table(name = "batch_job_execution_context")
@NamedQueries({
    @NamedQuery(name = "BatchJobExecutionContext.findAll", query = "SELECT b FROM BatchJobExecutionContext b"),
    @NamedQuery(name = "BatchJobExecutionContext.findByJobExecutionId", query = "SELECT b FROM BatchJobExecutionContext b WHERE b.jobExecutionId = :jobExecutionId"),
    @NamedQuery(name = "BatchJobExecutionContext.findByShortContext", query = "SELECT b FROM BatchJobExecutionContext b WHERE b.shortContext = :shortContext"),
    @NamedQuery(name = "BatchJobExecutionContext.findBySerializedContext", query = "SELECT b FROM BatchJobExecutionContext b WHERE b.serializedContext = :serializedContext")})
public class BatchJobExecutionContext implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "job_execution_id", nullable = false)
    private Long jobExecutionId;
    @Basic(optional = false)
    @Column(name = "short_context", nullable = false, length = 2500)
    private String shortContext;
    @Column(name = "serialized_context", length = 2147483647)
    private String serializedContext;
    @JoinColumn(name = "job_execution_id", referencedColumnName = "job_execution_id", nullable = false, insertable = false, updatable = false)
    @OneToOne(optional = false)
    private BatchJobExecution batchJobExecution;

    public BatchJobExecutionContext() {
    }

    public BatchJobExecutionContext(Long jobExecutionId) {
        this.jobExecutionId = jobExecutionId;
    }

    public BatchJobExecutionContext(Long jobExecutionId, String shortContext) {
        this.jobExecutionId = jobExecutionId;
        this.shortContext = shortContext;
    }

    public Long getJobExecutionId() {
        return jobExecutionId;
    }

    public void setJobExecutionId(Long jobExecutionId) {
        this.jobExecutionId = jobExecutionId;
    }

    public String getShortContext() {
        return shortContext;
    }

    public void setShortContext(String shortContext) {
        this.shortContext = shortContext;
    }

    public String getSerializedContext() {
        return serializedContext;
    }

    public void setSerializedContext(String serializedContext) {
        this.serializedContext = serializedContext;
    }

    public BatchJobExecution getBatchJobExecution() {
        return batchJobExecution;
    }

    public void setBatchJobExecution(BatchJobExecution batchJobExecution) {
        this.batchJobExecution = batchJobExecution;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (jobExecutionId != null ? jobExecutionId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof BatchJobExecutionContext)) {
            return false;
        }
        BatchJobExecutionContext other = (BatchJobExecutionContext) object;
        if ((this.jobExecutionId == null && other.jobExecutionId != null) || (this.jobExecutionId != null && !this.jobExecutionId.equals(other.jobExecutionId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.cscie599.gfn.entities.BatchJobExecutionContext[ jobExecutionId=" + jobExecutionId + " ]";
    }
    
}
