package com.mosioj.ideescadeaux.core.utils;

import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TestEscaper {

    @Test
    public void testComputeNewName() {

        String[] sources = new String[]{
                "mon_fichier.png",
                "avec_accentéèêîûôù.jpg"
        };

        String[] expected = new String[]{
                "mon_fichier.png",
                "avec_accenteeeiuou.jpg"
        };

        for (int i = 0; i < sources.length; i++) {
            String name = expected[i].substring(0, expected[i].lastIndexOf("."));
            String computed = Escaper.computeImageName(sources[i]);
            Assert.assertEquals(name, computed.substring(0, computed.lastIndexOf("_")));
            Assert.assertTrue(computed.contains(name));
        }
    }

    @Test
    public void testIdeaEscape() {

        String[] sources = new String[]{
                "<a href=\"http://tutu.com\">tutu.com</a>",
                "<a hReF=\"http://tutu.com\">tutu.com</a>toto<a href=\"http://tutu.com\">tutu.com</a><a href=\"http://tutu.com\">tutu.com</a>",
                "<a href=\"http://tutu.com\" target=\"_blank\">tutu.com</a>",
                "un lien https://www.liveffn.com/cgi-bin/resultats.php?competition=62933&amp;langue=fra et voilà",
                "http://www.amazon.fr",
                "hihihttp://www.amazon.fr",
                "http://www.amazon.fr http://www.amazon.fr http://www.amazon.fr",
                "https://phototrend.fr/wp-content/uploads/2014/12/jpeg-1.jpg",
                "[image](http://ma.super.jpeg.image.jpeg)",
                "https://www.decathlon.fr/p/ceinture-porte-bidon-de-ski-de-fond-xc-s-belt-100-adulte/_/R-p-180257?mc=8525020&gclid=Cj0KCQiA2NaNBhDvARIsAEw55hiTobURp-hjrulXH2wY4HxoeIONYTlty-TBZuEU74STIQ32EA0UWIwaAh31EALw_wcB"
        };

        String[] expected = new String[]{
                "<a href=\"http://tutu.com\" target=\"_blank\">tutu.com</a>",
                "<a href=\"http://tutu.com\" target=\"_blank\">tutu.com</a>toto<a href=\"http://tutu.com\" target=\"_blank\">tutu.com</a><a href=\"http://tutu.com\" target=\"_blank\">tutu.com</a>",
                "<a href=\"http://tutu.com\" target=\"_blank\">tutu.com</a>",
                "un lien [https://www.liveffn.com/cgi-bin/resultats.php?co[...]e=fra](https://www.liveffn.com/cgi-bin/resultats.php?competition=62933&amp;langue=fra) et voilà",
                "[http://www.amazon.fr](http://www.amazon.fr)",
                "hihi[http://www.amazon.fr](http://www.amazon.fr)",
                "[http://www.amazon.fr](http://www.amazon.fr) [http://www.amazon.fr](http://www.amazon.fr) [http://www.amazon.fr](http://www.amazon.fr)",
                "![https://phototrend.fr/wp-content/uploads/2014/12[...]1.jpg](https://phototrend.fr/wp-content/uploads/2014/12/jpeg-1.jpg)",
                "![image](http://ma.super.jpeg.image.jpeg)",
                "[https://www.decathlon.fr/p/ceinture-porte-bidon-[...]w_wcB](https://www.decathlon.fr/p/ceinture-porte-bidon-de-ski-de-fond-xc-s-belt-100-adulte/_/R-p-180257?mc=8525020&gclid=Cj0KCQiA2NaNBhDvARIsAEw55hiTobURp-hjrulXH2wY4HxoeIONYTlty-TBZuEU74STIQ32EA0UWIwaAh31EALw_wcB)"
        };

        for (int i = 0; i < sources.length; i++) {
            assertEquals("Plantage au numéro " + i, expected[i], Escaper.escapeIdeaText(sources[i]));
        }
    }
}
