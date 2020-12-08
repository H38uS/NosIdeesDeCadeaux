package com.mosioj.ideescadeaux.core.model.notifications;

import com.mosioj.ideescadeaux.core.model.entities.Idee;
import org.junit.Test;

import static com.mosioj.ideescadeaux.core.model.notifications.NType.NEW_QUESTION_TO_OWNER;
import static org.junit.Assert.assertEquals;

public class NTypeTest {

    @Test
    public void testMessages() {

        assertEquals(
                "Quelqu'un a ajouté une nouvelle question / une nouvelle réponse sur l'idée \"toto\". Aller <a href=\"protected/idee_questions?idee=42\">le lire</a>.",
                NEW_QUESTION_TO_OWNER.getText(null, Idee.builder().withText("toto").withId(42).build(), null));
    }


}