package com.mosioj.ideescadeaux.core;

import com.mosioj.ideescadeaux.core.model.database.DataSourceIdKDo;
import com.mosioj.ideescadeaux.core.model.entities.User;
import com.mosioj.ideescadeaux.core.model.notifications.NotificationActivation;
import com.mosioj.ideescadeaux.core.model.notifications.NotificationType;
import com.mosioj.ideescadeaux.core.model.repositories.UserParametersRepository;
import com.mosioj.ideescadeaux.core.model.repositories.UserRelationsRepository;
import com.mosioj.ideescadeaux.core.model.repositories.UsersRepository;
import com.mysql.cj.jdbc.MysqlDataSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.*;
import org.junit.rules.TestName;

import java.sql.SQLException;

public class TemplateTest {

    /**
     * firefox@toto.com aka firefox
     */
    protected static final int _OWNER_ID_ = 26;
    /**
     * test@toto.com aka friend of Firefox
     */
    protected static final int _FRIEND_ID_ = 4;
    /**
     * moiautre@toto.com
     */
    protected static final int _MOI_AUTRE_ = 8;
    /** Class logger. */
    private final static Logger LOGGER = LogManager.getLogger(TemplateTest.class);
    protected static DataSourceIdKDo ds;
    @Rule
    public TestName name = new TestName();
    protected User firefox;
    protected User friendOfFirefox;
    protected User moiAutre;

    public TemplateTest() {
        try {
            friendOfFirefox = UsersRepository.getUser(_FRIEND_ID_).orElseThrow(SQLException::new);
            firefox = UsersRepository.getUser(_OWNER_ID_).orElseThrow(SQLException::new);
            moiAutre = UsersRepository.getUser(_MOI_AUTRE_).orElseThrow(SQLException::new);
        } catch (SQLException e) {
            Assert.fail("Fail to retrieve the friend of Firefox");
        }
    }

    @BeforeClass
    public static void init() throws SQLException {

        MysqlDataSource dataSource = new MysqlDataSource();

        dataSource.setDatabaseName("test_ideeskdos");
        dataSource.setUser("mosioj");
        dataSource.setPassword("tuaD50Kv2jguyX5ncokK");
        dataSource.setURL("jdbc:mysql://192.168.1.7/test_ideeskdos?serverTimezone=Europe/Paris&useUnicode=yes");//

        DataSourceIdKDo.setDataSource(dataSource);
        ds = new DataSourceIdKDo();
        String email = ds.selectString("select email from USERS where id = ?", 3).orElseThrow(SQLException::new);
        Assert.assertEquals("ymosio@wanadzdzdzdoo.fr", email);

        for (NotificationType type : NotificationType.values()) {
            final User firefox = UsersRepository.getUser(_OWNER_ID_).orElseThrow(SQLException::new);
            UserRelationsRepository.getAllUsersInRelation(firefox)
                                   .forEach(u -> {
                                       try {
                                           UserParametersRepository.insertUpdateParameter(u,
                                                                                          type.name(),
                                                                                          NotificationActivation.SITE.name());
                                       } catch (SQLException e) {
                                           e.printStackTrace();
                                           Assert.fail();
                                       }
                                   });
            UserParametersRepository.insertUpdateParameter(firefox, type.name(), NotificationActivation.SITE.name());
        }
    }

    @Before
    public void printName() {
        LOGGER.info("============ Running " + name.getMethodName() + " ============");
    }

    @After
    public void printLine() {
        int length = name.getMethodName().length() + "============ Running ".length() + " ============".length();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++)
            sb.append("=");
        LOGGER.info(sb);
        System.out.println();
        System.out.println();
    }

    protected void assertNotifDoesNotExists(int notifId) {
        Assert.assertEquals(0, ds.selectCountStar("select count(*) from NOTIFICATIONS where id = ?", notifId));
    }

    protected void assertNotifDoesExists(int notifId) {
        Assert.assertEquals(1, ds.selectCountStar("select count(*) from NOTIFICATIONS where id = ?", notifId));
    }
}
