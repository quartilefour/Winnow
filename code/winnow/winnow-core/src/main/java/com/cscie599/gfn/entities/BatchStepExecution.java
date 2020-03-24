/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cscie599.gfn.entities;

import java.io.Serializable;
import java.math.BigInteger;
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
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author PulkitBhanot
 */
@Entity
@Table(name = "batch_step_execution")
@NamedQueries({
    @NamedQuery(name = "BatchStepExecution.findAll", query = "SELECT b FROM BatchStepExecution b"),
    @NamedQuery(name = "BatchStepExecution.findByStepExecutionId", query = "SELECT b FROM BatchStepExecution b WHERE b.stepExecutionId = :stepExecutionId"),
    @NamedQuery(name = "BatchStepExecution.findByVersion", query = "SELECT b FROM BatchStepExecution b WHERE b.version = :version"),
    @NamedQuery(name = "BatchStepExecution.findByStepName", query = "SELECT b FROM BatchStepExecution b WHERE b.stepName = :stepName"),
    @NamedQuery(name = "BatchStepExecution.findByStartTime", query = "SELECT b FROM BatchStepExecution b WHERE b.startTime = :startTime"),
    @NamedQuery(name = "BatchStepExecution.findByEndTime", query = "SELECT b FROM BatchStepExecution b WHERE b.endTime = :endTime"),
    @NamedQuery(name = "BatchStepExecution.findByStatus", query = "SELECT b FROM BatchStepExecution b WHERE b.status = :status"),
    @NamedQuery(name = "BatchStepExecution.findByCommitCount", query = "SELECT b FROM BatchStepExecution b WHERE b.commitCount = :commitCount"),
    @NamedQuery(name = "BatchStepExecution.findByReadCount", query = "SELECT b FROM BatchStepExecution b WHERE b.readCount = :readCount"),
    @NamedQuery(name = "BatchStepExecution.findByFilterCount", query = "SELECT b FROM BatchStepExecution b WHERE b.filterCount = :filterCount"),
    @NamedQuery(name = "BatchStepExecution.findByWriteCount", query = "SELECT b FROM BatchStepExecution b WHERE b.writeCount = :writeCount"),
    @NamedQuery(name = "BatchStepExecution.findByReadSkipCount", query = "SELECT b FROM BatchStepExecution b WHERE b.readSkipCount = :readSkipCount"),
    @NamedQuery(name = "BatchStepExecution.findByWriteSkipCount", query = "SELECT b FROM BatchStepExecution b WHERE b.writeSkipCount = :writeSkipCount"),
    @NamedQuery(name = "BatchStepExecution.findByProcessSkipCount", query = "SELECT b FROM BatchStepExecution b WHERE b.processSkipCount = :processSkipCount"),
    @NamedQuery(name = "BatchStepExecution.findByRollbackCount", query = "SELECT b FROM BatchStepExecution b WHERE b.rollbackCount = :rollbackCount"),
    @NamedQuery(name = "BatchStepExecution.findByExitCode", query = "SELECT b FROM BatchStepExecution b WHERE b.exitCode = :exitCode"),
    @NamedQuery(name = "BatchStepExecution.findByExitMessage", query = "SELECT b FROM BatchStepExecution b WHERE b.exitMessage = :exitMessage"),
    @NamedQuery(name = "BatchStepExecution.findByLastUpdated", query = "SELECT b FROM BatchStepExecution b WHERE b.lastUpdated = :lastUpdated")})
public class BatchStepExecution implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "step_execution_id", nullable = false)
    private Long stepExecutionId;
    @Basic(optional = false)
    @Column(name = "version", nullable = false)
    private long version;
    @Basic(optional = false)
    @Column(name = "step_name", nullable = false, length = 100)
    private String stepName;
    @Basic(optional = false)
    @Column(name = "start_time", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date startTime;
    @Column(name = "end_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date endTime;
    @Column(name = "status", length = 10)
    private String status;
    @Column(name = "commit_count")
    private BigInteger commitCount;
    @Column(name = "read_count")
    private BigInteger readCount;
    @Column(name = "filter_count")
    private BigInteger filterCount;
    @Column(name = "write_count")
    private BigInteger writeCount;
    @Column(name = "read_skip_count")
    private BigInteger readSkipCount;
    @Column(name = "write_skip_count")
    private BigInteger writeSkipCount;
    @Column(name = "process_skip_count")
    private BigInteger processSkipCount;
    @Column(name = "rollback_count")
    private BigInteger rollbackCount;
    @Column(name = "exit_code", length = 2500)
    private String exitCode;
    @Column(name = "exit_message", length = 2500)
    private String exitMessage;
    @Column(name = "last_updated")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastUpdated;
    @JoinColumn(name = "job_execution_id", referencedColumnName = "job_execution_id", nullable = false)
    @ManyToOne(optional = false)
    private BatchJobExecution jobExecutionId;
    @OneToOne(cascade = CascadeType.ALL, mappedBy = "batchStepExecution")
    private BatchStepExecutionContext batchStepExecutionContext;

    public BatchStepExecution() {
    }

    public BatchStepExecution(Long stepExecutionId) {
        this.stepExecutionId = stepExecutionId;
    }

    public BatchStepExecution(Long stepExecutionId, long version, String stepName, Date startTime) {
        this.stepExecutionId = stepExecutionId;
        this.version = version;
        this.stepName = stepName;
        this.startTime = startTime;
    }

    public Long getStepExecutionId() {
        return stepExecutionId;
    }

    public void setStepExecutionId(Long stepExecutionId) {
        this.stepExecutionId = stepExecutionId;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    public String getStepName() {
        return stepName;
    }

    public void setStepName(String stepName) {
        this.stepName = stepName;
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

    public BigInteger getCommitCount() {
        return commitCount;
    }

    public void setCommitCount(BigInteger commitCount) {
        this.commitCount = commitCount;
    }

    public BigInteger getReadCount() {
        return readCount;
    }

    public void setReadCount(BigInteger readCount) {
        this.readCount = readCount;
    }

    public BigInteger getFilterCount() {
        return filterCount;
    }

    public void setFilterCount(BigInteger filterCount) {
        this.filterCount = filterCount;
    }

    public BigInteger getWriteCount() {
        return writeCount;
    }

    public void setWriteCount(BigInteger writeCount) {
        this.writeCount = writeCount;
    }

    public BigInteger getReadSkipCount() {
        return readSkipCount;
    }

    public void setReadSkipCount(BigInteger readSkipCount) {
        this.readSkipCount = readSkipCount;
    }

    public BigInteger getWriteSkipCount() {
        return writeSkipCount;
    }

    public void setWriteSkipCount(BigInteger writeSkipCount) {
        this.writeSkipCount = writeSkipCount;
    }

    public BigInteger getProcessSkipCount() {
        return processSkipCount;
    }

    public void setProcessSkipCount(BigInteger processSkipCount) {
        this.processSkipCount = processSkipCount;
    }

    public BigInteger getRollbackCount() {
        return rollbackCount;
    }

    public void setRollbackCount(BigInteger rollbackCount) {
        this.rollbackCount = rollbackCount;
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

    public BatchJobExecution getJobExecutionId() {
        return jobExecutionId;
    }

    public void setJobExecutionId(BatchJobExecution jobExecutionId) {
        this.jobExecutionId = jobExecutionId;
    }

    public BatchStepExecutionContext getBatchStepExecutionContext() {
        return batchStepExecutionContext;
    }

    public void setBatchStepExecutionContext(BatchStepExecutionContext batchStepExecutionContext) {
        this.batchStepExecutionContext = batchStepExecutionContext;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (stepExecutionId != null ? stepExecutionId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof BatchStepExecution)) {
            return false;
        }
        BatchStepExecution other = (BatchStepExecution) object;
        if ((this.stepExecutionId == null && other.stepExecutionId != null) || (this.stepExecutionId != null && !this.stepExecutionId.equals(other.stepExecutionId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.cscie599.gfn.entities.BatchStepExecution[ stepExecutionId=" + stepExecutionId + " ]";
    }
    
}
