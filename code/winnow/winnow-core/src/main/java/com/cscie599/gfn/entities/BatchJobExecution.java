package com.cscie599.gfn.entities;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author PulkitBhanot
 */
@Entity
@Table(name = "batch_job_execution")
@NamedQueries({
    @NamedQuery(name = "BatchJobExecution.findAll", query = "SELECT b FROM BatchJobExecution b"),
    @NamedQuery(name = "BatchJobExecution.findByJobExecutionId", query = "SELECT b FROM BatchJobExecution b WHERE b.jobExecutionId = :jobExecutionId"),
    @NamedQuery(name = "BatchJobExecution.findByVersion", query = "SELECT b FROM BatchJobExecution b WHERE b.version = :version"),
    @NamedQuery(name = "BatchJobExecution.findByCreateTime", query = "SELECT b FROM BatchJobExecution b WHERE b.createTime = :createTime"),
    @NamedQuery(name = "BatchJobExecution.findByStartTime", query = "SELECT b FROM BatchJobExecution b WHERE b.startTime = :startTime"),
    @NamedQuery(name = "BatchJobExecution.findByEndTime", query = "SELECT b FROM BatchJobExecution b WHERE b.endTime = :endTime"),
    @NamedQuery(name = "BatchJobExecution.findByStatus", query = "SELECT b FROM BatchJobExecution b WHERE b.status = :status"),
    @NamedQuery(name = "BatchJobExecution.findByExitCode", query = "SELECT b FROM BatchJobExecution b WHERE b.exitCode = :exitCode"),
    @NamedQuery(name = "BatchJobExecution.findByExitMessage", query = "SELECT b FROM BatchJobExecution b WHERE b.exitMessage = :exitMessage"),
    @NamedQuery(name = "BatchJobExecution.findByLastUpdated", query = "SELECT b FROM BatchJobExecution b WHERE b.lastUpdated = :lastUpdated"),
    @NamedQuery(name = "BatchJobExecution.findByJobConfigurationLocation", query = "SELECT b FROM BatchJobExecution b WHERE b.jobConfigurationLocation = :jobConfigurationLocation")})
public class BatchJobExecution implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "job_execution_id", nullable = false)
    private Long jobExecutionId;
    @Column(name = "version")
    private BigInteger version;
    @Basic(optional = false)
    @Column(name = "create_time", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createTime;
    @Column(name = "start_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date startTime;
    @Column(name = "end_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date endTime;
    @Column(name = "status", length = 10)
    private String status;
    @Column(name = "exit_code", length = 2500)
    private String exitCode;
    @Column(name = "exit_message", length = 2500)
    private String exitMessage;
    @Column(name = "last_updated")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastUpdated;
    @Column(name = "job_configuration_location", length = 2500)
    private String jobConfigurationLocation;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "jobExecutionId")
    private Collection<BatchStepExecution> batchStepExecutionCollection;
    @OneToOne(cascade = CascadeType.ALL, mappedBy = "batchJobExecution")
    private BatchJobExecutionContext batchJobExecutionContext;
    @JoinColumn(name = "job_instance_id", referencedColumnName = "job_instance_id", nullable = false)
    @ManyToOne(optional = false)
    private BatchJobInstance jobInstanceId;

    public BatchJobExecution() {
    }

    public BatchJobExecution(Long jobExecutionId) {
        this.jobExecutionId = jobExecutionId;
    }

    public BatchJobExecution(Long jobExecutionId, Date createTime) {
        this.jobExecutionId = jobExecutionId;
        this.createTime = createTime;
    }

    public Long getJobExecutionId() {
        return jobExecutionId;
    }

    public void setJobExecutionId(Long jobExecutionId) {
        this.jobExecutionId = jobExecutionId;
    }

    public BigInteger getVersion() {
        return version;
    }

    public void setVersion(BigInteger version) {
        this.version = version;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getExitCode() {
        return exitCode;
    }

    public void setExitCode(String exitCode) {
        this.exitCode = exitCode;
    }

    public String getExitMessage() {
        return exitMessage;
    }

    public void setExitMessage(String exitMessage) {
        this.exitMessage = exitMessage;
    }

    public Date getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Date lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public String getJobConfigurationLocation() {
        return jobConfigurationLocation;
    }

    public void setJobConfigurationLocation(String jobConfigurationLocation) {
        this.jobConfigurationLocation = jobConfigurationLocation;
    }

    public Collection<BatchStepExecution> getBatchStepExecutionCollection() {
        return batchStepExecutionCollection;
    }

    public void setBatchStepExecutionCollection(Collection<BatchStepExecution> batchStepExecutionCollection) {
        this.batchStepExecutionCollection = batchStepExecutionCollection;
    }

    public BatchJobExecutionContext getBatchJobExecutionContext() {
        return batchJobExecutionContext;
    }

    public void setBatchJobExecutionContext(BatchJobExecutionContext batchJobExecutionContext) {
        this.batchJobExecutionContext = batchJobExecutionContext;
    }

    public BatchJobInstance getJobInstanceId() {
        return jobInstanceId;
    }

    public void setJobInstanceId(BatchJobInstance jobInstanceId) {
        this.jobInstanceId = jobInstanceId;
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
        if (!(object instanceof BatchJobExecution)) {
            return false;
        }
        BatchJobExecution other = (BatchJobExecution) object;
        if ((this.jobExecutionId == null && other.jobExecutionId != null) || (this.jobExecutionId != null && !this.jobExecutionId.equals(other.jobExecutionId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.cscie599.gfn.entities.BatchJobExecution[ jobExecutionId=" + jobExecutionId + " ]";
    }
    
}
