package com.mosioj.ideescadeaux.tasks;

import com.mosioj.ideescadeaux.core.utils.AppVersion;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class IdeesCadeauxTask {

    private static final Logger logger = LogManager.getLogger(IdeesCadeauxTask.class);

    public static void main(String[] args) {
        try {
            logger.info("Running IdeesCadeauxTasks...");
            logger.info("Application version: {}", AppVersion.DA_VERSION);
            sendNotification();
            logger.info("Run IdeesCadeauxTasks completed successfully !");
        } catch (Exception e) {
            logger.error("Something wrong happened", e);
        }
    }

    /**
     * Do all the work. Assumes the connexion has been setup.
     */
    protected static void sendNotification() {

        BirthdayNotifier bn = new BirthdayNotifier();

        // A la personne, pour qu'elle mette à jour ses idées
        logger.info("Envoie des mails aux personnes qui ont leur anniversaire qui arrive...");
        bn.findBirthdayAndSendMailToTheLuckyOne(20);

        // Au poto, pour qu'ils réservent les idées !
        logger.info("Envoie aux copains pour les prévenir !");
        bn.findBirthdayAndSendMailToFriends(15);
        bn.findBirthdayAndSendMailToFriends(5);
    }

}
