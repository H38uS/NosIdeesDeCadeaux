package com.mosioj.ideescadeaux.core.utils;

import com.vdurmont.emoji.EmojiParser;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

import java.util.Random;

public class Escaper {

    private static final Logger logger = LogManager.getLogger(Escaper.class);

    /**
     * Markdown processor
     */
    private static final Parser parser = Parser.builder().build();

    /**
     * Markdown renderer
     */
    private static final HtmlRenderer renderer = HtmlRenderer.builder().escapeHtml(true).sanitizeUrls(true).build();

    private Escaper() {
        // Forbidden
    }

    private static final String HTTPS_REGEX = "(^|[^\"(\\[])(https?://[^\\s]*)";
    private static final String HTTPS_REPLACEMENT = "$1[$2]($2)";

    /**
     *
     * @param initialText The initial text containing smileys.
     * @return The string with all smileys transformed to codes.
     */
    public static String transformSmileyToCode(final String initialText) {
        return EmojiParser.parseToAliases(initialText);
    }

    /**
     *
     * @param initialText The initial text containing codes.
     * @return The string with all codes transformed to smileys.
     */
    public static String transformCodeToSmiley(final String initialText) {
        return EmojiParser.parseToUnicode(initialText);
    }

    /**
     * @param initialText The initial idea text generated by the JQuery Text Editor plugin.
     * @return The idea text escaped (script tag etc.).
     */
    public static String escapeIdeaText(String initialText) {
        String val = initialText.replaceAll("(?i)<a href=\"?([^\\s\"]*)\"?( target=\"?_blank\"?)?>([^\\s]*)</a>",
                                            "<a href=\"$1\" target=\"_blank\">$3</a>");
        val = val.replaceAll(HTTPS_REGEX, HTTPS_REPLACEMENT);
        return val;
    }

    /**
     * @param initialName The initial upload name.
     * @return The computed name to use, with the png extension.
     */
    public static String computeImageName(String initialName) {

        logger.debug("Initial image name: " + initialName);
        String fileName = initialName;
        if (fileName.contains(".")) {
            // Drop all of them
            fileName = fileName.substring(0, fileName.indexOf("."));
        }
        if (fileName.isEmpty()) {
            fileName = "IMG";
        }

        Random r = new Random();
        int id = Math.abs(r.nextInt());
        int maxSize = 60;
        if (fileName.length() > maxSize) {
            fileName = fileName.substring(0, maxSize - 4) + "_" + id;
        } else {
            fileName = fileName + "_" + id;
        }

        fileName = fileName.replaceAll("'", "");
        fileName = fileName.replaceAll("[éêè]", "e");
        fileName = fileName.replaceAll("î", "i");
        fileName = fileName.replaceAll("ô", "o");
        fileName = fileName.replaceAll("[ùû]", "u");
        fileName = fileName.replaceAll("[àâ]", "a");
        fileName = StringEscapeUtils.escapeHtml4(fileName);

        logger.debug("Computed image name: " + fileName + ".png");
        return fileName + ".png";
    }

    /**
     * @param source The source string to interpret
     * @return The HTML interpreted mark down text corresponding to the source
     */
    public static String interpreteMarkDown(String source) {
        return renderer.render(parser.parse(source));
    }

}
