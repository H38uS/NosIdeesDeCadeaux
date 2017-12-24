package com.mosioj.model.table;

import static com.mosioj.model.table.columns.SousReservationColumns.COMMENT;
import static com.mosioj.model.table.columns.SousReservationColumns.DATE_RESERVATION;
import static com.mosioj.model.table.columns.SousReservationColumns.IDEE_ID;
import static com.mosioj.model.table.columns.SousReservationColumns.USER_ID;

import java.sql.SQLException;
import java.text.MessageFormat;

public class SousReservation extends Table {

	public static final String TABLE_NAME = "SOUS_RESERVATION";

	public void sousReserver(int idea, int userId, String comment) throws SQLException {
		getDb().executeUpdate(	MessageFormat.format(	"insert into {0}({1},{2},{3},{4}) values (?, ?, ?, now()) ",
														TABLE_NAME,
														IDEE_ID,
														USER_ID,
														COMMENT,
														DATE_RESERVATION),
								idea,
								userId,
								comment);
	}
}
