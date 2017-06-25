package com.mosioj.tests;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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

	@Test
	public void testAllLink() throws IOException {

		File web = new File(root, "WebContent");
		assertTrue(web.exists());

		Set<String> availableLinks = new HashSet<String>();
		addJSPToReferences(availableLinks, new File(web, "protected"));
		addJSPToReferences(availableLinks, new File(web, "public"));
		addJavaToReference(availableLinks, new File(root, "src/main/java"));

		Set<String> referencedLinks = new HashSet<String>();
		Map<String, String> referenceMap = new HashMap<String, String>();
		lookForReferenceInJSP(referencedLinks, referenceMap, new File(web, "public"));
		lookForReferenceInJSP(referencedLinks, referenceMap, new File(web, "protected"));

		boolean hasError = false;
		for (String reference : referencedLinks) {
			if (!availableLinks.contains(reference)) {
				hasError = true;
				logger.error("Link " + reference + " is referenced but does not exist. Found in: " + referenceMap.get(reference));
			}
		}

		assertFalse(hasError);

	}

	private void addJavaToReference(Set<String> availableLinks, File source) throws IOException {

		assertTrue(source.exists());
		for (File file : source.listFiles()) {
			if (file.isDirectory()) {
				addJavaToReference(availableLinks, file);
				continue;
			}
			if (file.isFile()) {
				String content = FileUtils.readFileToString(file);
				String webServlet = "@WebServlet(\"/";
				if (content.contains(webServlet)) {
					content = content.substring(content.indexOf(webServlet) + webServlet.length());
					String url = content.substring(0, content.indexOf("\""));
					availableLinks.add(url);
				}
				continue;
			}
		}
	}

	private void lookForReferenceInJSP(Set<String> referencedLinks, Map<String, String> referenceMap, File pub) throws IOException {
		for (File file : pub.listFiles()) {
			if (file.getName().endsWith(".jsp")) {
				String content = FileUtils.readFileToString(file);
				String sHref = "href=\"";
				while (content.contains(sHref)) {
					content = content.substring(content.indexOf(sHref) + sHref.length());
					String url = content.substring(0, content.indexOf("\""));
					if (url.indexOf("?") > 0) {
						url = url.substring(0, url.indexOf("?"));
					}
					if (url.equals("${pageContext.request.contextPath}/logout")) {
						continue;
					}
					referencedLinks.add(url);
					referenceMap.put(url, file.getName());

					// TODO : Test add URL in form
				}
			}
		}
	}

	private void addJSPToReferences(Set<String> availableLinks, File folder) {
		assertTrue(folder.exists());
		for (File file : folder.listFiles()) {
			if (file.getName().endsWith(".jsp")) {
				availableLinks.add(folder.getName() + "/" + file.getName());
			}
		}
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
				content = content.substring(endOfForm + 6);

				if (form.contains("enctype=\"multipart/form-data\"")) {
					continue;
				}

				assertTrue(form.contains("<input type=\"hidden\" name=\"${_csrf.parameterName}\" value=\"${_csrf.token}\" />"));
			}
		}
	}

}
