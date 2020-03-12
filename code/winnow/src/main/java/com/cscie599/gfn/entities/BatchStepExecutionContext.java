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
 * @author bhanotp
 */
@Entity
@Table(name = "batch_step_execution_context")
@NamedQueries({
    @NamedQuery(name = "BatchStepExecutionContext.findAll", query = "SELECT b FROM BatchStepExecutionContext b"),
    @NamedQuery(name = "BatchStepExecutionContext.findByStepExecutionId", query = "SELECT b FROM BatchStepExecutionContext b WHERE b.stepExecutionId = :stepExecutionId"),
    @NamedQuery(name = "BatchStepExecutionContext.findByShortContext", query = "SELECT b FROM BatchStepExecutionContext b WHERE b.shortContext = :shortContext"),
    @NamedQuery(name = "BatchStepExecutionContext.findBySerializedContext", query = "SELECT b FROM BatchStepExecutionContext b WHERE b.serializedContext = :serializedContext")})
public class BatchStepExecutionContext implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "step_execution_id", nullable = false)
    private Long stepExecutionId;
    @Basic(optional = false)
    @Column(name = "short_context", nullable = false, length = 2500)
    private String shortContext;
    @Column(name = "serialized_context", length = 2147483647)
    private String serializedContext;
    @JoinColumn(name = "step_execution_id", referencedColumnName = "step_execution_id", nullable = false, insertable = false, updatable = false)
    @OneToOne(optional = false)
    private BatchStepExecution batchStepExecution;

    public BatchStepExecutionContext() {
    }

    public BatchStepExecutionContext(Long stepExecutionId) {
        this.stepExecutionId = stepExecutionId;
    }

    public BatchStepExecutionContext(Long stepExecutionId, String shortContext) {
        this.stepExecutionId = stepExecutionId;
        this.shortContext = shortContext;
    }

    public Long getStepExecutionId() {
        return stepExecutionId;
    }

    public void setStepExecutionId(Long stepExecutionId) {
        this.stepExecutionId = stepExecutionId;
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

    public BatchStepExecution getBatchStepExecution() {
        return batchStepExecution;
    }

    public void setBatchStepExecution(BatchStepExecution batchStepExecution) {
        this.batchStepExecution = batchStepExecution;
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
        if (!(object instanceof BatchStepExecutionContext)) {
            return false;
        }
        BatchStepExecutionContext other = (BatchStepExecutionContext) object;
        if ((this.stepExecutionId == null && other.stepExecutionId != null) || (this.stepExecutionId != null && !this.stepExecutionId.equals(other.stepExecutionId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.cscie599.gfn.entities.BatchStepExecutionContext[ stepExecutionId=" + stepExecutionId + " ]";
    }
    
}
