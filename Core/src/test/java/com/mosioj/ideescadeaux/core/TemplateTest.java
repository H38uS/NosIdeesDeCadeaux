package com.mosioj.ideescadeaux.core;

import com.mosioj.ideescadeaux.core.model.database.DataSourceIdKDo;
import com.mosioj.ideescadeaux.core.model.entities.User;
import com.mosioj.ideescadeaux.core.model.entities.notifications.NType;
import com.mosioj.ideescadeaux.core.model.repositories.UsersRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.*;
import org.junit.rules.TestName;

import java.sql.SQLException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

        ds = new DataSourceIdKDo();
        String email = UsersRepository.getUser(3).map(User::getEmail).orElseThrow(SQLException::new);
        Assert.assertEquals("ymosio@wanadzdzdzdoo.fr", email);
        
        String values = Stream.of(NType.values()).map(v -> "'" + v + "'").collect(Collectors.joining(","));
        ds.executeUpdate("update USER_PARAMETERS set parameter_value = 'SITE' where parameter_name in (" +
                         values +
                         ")");
    }

    @Before
    public void printName() {
        LOGGER.info("============ Running " + name.getMethodName() + " ============");
    }

    @After
    public void printLine() {
        int length = name.getMethodName().length() + "============ Running ".length() + " ============".length();
        StringBuilder sb = new StringBuilder();
        sb.append("=".repeat(Math.max(0, length)));
        LOGGER.info(sb);
        System.out.println();
        System.out.println();
    }

    protected void assertNotifDoesNotExists(long notifId) {
        Assert.assertEquals(0, ds.selectCountStar("select count(*) from NOTIFICATIONS where id = ?", notifId));
    }

    protected void assertNotifDoesExists(long notifId) {
        Assert.assertEquals(1, ds.selectCountStar("select count(*) from NOTIFICATIONS where id = ?", notifId));
    }
}
