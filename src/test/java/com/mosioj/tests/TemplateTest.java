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
import com.mosioj.model.table.Idees;
import com.mosioj.model.table.Notifications;
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
	 * firefox@toto.com
	 */
	protected static final int _OWNER_ID_ = 26;
	
	/**
	 * test@toto.com
	 */
	protected static final int _FRIEND_ID_ = 4;
	
	/**
	 * moiautre@toto.com
	 */
	protected static final int _MOI_AUTRE_ = 8;

	/**
	 * test@toto.com
	 */
	protected User friendOfFirefox;

	protected final Idees idees = new Idees();
	protected final Users users = new Users();
	protected static final UserParameters userParameters = new UserParameters();
	protected final Notifications notif = new Notifications();
	protected final UserRelations userRelations = new UserRelations();
	protected final UserRelationRequests userRelationRequests = new UserRelationRequests();
	
	protected static DataSourceIdKDo ds;
	
	public TemplateTest() {
		try {
			friendOfFirefox = users.getUser(_FRIEND_ID_);
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
			userParameters.insertUpdateParameter(_OWNER_ID_, type.name(), NotificationActivation.SITE.name());
			userParameters.insertUpdateParameter(_FRIEND_ID_, type.name(), NotificationActivation.SITE.name());
			userParameters.insertUpdateParameter(_MOI_AUTRE_, type.name(), NotificationActivation.SITE.name());
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
}
