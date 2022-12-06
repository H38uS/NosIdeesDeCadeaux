package com.mosioj.ideescadeaux.webapp;

import com.mosioj.ideescadeaux.core.model.database.DataSourceIdKDo;
import com.mosioj.ideescadeaux.core.model.entities.User;
import com.mosioj.ideescadeaux.core.model.notifications.NType;
import com.mosioj.ideescadeaux.core.model.repositories.UsersRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.*;
import org.junit.rules.TestName;

import java.io.File;
import java.sql.SQLException;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class WebAppTemplateTest {

    private final static Logger LOGGER = LogManager.getLogger(WebAppTemplateTest.class);
    protected final File root = new File(Objects.requireNonNull(getClass().getResource("/"))
                                                .getFile()).getParentFile()
                                                           .getParentFile();

    /** firefox@toto.com aka firefox */
    public static final int _OWNER_ID_ = 26;
    public static final User firefox = UsersRepository.getUser(_OWNER_ID_).orElseThrow();

    /** test@toto.com aka friend of Firefox */
    public static final int _FRIEND_ID_ = 4;
    public static final User friendOfFirefox = UsersRepository.getUser(_FRIEND_ID_).orElseThrow();

    /** moiautre@toto.com */
    public static final int _MOI_AUTRE_ = 8;
    public static final User moiAutre = UsersRepository.getUser(_MOI_AUTRE_).orElseThrow();

    /** The admin user. */
    public static final int _ADMIN_ID_ = 1;
    public static final User theAdmin = UsersRepository.getUser(_ADMIN_ID_).orElseThrow();

    protected static final int _JO3_ = 22;
    public static final User jo3 = UsersRepository.getUser(_JO3_).orElseThrow();

    protected static DataSourceIdKDo ds;

    @Rule
    public TestName name = new TestName();

    @Before
    public void printName() {
        LOGGER.info("============ Running " + name.getMethodName() + " ============");
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

    @After
    public void printLine() {
        int length = name.getMethodName().length() + "============ Running ".length() + " ============".length();
        StringBuilder sb = new StringBuilder();
        sb.append("=".repeat(Math.max(0, length)));
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
