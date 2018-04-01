package com.mosioj.tests.model;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.mosioj.model.Idee;

public class TestIdee {

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

	private Idee ideaFactory(String text) {
		return new Idee(1, null, text, "", null, 0, null, null, null, null, null, null, null, null, null);
	}

}
