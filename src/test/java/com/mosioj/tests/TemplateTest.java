package com.mosioj.tests;

import java.io.File;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TestName;

public class TemplateTest {

	private final static Logger LOGGER = LogManager.getLogger(TemplateTest.class);
	protected final File root = new File(getClass().getResource("/").getFile()).getParentFile().getParentFile();

	@Rule
	public TestName name = new TestName();

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
}
