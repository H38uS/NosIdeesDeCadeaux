package com.mosioj.tests.viewhelper;

import static org.junit.Assert.*;

import org.junit.Test;

import com.mosioj.viewhelper.Escaper;

public class TestEscaper {

	@Test
	public void testTextToHtml() {

		String[] sources = new String[] {
				"http://www.oxybul.com/jeux-de-societe/jeux-de-cooperation/jeu-de-societe-mini-verger/produit/145163",
				"https://www.amazon.fr/Repos-Production-SEC01-Secrets-English/dp/B074MJKXB5/ref=sr_1_1?s=toys&ie=UTF8&qid=1510504034&sr=1-1&keywords=secrets+bruno+faidutti",
				"https://www.amazon.fr/Fujifilm-Appareil-Impression-Instantanée-Objectif/dp/B009ZM9R4O/ref=sr_1_2?ie=UTF8&qid=1481557517&sr=8-2&keywords=polaroid+fujifilm",
				"http://www.amazon.fr", "http://www.amazon.fr http://www.amazon.fr http://www.amazon.fr" };

		String[] expected = new String[] {
				"<a href=\"http://www.oxybul.com/jeux-de-societe/jeux-de-cooperation/jeu-de-societe-mini-verger/produit/145163\" target=\"_blank\">http://www.oxybul.com/jeux-de-[...]</a>",
				"<a href=\"https://www.amazon.fr/Repos-Production-SEC01-Secrets-English/dp/B074MJKXB5/ref=sr_1_1?s=toys&ie=UTF8&qid=1510504034&sr=1-1&keywords=secrets+bruno+faidutti\" target=\"_blank\">https://www.amazon.fr/Repos-Pr[...]</a>",
				"<a href=\"https://www.amazon.fr/Fujifilm-Appareil-Impression-Instantanée-Objectif/dp/B009ZM9R4O/ref=sr_1_2?ie=UTF8&qid=1481557517&sr=8-2&keywords=polaroid+fujifilm\" target=\"_blank\">https://www.amazon.fr/Fujifilm[...]</a>",
				"<a href=\"http://www.amazon.fr\" target=\"_blank\">http://www.amazon.fr</a>",
				"<a href=\"http://www.amazon.fr\" target=\"_blank\">http://www.amazon.fr</a> <a href=\"http://www.amazon.fr\" target=\"_blank\">http://www.amazon.fr</a> <a href=\"http://www.amazon.fr\" target=\"_blank\">http://www.amazon.fr</a>"

		};

		for (int i = 0; i < sources.length; i++) {
			assertEquals(expected[i], Escaper.textToHtml(sources[i]));
			assertEquals(sources[i], Escaper.htmlToText(expected[i]));
		}
	}
	
	@Test
	public void testComputeNewName() {
		
		String[] sources = new String[] {
				"mon_fichier.png",
				"avec_accentéèêîûôù.jpg"
		};

		String[] expected = new String[] {
				"mon_fichier.png",
				"avec_accenteeeiuou.jpg"
		};

		for (int i = 0; i < sources.length; i++) {
			String name = expected[i].substring(0, expected[i].lastIndexOf("."));
			String computed = Escaper.computeImageName(sources[i]);
			assertEquals(name, computed.substring(0, computed.lastIndexOf("_")));
			assertTrue(computed.contains(name));
		}
	}

	@Test
	public void testIdeaEscape() {

		String[] sources = new String[] {
				"<a href=\"http://tutu.com\">tutu.com</a>",
				"<a hReF=\"http://tutu.com\">tutu.com</a>toto<a href=\"http://tutu.com\">tutu.com</a><a href=\"http://tutu.com\">tutu.com</a>",
				"<a href=\"http://tutu.com\" target=\"_blank\">tutu.com</a>",
				"<script>alert('toto');</script>",
				"<sCrIpt>alert('toto');</sCripT>",
				"http://www.amazon.fr", "http://www.amazon.fr http://www.amazon.fr http://www.amazon.fr"
		};

		String[] expected = new String[] {
				"<a href=\"http://tutu.com\" target=\"_blank\">tutu.com</a>",
				"<a href=\"http://tutu.com\" target=\"_blank\">tutu.com</a>toto<a href=\"http://tutu.com\" target=\"_blank\">tutu.com</a><a href=\"http://tutu.com\" target=\"_blank\">tutu.com</a>",
				"<a href=\"http://tutu.com\" target=\"_blank\">tutu.com</a>",
				">alert('toto');>",
				">alert('toto');>",
				"http://www.amazon.fr", "http://www.amazon.fr http://www.amazon.fr http://www.amazon.fr"
		};

		for (int i = 0; i < sources.length; i++) {
			assertEquals(expected[i], Escaper.escapeIdeaText(sources[i]));
		}
	}
}
