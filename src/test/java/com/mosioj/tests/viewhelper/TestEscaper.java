package com.mosioj.tests.viewhelper;

import static org.junit.Assert.*;

import org.junit.Test;

import com.mosioj.viewhelper.Escaper;

public class TestEscaper {

	@Test
	public void testTextToHtml() {

		String[] sources = new String[] {
				"https://www.amazon.fr/Fujifilm-Appareil-Impression-Instantanée-Objectif/dp/B009ZM9R4O/ref=sr_1_2?ie=UTF8&qid=1481557517&sr=8-2&keywords=polaroid+fujifilm",
				"http://www.amazon.fr", "http://www.amazon.fr http://www.amazon.fr http://www.amazon.fr" };

		String[] expected = new String[] {
				"<a href=\"https://www.amazon.fr/Fujifilm-Appareil-Impression-Instantanée-Objectif/dp/B009ZM9R4O/ref=sr_1_2?ie=UTF8&qid=1481557517&sr=8-2&keywords=polaroid+fujifilm\">https://www.amazon.fr/Fujifilm[...]</a>",
				"<a href=\"http://www.amazon.fr\">http://www.amazon.fr</a>",
				"<a href=\"http://www.amazon.fr\">http://www.amazon.fr</a> <a href=\"http://www.amazon.fr\">http://www.amazon.fr</a> <a href=\"http://www.amazon.fr\">http://www.amazon.fr</a>"

		};

		for (int i = 0; i < sources.length; i++) {
			assertEquals(expected[i], Escaper.textToHtml(sources[i]));
			assertEquals(sources[i], Escaper.htmlToText(expected[i]));
		}
	}

}
