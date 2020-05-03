package com.mosioj.ideescadeaux.tasks;

import com.mosioj.ideescadeaux.core.model.entities.User;
import com.mosioj.ideescadeaux.core.model.repositories.UserRelationsRepository;
import com.mosioj.ideescadeaux.core.utils.EmailSender;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.List;

public class BirthdayNotifier {

    private static final Logger logger = LogManager.getLogger(BirthdayNotifier.class);

    /**
     * Finds the user that have their birthday coming in nbDays. Then find all their friends. Finally send an email to
     * this friend list.
     *
     * @param nbDays The number of days before the next birthday.
     */
    public void findBirthdayAndSendMailToFriends(int nbDays) {
        List<User> users;
        try {
            users = UserRelationsRepository.getBirthday(nbDays);
        } catch (SQLException e) {
            logger.error(MessageFormat.format("Fail to get the user list: {0}", e.getMessage()));
            return;
        }
        users.parallelStream().forEach(u -> sendMailToFriends(u, nbDays));
    }

    /**
     * Finds the user that have their birthday coming in nbDays and send them a reminder email.
     *
     * @param nbDays The number of days before the next birthday.
     */
    public void findBirthdayAndSendMailToTheLuckyOne(int nbDays) {
        List<User> users;
        try {
            users = UserRelationsRepository.getBirthday(nbDays);
        } catch (SQLException e) {
            logger.error(MessageFormat.format("Fail to get the user list: {0}", e.getMessage()));
            return;
        }
        users.parallelStream().forEach(u -> sendMailToLuckyOne(u, nbDays));
    }

    /**
     * Send an email to the given user, saying that the birthday is coming !
     *
     * @param user   The user that will have its birthday in nbDays.
     * @param nbDays The number of days before the birthday comes.
     */
    private void sendMailToLuckyOne(User user, int nbDays) {
        logger.info(MessageFormat.format("Envoie d''un mail à {0} ({1}) pour son anniversaire dans {2} jours !",
                                         user,
                                         user.id,
                                         nbDays));

        String body = EmailSender.MY_PROPERTIES.get("birthday_lucky").toString();
        body = body.replaceAll("\\$\\$nb_jours\\$\\$", nbDays + "");

        EmailSender.sendEmail(user.email, "Votre anniversaire approche... Compléter votre liste !", body);
    }

    /**
     * Send an email to all friends of the given user, saying that the birthday is coming !
     *
     * @param user   The user that will have its birthday in nbDays.
     * @param nbDays The number of days before the birthday comes.
     */
    private void sendMailToFriends(User user, int nbDays) {
        try {
            logger.info(MessageFormat.format("L''anniversaire {0} ({1}) arrive dans {2} !",
                                             user.getMyDName(),
                                             user.id,
                                             nbDays));
            UserRelationsRepository.getAllUsersInRelation(user)
                                   .parallelStream()
                                   .forEach(u -> sendMail(u, user, nbDays));
        } catch (SQLException e) {
            logger.error(MessageFormat.format("Fail to get the user list: {0}", e.getMessage()));
        }
    }

    /**
     * Send an email to toUser, saying that the birthday of birthdayUser is coming !
     *
     * @param toUser       The user to which we should send an email.
     * @param birthdayUser The friend that has a birthday coming !
     * @param nbDays       The number of days before the birthday.
     */
    private void sendMail(User toUser, User birthdayUser, int nbDays) {
        logger.info(MessageFormat.format("Envoie d''un mail à {0} pour l''anniversaire {1} ({2}) dans {3} jours !",
                                         toUser,
                                         birthdayUser.getMyDName(),
                                         birthdayUser.id,
                                         nbDays));

        String body = EmailSender.MY_PROPERTIES.get("birthday").toString();

        body = body.replaceAll("\\$\\$de_name\\$\\$", birthdayUser.getMyDName());
        body = body.replaceAll("\\$\\$name\\$\\$", birthdayUser.getName());
        body = body.replaceAll("\\$\\$nb_jours\\$\\$", nbDays + "");
        body = body.replaceAll("\\$\\$id\\$\\$", birthdayUser.id + "");

        String nameInFullText = StringEscapeUtils.unescapeHtml4(birthdayUser.getMyDName());
        EmailSender.sendEmail(toUser.email,
                              MessageFormat.format("L''anniversaire {0} est proche !", nameInFullText),
                              body);
    }
}
