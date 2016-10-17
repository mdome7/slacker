package com.labs2160.slacker.core;

import java.util.Date;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Model;
import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.labs2160.slacker.core.cdi.Eager;
import com.labs2160.slacker.core.engine.WorkflowEngine;

/**
 * Keeps current state of the application.
 *
 * @author mdometita
 */
@Named("app")
@Model
@Eager
@ApplicationScoped
public class ApplicationManager {

    private static final Logger logger = LoggerFactory
            .getLogger(ApplicationManager.class);
    private ApplicationStatus status;

    private Date startDate;

    @Inject
    @Named("engine")
    private WorkflowEngine engine;

    @PostConstruct
    public void initialize() {
        logger.info("Initializing...");
        startDate = new Date();
        status = ApplicationStatus.INTIALIZING;
        engine.start();
        status = ApplicationStatus.RUNNING;
        logger.info("Server initialized!");
    }

    public ApplicationStatus getStatus() {
        return status;
    }

    public void setStatus(ApplicationStatus status) {
        this.status = status;
    }

    public Date getStartDate() {
        return startDate;
    }

    public WorkflowEngine getWorkflowEngine() {
        return engine;
    }
}
