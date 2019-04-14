package com.mosioj.tests.model;

import static org.junit.Assert.assertEquals;

import java.sql.SQLException;

import org.junit.Test;
import org.springframework.mobile.device.Device;
import org.springframework.mobile.device.DevicePlatform;

import com.mosioj.model.Idee;
import com.mosioj.model.User;
import com.mosioj.model.table.Idees;
import com.mosioj.tests.TemplateTest;

public class TestIdee extends TemplateTest {

	private final class TestDevice implements Device {
		@Override
		public boolean isTablet() {
			return false;
		}

		@Override
		public boolean isNormal() {
			return false;
		}

		@Override
		public boolean isMobile() {
			return false;
		}

		@Override
		public DevicePlatform getDevicePlatform() {
			return null;
		}
	}

	@Test
	public void testGetSummary() {

		Idee idee = ideaFactory("toto");
		assertEquals("toto", idee.getTextSummary(4));
		assertEquals("...", idee.getTextSummary(3));

		idee = ideaFactory("totototo");
		assertEquals("totototo", idee.getTextSummary(8));
		assertEquals("toto...", idee.getTextSummary(7));

		idee = ideaFactory("toto &lt; &quot;6&quot;");
		assertEquals("toto...", idee.getTextSummary(7));
		assertEquals("toto &lt;...", idee.getTextSummary(10));

	}

	@Test
	public void testEnrichissement() throws SQLException {
		User owner = new User(1, "toto", "tutu@fneihfe.com", null);
		User booker = new User(32, "moi", "moi", null);
		Idee idee = new Idee(1, owner, "tutu", "", booker, 0, null, null, null, null, null, null, null, null, null);
		new Idees().fillAUserIdea(booker, idee, new TestDevice());
		assertEquals("booked_by_me_idea", idee.displayClass);
	}

	private Idee ideaFactory(String text) {
		return new Idee(1, null, text, "", null, 0, null, null, null, null, null, null, null, null, null);
	}

}
