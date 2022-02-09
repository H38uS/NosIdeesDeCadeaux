package com.mosioj.ideescadeaux.webapp;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TestMetaDataWebApp extends WebAppTemplateTest {

    private static final Logger logger = LogManager.getLogger(TestMetaDataWebApp.class);

    @Test
    public void testAllLink() throws IOException {

        File web = new File(root, "WebContent");
        assertTrue(web.exists());

        Set<String> availableLinks = new HashSet<>();
        addCSSorJSPToReferences(availableLinks, web);
        addJavaToReference(availableLinks, new File(root, "src/main/java"));

        Set<String> referencedLinks = new HashSet<>();
        Map<String, String> referenceMap = new HashMap<>();
        lookForReferenceInJSP(referencedLinks, referenceMap, new File(web, "public"));
        lookForReferenceInJSP(referencedLinks, referenceMap, new File(web, "protected"));

        // Ajout des services
        availableLinks.add("protected/est_a_jour");
        availableLinks.add("protected/remove_an_idea");

        // Lien d'onglets JS
        availableLinks.add("#procurations");
        availableLinks.add("#notifications");
        availableLinks.add("#infos");

        // Tutoriels et doc
        availableLinks.add("https://commonmark.org/help/tutorial/");
        availableLinks.add("https://commonmark.org/");
        availableLinks.add("https://commonmark.org/help/");

        boolean hasError = false;
        for (String reference : referencedLinks) {
            if (!StringUtils.isBlank(reference) && !availableLinks.contains(reference)) {
                hasError = true;
                logger.error("Link " +
                             reference +
                             " is referenced but does not exist. Found in: " +
                             referenceMap.get(reference));
            }
        }

        assertFalse(hasError);

    }

    private void addJavaToReference(Set<String> availableLinks, File source) throws IOException {
        assertTrue(source.exists());
        for (File file : Objects.requireNonNull(source.listFiles())) {
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
            }
        }
    }

    private void lookForReferenceInJSP(Set<String> referencedLinks, Map<String, String> referenceMap, File pub) throws IOException {
        for (File file : Objects.requireNonNull(pub.listFiles())) {
            if (file.getName().endsWith(".jsp")) {
                String content = FileUtils.readFileToString(file);
                String sHref = "href=\"";
                while (content.contains(sHref)) {
                    content = content.substring(content.indexOf(sHref) + sHref.length());
                    String url = content.substring(0, content.indexOf("\""));
                    if (url.indexOf("?") > 0) {
                        url = url.substring(0, url.indexOf("?"));
                    }
                    if (url.contains("${") || url.contains("<c:url")) {
                        continue;
                    }
                    referencedLinks.add(url);
                    referenceMap.put(url, file.getName());
                }
            }
        }
    }

    private void addCSSorJSPToReferences(Set<String> availableLinks, File folder, String parent) {
        assertTrue(folder.exists());
        for (File file : Objects.requireNonNull(folder.listFiles())) {

            String name = file.getName();
            String newPath = parent == null || parent.isEmpty() ? name : parent + "/" + name;

            if (file.isDirectory()) {
                addCSSorJSPToReferences(availableLinks, file, newPath);
                continue;
            }
            if (name.endsWith(".css") || name.endsWith(".jsp") || name.endsWith(".ico")) {
                logger.debug(newPath);
                availableLinks.add(newPath);
            }
        }
    }

    private void addCSSorJSPToReferences(Set<String> availableLinks, File folder) {
        addCSSorJSPToReferences(availableLinks, folder, "");
    }

}
