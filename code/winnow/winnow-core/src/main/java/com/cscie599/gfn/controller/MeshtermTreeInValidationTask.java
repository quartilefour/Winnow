package com.cscie599.gfn.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Component to invalidate the inmemory MeshTerm tree cache every X milliseconds.
 *
 * @author PulkitBhanot
 */
@Component
public class MeshtermTreeInValidationTask {

    private static final Log logger = LogFactory.getLog(MeshtermTreeInValidationTask.class);

    @Autowired
    MeshtermController meshtermController;

    @Scheduled(fixedRate = 86400000)
    public void invalidateMeshtermCache() {
        logger.info("Invalidating inmemory cached Meshterm tree");
        meshtermController.invalidate();
    }
}
