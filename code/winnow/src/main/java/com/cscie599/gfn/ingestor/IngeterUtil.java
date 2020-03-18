package com.cscie599.gfn.ingestor;

import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.interceptor.DefaultTransactionAttribute;

public class IngeterUtil {

    public static DefaultTransactionAttribute getDefaultTransactionAttribute(){
        DefaultTransactionAttribute attribute = new DefaultTransactionAttribute();
        attribute.setPropagationBehavior(Propagation.REQUIRED.value());
        attribute.setIsolationLevel(Isolation.SERIALIZABLE.value());
        attribute.setTimeout(30);
        return attribute;
    }
}
