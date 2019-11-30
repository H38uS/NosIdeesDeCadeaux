package com.mosioj.tests.model;

import static org.junit.Assert.assertEquals;

import java.sql.SQLException;

import org.junit.Test;

import com.mosioj.ideescadeaux.model.entities.User;
import com.mosioj.ideescadeaux.model.repositories.columns.UserRolesColumns;
import com.mosioj.ideescadeaux.notifications.instance.NotifNoIdea;
import com.mosioj.tests.TemplateTest;

public class TestUser extends TemplateTest {

	@Test
	public void testDelete() throws SQLException {

		String email = "a_new_email@toto.tutu";
		ds.executeUpdate("delete from USERS where email = ?", email);
		ds.executeUpdate("delete from USER_ROLES where email = ?", email);

		int userId = users.addNewPersonne(email, "hihi", "my_new_name");
		User user = users.getUser(userId);
		int notifId = notif.addNotification(userId, new NotifNoIdea());

		assertEquals(1, ds.selectCountStar("select count(*) from USERS where id = ?", user.id));
		assertEquals(1, ds.selectCountStar("select count(*) from USER_ROLES where " + UserRolesColumns.EMAIL + " = ?", user.email));
		assertNotifDoesExists(notifId);

		users.deleteUser(user);

		assertEquals(0, ds.selectCountStar("select count(*) from USERS where id = ?", user.id));
		assertEquals(0, ds.selectCountStar("select count(*) from USER_ROLES where " + UserRolesColumns.EMAIL + " = ?", user.email));
		assertNotifDoesNotExists(notifId);
	}

}
