package com.mosioj.ideescadeaux.core.model.repositories;

import com.mosioj.ideescadeaux.core.model.repositories.columns.MessagesAccueilColumns;
import com.mosioj.ideescadeaux.core.utils.db.HibernateUtil;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.hibernate.query.NativeQuery;

import java.text.MessageFormat;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

public class MessagesAccueilRepository extends AbstractRepository {

    private static final String TABLE_NAME = "MESSAGES_ACCUEIL";

    private static MessagesAccueilRepository instance;
    private static Map<String, List<String>> messages;

    private MessagesAccueilRepository() {
        // Forbidden
    }

    /**
     * @return For each type found, the possible list of messages.
     */
    public Map<String, List<String>> getThem() {

        String query = MessageFormat.format("select {0}, {1} from {2}",
                                            MessagesAccueilColumns.TYPE,
                                            MessagesAccueilColumns.TEXT,
                                            TABLE_NAME);

        List<Object[]> res = HibernateUtil.doQueryFetch(s -> ((NativeQuery<Object[]>) s.createSQLQuery(query)).list());
        return res.stream()
                  .map(r -> new ImmutablePair<>((String) r[0], (String) r[1]))
                  .collect(groupingBy(p -> p.left)).entrySet()
                  .stream()
                  .collect(Collectors.toMap(Map.Entry::getKey,
                                            e -> e.getValue().stream().map(ImmutablePair::getRight).toList()));
    }

    /**
     * @return All messages
     */
    public static Map<String, List<String>> getMessages() {
        if (messages == null) {
            if (instance == null) {
                instance = new MessagesAccueilRepository();
            }
            messages = instance.getThem();
        }
        return messages;
    }

    /**
     * @param type The type.
     * @return The messages.
     */
    public static Optional<List<String>> getMessagesOf(String type) {
        return Optional.ofNullable(getMessages().get(type));
    }

    /**
     * @return A possible message for Christmas approaching !
     */
    public static String getOneChristmasMessage() {
        List<String> possibilities = getMessagesOf(MessageTypes.NOEL.name()).orElse(new ArrayList<>());
        if (possibilities.isEmpty()) {
            return "Noël arrive bientôt...";
        }
        int index = new Random().nextInt(possibilities.size());
        return possibilities.get(index);
    }

    /**
     * @return A possible message for birthdays approaching.
     */
    public static String getOneBirthdayMessage() {
        List<String> possibilities = getMessagesOf(MessageTypes.BIRTHDAY.name()).orElse(new ArrayList<>());
        if (possibilities.isEmpty()) {
            return "Des anniversaires arrivent bientôt...";
        }
        int index = new Random().nextInt(possibilities.size());
        return possibilities.get(index);
    }

    /**
     * @return A possible message when nothing is approaching.
     */
    public static String getOneNothingMessage() {
        List<String> possibilities = getMessagesOf(MessageTypes.NOTHING.name()).orElse(new ArrayList<>());
        if (possibilities.isEmpty()) {
            return "Aucun anniversaire dans peu de temps... C'est le bon moment pour récupérer des repas !";
        }
        int index = new Random().nextInt(possibilities.size());
        return possibilities.get(index);
    }

    private enum MessageTypes {
        NOEL, BIRTHDAY, NOTHING
    }
}
