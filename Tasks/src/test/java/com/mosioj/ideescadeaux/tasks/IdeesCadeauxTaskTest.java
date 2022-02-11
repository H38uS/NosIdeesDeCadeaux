package com.mosioj.ideescadeaux.tasks;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

public class IdeesCadeauxTaskTest {

    private static final Logger logger = LogManager.getLogger(IdeesCadeauxTaskTest.class);

    @Test
    public void executionShouldNotCrash() {
        logger.info("Sending notification...");
        IdeesCadeauxTask.sendNotification();
    }

}