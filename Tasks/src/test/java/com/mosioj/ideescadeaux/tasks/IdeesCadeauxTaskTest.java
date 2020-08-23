package com.mosioj.ideescadeaux.tasks;

import com.mosioj.ideescadeaux.core.model.database.DataSourceIdKDo;
import com.mysql.cj.jdbc.MysqlDataSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.BeforeClass;
import org.junit.Test;

public class IdeesCadeauxTaskTest {

    private static final Logger logger = LogManager.getLogger(IdeesCadeauxTaskTest.class);

    @BeforeClass
    public static void init() {

        logger.info("Seting up the DB...");
        final String url = "jdbc:mysql://192.168.1.7/test_ideeskdos?useLegacyDatetimeCode=false&serverTimezone=Europe/Paris&useUnicode=yes";

        MysqlDataSource mysqlDS = new MysqlDataSource();
        mysqlDS.setDatabaseName("test_ideeskdos");
        mysqlDS.setUser("mosioj");
        mysqlDS.setPassword("tuaD50Kv2jguyX5ncokK");
        mysqlDS.setURL(url);

        DataSourceIdKDo.setDataSource(mysqlDS);
    }

    @Test
    public void executionShouldNotCrash() {
        logger.info("Sending notification...");
        IdeesCadeauxTask.sendNotification();
    }

}