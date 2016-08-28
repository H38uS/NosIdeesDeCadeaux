package com.mosioj.tests;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

public class TestMetaData extends TemplateTest {

	private static final Logger logger = LogManager.getLogger(TestMetaData.class);

	@Test
	public void testAllFormsHaveCRF() throws IOException {
		logger.info(root);

		File web = new File(root, "WebContent");
		assertTrue(web.exists());

		File protect = new File(web, "protected");
		File pub = new File(web, "public");
		assertTrue(protect.exists());
		assertTrue(pub.exists());

		checkForms(protect);
		checkForms(pub);
	}

	private void checkForms(File folder) throws IOException {

		FileFilter filter = new WildcardFileFilter("*.jsp");

		for (File file : folder.listFiles(filter)) {

			String content = FileUtils.readFileToString(file);
			logger.debug("Checking " + file + "...");

			String sForm = "<form";
			while (content.contains(sForm)) {

				content = content.substring(content.indexOf(sForm) + 5);
				assertTrue(content.contains("</form>"));
				
				int endOfForm = content.indexOf("</form>");
				String form = content.substring(0, endOfForm);
				assertFalse(form.contains("</form>"));
				content.substring(endOfForm + 6);

				assertTrue(form.contains("<input type=\"hidden\" name=\"${_csrf.parameterName}\" value=\"${_csrf.token}\" />"));
			}
		}
	}

}
