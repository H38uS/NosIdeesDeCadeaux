package com.mosioj.ideescadeaux.tasks;

import com.mosioj.ideescadeaux.core.model.database.DataSourceIdKDo;
import com.mosioj.ideescadeaux.core.utils.AppVersion;
import com.mysql.cj.jdbc.MysqlDataSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.Properties;

public class IdeesCadeauxTask {

    private static final Logger logger = LogManager.getLogger(IdeesCadeauxTask.class);

    public static void main(String[] args) {

        logger.info("Running IdeesCadeauxTasks...");
        logger.info("Application version: {}", AppVersion.DA_VERSION);

        try {
            DataSourceIdKDo.setDataSource(initDB());
        } catch (IOException e) {
            logger.error("Fail to initialize the DB... Exiting.", e);
            return;
        }

        sendNotification();
        logger.info("Run IdeesCadeauxTasks completed successfully !");
    }

    /**
     * Do all the work. Assumes the connexion has been setup.
     */
    protected static void sendNotification() {

        BirthdayNotifier bn = new BirthdayNotifier();

        // A la personne, pour qu'elle mette à jour ses idées
        bn.findBirthdayAndSendMailToTheLuckyOne(20);

        // Au poto, pour qu'ils réservent les idées !
        bn.findBirthdayAndSendMailToFriends(15);
        bn.findBirthdayAndSendMailToFriends(5);
    }

    /**
     * @return The datasource.
     */
    private static DataSource initDB() throws IOException {

        Properties props = new Properties();
        InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("db.properties");
        props.load(is);

        MysqlDataSource mysqlDS = new MysqlDataSource();
        String url = MessageFormat.format("{0}?useLegacyDatetimeCode=false&serverTimezone=Europe/Paris&useUnicode=yes",
                                          props.getProperty("MYSQL_DB_URL"));
        logger.debug(MessageFormat.format("URL: {0}", url));

        mysqlDS.setURL(url);
        mysqlDS.setUser(props.getProperty("MYSQL_DB_USERNAME"));
        mysqlDS.setPassword(props.getProperty("MYSQL_DB_PASSWORD"));

        return mysqlDS;
    }

}
