package com.cscie599.gfn.ingestor;

import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.interceptor.DefaultTransactionAttribute;

/**
 *
 * @author PulkitBhanot
 */
public class IngeterUtil {

    public static DefaultTransactionAttribute getDefaultTransactionAttribute(){
        DefaultTransactionAttribute attribute = new DefaultTransactionAttribute();
        attribute.setIsolationLevel(Isolation.READ_COMMITTED.value());
        return attribute;
    }
}
