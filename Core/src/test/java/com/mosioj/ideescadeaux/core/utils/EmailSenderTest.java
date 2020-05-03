package com.mosioj.ideescadeaux.core.utils;

import com.mosioj.ideescadeaux.core.TemplateTest;
import org.junit.Test;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class EmailSenderTest extends TemplateTest {

    /**
     * Timeout en mode maven build à environ 2.5 secondes.
     */
    @Test(timeout = 3500)
    public void emailShouldBeAsynchronous() throws InterruptedException, ExecutionException {

        Future<?> res1 = EmailSender.sendEmail("dzjhdquhd@dzkozjkd.cijzcjiz", "test", "Da test.");
        Future<?> res2 = EmailSender.sendEmail("dzjhdquhd@dzkozjkd.cijzcjiz", "test", "Da test.");
        Future<?> res3 = EmailSender.sendEmail("dzjhdquhd@dzkozjkd.cijzcjiz", "test", "Da test.");

        res1.get();
        res2.get();
        res3.get();
    }
}