package com.mosioj.ideescadeaux.model.repositories;

import static com.mosioj.ideescadeaux.model.repositories.columns.MessagesAccueilColumns.TEXT;
import static com.mosioj.ideescadeaux.model.repositories.columns.MessagesAccueilColumns.TYPE;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mosioj.ideescadeaux.utils.database.PreparedStatementIdKdo;

public class MessagesAccueil extends Table {

	private static final Logger logger = LogManager.getLogger(MessagesAccueil.class);
	private static final String TABLE_NAME = "MESSAGES_ACCUEIL";

	private static MessagesAccueil instance;
	private static Map<String, List<String>> messages;

	public MessagesAccueil() {
	}

	/**
	 * 
	 * @return For each type found, the possible list of messages.
	 */
	public Map<String, List<String>> getThem() {

		Map<String, List<String>> res = new HashMap<String, List<String>>();

		String query = MessageFormat.format("select {0}, {1} from {2}", TYPE, TEXT, TABLE_NAME);
		PreparedStatementIdKdo ps = new PreparedStatementIdKdo(getDb(), query);

		try {
			if (ps.execute()) {
				ResultSet rs = ps.getResultSet();
				while (rs.next()) {
					String type = rs.getString(TYPE.name());
					List<String> list = res.get(type);
					if (list == null) {
						list = new ArrayList<>();
						res.put(type, list);
					}
					list.add(rs.getString(TEXT.name()));
				}
			}
		} catch (SQLException e) {
			logger.error(e);
			e.printStackTrace();
		} finally {
			ps.close();
		}

		return res;
	}

	/**
	 * 
	 * @return All messages
	 */
	public static Map<String, List<String>> getMessages() {
		if (messages == null) {
			if (instance == null) {
				instance = new MessagesAccueil();
			}
			messages = instance.getThem();
		}
		return messages;
	}

	/**
	 * 
	 * @return All types.
	 */
	public static Set<String> getTypes() {
		return getMessages().keySet();
	}

	/**
	 * 
	 * @param type
	 * @return The messages.
	 */
	public static Optional<List<String>> getMessagesOf(String type) {
		return Optional.ofNullable(getMessages().get(type));
	}
	
	/**
	 * 
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
	 * 
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
	 * 
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
		NOEL, BIRTHDAY, NOTHING;
	}
}
