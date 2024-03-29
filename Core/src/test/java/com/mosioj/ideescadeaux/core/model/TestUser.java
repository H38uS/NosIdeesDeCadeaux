package com.mosioj.ideescadeaux.core.model;

import com.mosioj.ideescadeaux.core.TemplateTest;
import com.mosioj.ideescadeaux.core.model.entities.User;
import com.mosioj.ideescadeaux.core.model.entities.notifications.NType;
import com.mosioj.ideescadeaux.core.model.entities.notifications.Notification;
import com.mosioj.ideescadeaux.core.model.repositories.UsersRepository;
import org.junit.Test;

import java.sql.SQLException;

import static org.junit.Assert.assertEquals;

public class TestUser extends TemplateTest {

    @Test
    public void testDelete() throws SQLException {

        String email = "a_new_email@toto.tutu";
        ds.executeUpdate("delete from USERS where email = ?", email);
        ds.executeUpdate("delete from USER_ROLES where email = ?", email);

        int userId = UsersRepository.addNewPersonne(email, "hihi", "my_new_name");
        User user = UsersRepository.getUser(userId).orElseThrow(SQLException::new);
        Notification notification = NType.NO_IDEA.buildDefault().sendItTo(user);

        assertEquals(1, ds.selectCountStar("select count(*) from USERS where id = ?", user.id));
        assertEquals(1, ds.selectCountStar("select count(*) from USER_ROLES where email = ?", user.email));
        assertNotifDoesExists(notification.getId());

        UsersRepository.deleteUser(user);

        assertEquals(0, ds.selectCountStar("select count(*) from USERS where id = ?", user.id));
        assertEquals(0, ds.selectCountStar("select count(*) from USER_ROLES where email = ?", user.email));
        assertNotifDoesNotExists(notification.getId());
    }

}
