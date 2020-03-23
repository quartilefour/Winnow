/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cscie599.gfn.entities;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Collection;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 *
 * @author PulkitBhanot
 */
@Entity
@Table(name = "batch_job_instance", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"job_name", "job_key"})})
@NamedQueries({
    @NamedQuery(name = "BatchJobInstance.findAll", query = "SELECT b FROM BatchJobInstance b"),
    @NamedQuery(name = "BatchJobInstance.findByJobInstanceId", query = "SELECT b FROM BatchJobInstance b WHERE b.jobInstanceId = :jobInstanceId"),
    @NamedQuery(name = "BatchJobInstance.findByVersion", query = "SELECT b FROM BatchJobInstance b WHERE b.version = :version"),
    @NamedQuery(name = "BatchJobInstance.findByJobName", query = "SELECT b FROM BatchJobInstance b WHERE b.jobName = :jobName"),
    @NamedQuery(name = "BatchJobInstance.findByJobKey", query = "SELECT b FROM BatchJobInstance b WHERE b.jobKey = :jobKey")})
public class BatchJobInstance implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "job_instance_id", nullable = false)
    private Long jobInstanceId;
    @Column(name = "version")
    private BigInteger version;
    @Basic(optional = false)
    @Column(name = "job_name", nullable = false, length = 100)
    private String jobName;
    @Basic(optional = false)
    @Column(name = "job_key", nullable = false, length = 32)
    private String jobKey;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "jobInstanceId")
    private Collection<BatchJobExecution> batchJobExecutionCollection;

    public BatchJobInstance() {
    }

    public BatchJobInstance(Long jobInstanceId) {
        this.jobInstanceId = jobInstanceId;
    }

    public BatchJobInstance(Long jobInstanceId, String jobName, String jobKey) {
        this.jobInstanceId = jobInstanceId;
        this.jobName = jobName;
        this.jobKey = jobKey;
    }

    public Long getJobInstanceId() {
        return jobInstanceId;
    }

    public void setJobInstanceId(Long jobInstanceId) {
        this.jobInstanceId = jobInstanceId;
    }

    public BigInteger getVersion() {
        return version;
    }

    public void setVersion(BigInteger version) {
        this.version = version;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public String getJobKey() {
        return jobKey;
    }

    public void setJobKey(String jobKey) {
        this.jobKey = jobKey;
    }

    public Collection<BatchJobExecution> getBatchJobExecutionCollection() {
        return batchJobExecutionCollection;
    }

    public void setBatchJobExecutionCollection(Collection<BatchJobExecution> batchJobExecutionCollection) {
        this.batchJobExecutionCollection = batchJobExecutionCollection;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (jobInstanceId != null ? jobInstanceId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof BatchJobInstance)) {
            return false;
        }
        BatchJobInstance other = (BatchJobInstance) object;
        if ((this.jobInstanceId == null && other.jobInstanceId != null) || (this.jobInstanceId != null && !this.jobInstanceId.equals(other.jobInstanceId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.cscie599.gfn.entities.BatchJobInstance[ jobInstanceId=" + jobInstanceId + " ]";
    }
    
}
