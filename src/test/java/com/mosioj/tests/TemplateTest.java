package com.mosioj.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.sql.SQLException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.rules.TestName;

import com.mosioj.model.User;
import com.mosioj.model.table.Comments;
import com.mosioj.model.table.GroupIdea;
import com.mosioj.model.table.Idees;
import com.mosioj.model.table.Notifications;
import com.mosioj.model.table.Questions;
import com.mosioj.model.table.UserParameters;
import com.mosioj.model.table.UserRelationRequests;
import com.mosioj.model.table.UserRelations;
import com.mosioj.model.table.Users;
import com.mosioj.notifications.NotificationActivation;
import com.mosioj.notifications.NotificationType;
import com.mosioj.utils.database.DataSourceIdKDo;
import com.mysql.cj.jdbc.MysqlDataSource;

public class TemplateTest {

	private final static Logger LOGGER = LogManager.getLogger(TemplateTest.class);
	protected final File root = new File(getClass().getResource("/").getFile()).getParentFile().getParentFile();

	/**
	 * firefox@toto.com aka firefox
	 */
	protected static final int _OWNER_ID_ = 26;
	protected User firefox;
	
	/**
	 * test@toto.com aka friend of Firefox
	 */
	protected static final int _FRIEND_ID_ = 4;
	protected User friendOfFirefox;
	
	/**
	 * moiautre@toto.com
	 */
	protected static final int _MOI_AUTRE_ = 8;
	protected User moiAutre;
	
	/**
	 * The admin user.
	 */
	protected static final int _ADMIN_ID_ = 1;

	protected final Idees idees = new Idees();
	protected final Users users = new Users();
	protected static final UserParameters userParameters = new UserParameters();
	protected final Notifications notif = new Notifications();
	protected final UserRelations userRelations = new UserRelations();
	protected final UserRelationRequests userRelationRequests = new UserRelationRequests();
	protected final GroupIdea groupIdea = new GroupIdea();
	protected final Questions questions = new Questions();
	protected final Comments comments = new Comments();
	
	protected static DataSourceIdKDo ds;
	
	public TemplateTest() {
		try {
			friendOfFirefox = users.getUser(_FRIEND_ID_);
			firefox = users.getUser(_OWNER_ID_);
			moiAutre = users.getUser(_MOI_AUTRE_);
		} catch (SQLException e) {
			fail("Fail to retrieve the friend of Firefox");
		}
	}

	@Rule
	public TestName name = new TestName();

	@Before
	public void printName() {
		LOGGER.info("============ Running " + name.getMethodName() + " ============");
	}
	
	@BeforeClass
	public static void init() throws SQLException {
		
		MysqlDataSource dataSource = new MysqlDataSource();
		
		dataSource.setDatabaseName("test_ideeskdos");
		dataSource.setUser("mosioj");
		dataSource.setPassword("tuaD50Kv2jguyX5ncokK");
		dataSource.setURL("jdbc:mysql://192.168.1.44/test_ideeskdos?serverTimezone=Europe/Paris");

		DataSourceIdKDo.setDataSource(dataSource);
		ds = new DataSourceIdKDo();
		String email = ds.selectString("select email from USERS where id = ?", 3);
		assertEquals("ymosio@wanadzdzdzdoo.fr", email);
		
		for (NotificationType type : NotificationType.values()) {
			new UserRelations().getAllUsersInRelation(new Users().getUser(_OWNER_ID_)).forEach(u -> {
				try {
					userParameters.insertUpdateParameter(u, type.name(), NotificationActivation.SITE.name());
				} catch (SQLException e) {
					e.printStackTrace();
					fail();
				}
			});
			userParameters.insertUpdateParameter(new User(_OWNER_ID_,"","",""), type.name(), NotificationActivation.SITE.name());
		}
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

	protected void assertNotifDoesNotExists(int notifId) throws SQLException {
		assertEquals(0, ds.selectCountStar("select count(*) from NOTIFICATIONS where id = ?", notifId));
	}

	protected void assertNotifDoesExists(int notifId) throws SQLException {
		assertEquals(1, ds.selectCountStar("select count(*) from NOTIFICATIONS where id = ?", notifId));
	}
}
