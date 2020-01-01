package com.mosioj.ideescadeaux.core.model;

import static org.junit.Assert.assertEquals;

import java.sql.SQLException;

import com.mosioj.ideescadeaux.core.model.repositories.NotificationsRepository;
import com.mosioj.ideescadeaux.core.model.repositories.UsersRepository;
import org.junit.Test;

import com.mosioj.ideescadeaux.core.model.entities.User;
import com.mosioj.ideescadeaux.core.model.repositories.columns.UserRolesColumns;
import com.mosioj.ideescadeaux.core.model.notifications.instance.NotifNoIdea;
import com.mosioj.ideescadeaux.core.TemplateTest;

public class TestUser extends TemplateTest {

    @Test
    public void testDelete() throws SQLException {

        String email = "a_new_email@toto.tutu";
        ds.executeUpdate("delete from USERS where email = ?", email);
        ds.executeUpdate("delete from USER_ROLES where email = ?", email);

        int userId = UsersRepository.addNewPersonne(email, "hihi", "my_new_name");
        User user = UsersRepository.getUser(userId);
        int notifId = NotificationsRepository.addNotification(userId, new NotifNoIdea());

        assertEquals(1, ds.selectCountStar("select count(*) from USERS where id = ?", user.id));
        assertEquals(1,
                     ds.selectCountStar("select count(*) from USER_ROLES where " + UserRolesColumns.EMAIL + " = ?",
                                        user.email));
        assertNotifDoesExists(notifId);

        UsersRepository.deleteUser(user);

        assertEquals(0, ds.selectCountStar("select count(*) from USERS where id = ?", user.id));
        assertEquals(0,
                     ds.selectCountStar("select count(*) from USER_ROLES where " + UserRolesColumns.EMAIL + " = ?",
                                        user.email));
        assertNotifDoesNotExists(notifId);
    }

}
