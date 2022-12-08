package com.mosioj.ideescadeaux.core.model.entities.notifications;

import com.mosioj.ideescadeaux.core.model.entities.IdeaGroup;
import com.mosioj.ideescadeaux.core.model.entities.User;
import com.mosioj.ideescadeaux.core.model.entities.text.Idee;

import java.text.MessageFormat;

/**
 * 50 caracters max.
 *
 * @author Jordan Mosio
 */
public enum NType {

    NO_IDEA("Lorsque vous n'avez pas d'idées", (u, i, g) -> "Vous n'avez pas encore d'idée !"),


    BOOKED_REMOVE("Un ami a supprimé une idée que vous aviez réservée",
                  (u, i, g) -> i.getOwner().getName() + " a supprimé son idée : \"" + i.getTextSummary(50) + "\""),


    GROUP_IDEA_SUGGESTION("On vous invite à un groupe sur une idée", (u, i, g) -> {
        final String link = "<a href=\"protected/detail_du_groupe?groupid=" + g.getId() + "\">ici</a>";
        return MessageFormat.format("{0} vous suggère un groupe sur l''idée \"{1}\". Cliquez {2} pour participer !",
                                    u.getName(),
                                    i.getTextSummary(50),
                                    link);
    }),


    JOIN_GROUP("Quelqu'un a rejoint un groupe où vous participez", (u, i, g) -> {
        final String link = "<a href=\"protected/detail_du_groupe?groupid=" + g.getId() + "\">ici</a>";
        return MessageFormat.format(
                "{0} a rejoint le groupe sur l''idée \"{1}\". Cliquez {2} pour voir le détail du groupe.",
                u.getName(),
                i.getTextSummary(50),
                link);
    }),


    LEAVE_GROUP("Quelqu'un a quitté un groupe où vous participez", (u, i, g) -> {
        final String link = "<a href=\"protected/detail_du_groupe?groupid=" + g.getId() + "\">ici</a>";
        return MessageFormat.format(
                "{0} a quitté le groupe sur l''idée \"{1}\". Cliquez {2} pour voir le détail du groupe.",
                u.getName(),
                i.getTextSummary(50),
                link);
    }),


    IS_IDEA_UP_TO_DATE("On vous demande si l'idée est à jour", (u, i, g) -> {

        String param = "idee=" + i.getId();
        String oui = MessageFormat.format("<li><a href=\"protected/confirmation_est_a_jour?{0}\">Oui !</a></li>",
                                          param);

        param = "idee=" + i.getId();
        String nonSupr = MessageFormat.format(
                "<li>Non... Il faudrait la <a class=\"removeIdea\" href=\"protected/remove_an_idea?{0}\">supprimer</a>.</li>",
                param);

        param = "id=" + i.getId();
        String nonModif = MessageFormat.format(
                "<li>Non... Je la <a href=\"protected/modifier_idee?{0}\">modifie</a> de suite !</li>",
                param);

        return MessageFormat.format(
                "Quelqu''un souhaiterait savoir si votre idée \"{0}\" est toujours à jour. <ul>{1}{2}{3}</ul>",
                i.getTextSummary(50),
                oui,
                nonSupr,
                nonModif);
    }),


    CONFIRMED_UP_TO_DATE("On vous confirme que l'idée est à jour",
                         (u, i, g) -> MessageFormat.format("{0} confirme que son idée \"{1}\" est maintenant à jour !",
                                                           i.getOwner().getName(),
                                                           i.getTextSummary(50))),


    NEW_RELATION_SUGGESTION("On vous suggère une demande d'ami",
                            (u, i, g) -> u.getName() +
                                         " vous a suggérer des amis ! <a href=\"protected/afficher_reseau\">Aller voir</a>..."),


    NEW_COMMENT_ON_IDEA("Quelqu'un a posté un nouveau commentaire sur une idée", (u, i, g) -> {
        final String link = "<a href=\"protected/idee_commentaires?idee=" + i.getId() + "\">le lire</a>";
        return MessageFormat.format("{0} a ajouté un nouveau commentaire sur l''idée \"{1}\". Aller {2}.",
                                    u.getName(),
                                    i.getTextSummary(50),
                                    link);
    }),


    NEW_QUESTION_ON_IDEA("Quelqu'un a posté une nouvelle question ou nouvelle réponse sur une idée", (u, i, g) -> {
        final String link = "<a href=\"protected/idee_questions?idee=" + i.getId() + "\">le lire</a>";
        return MessageFormat.format(
                "{0} a ajouté une nouvelle question / une nouvelle réponse sur l''idée \"{1}\". Aller {2}.",
                u.getName(),
                i.getTextSummary(50),
                link);
    }),


    NEW_QUESTION_TO_OWNER("Quelqu'un a posté une nouvelle question ou nouvelle réponse sur une idée", (u, i, g) -> {
        final String link = "<a href=\"protected/idee_questions?idee=" + i.getId() + "\">le lire</a>";
        return MessageFormat.format(
                "Quelqu''un a ajouté une nouvelle question / une nouvelle réponse sur l''idée \"{0}\". Aller {1}.",
                i.getTextSummary(50),
                link);
    }),


    MODIFIED_IDEA_BIRTHDAY_SOON("Un ami qui va bientôt fêter son anniversaire a modifié une idée", (u, i, g) -> {
        final String link = "<a href=\"protected/voir_liste?id=" + u.getId() + "\">sa liste</a>";
        return MessageFormat.format("{0} a modifié son idée \"{1}\". Consulter {2}.",
                                    u.getName(),
                                    i.getTextSummary(50),
                                    link);
    }),


    NEW_IDEA_BIRTHDAY_SOON("Un ami qui va bientôt fêter son anniversaire a ajouté une idée", (u, i, g) -> {
        final String link = "<a href=\"protected/voir_liste?id=" + u.getId() + "\">sa liste</a>";
        return MessageFormat.format("{0} a ajouté une nouvelle idée \"{1}\". Consulter {2}.",
                                    u.getName(),
                                    i.getTextSummary(50),
                                    link);
    }),


    FRIENDSHIP_DROPPED("Quelqu'un a supprimé votre relation",
                       (u, i, g) -> u.getName() + " a mis fin à votre relation..."),


    NEW_FRIENSHIP_REQUEST("Quelqu'un vous fait une demande d'ami", (u, i, g) -> {
        final String link = "<a href=\"protected/afficher_reseau\">Voir les demandes en cours</a>";
        return MessageFormat.format("{0} vous a envoyé une demande d''ami ! {1}.", u.getName(), link);
    }),


    ACCEPTED_FRIENDSHIP("Quelqu'un a accepté votre demande",
                        (u, i, g) -> u.getName() + " a accepté votre demande d'ami !"),


    REJECTED_FRIENDSHIP("Quelqu'un a refusé votre demande d'amis",
                        (u, i, g) -> u.getName() + " a refusé votre demande d'ami..."),


    RECURENT_IDEA_UNBOOK("Un amis précise qu'une idée déjà reçue est toujours d'actualité", (u, i, g) -> {
        final String link = "<a href=\"protected/voir_liste?id=" + u.getId() + "\">sa liste</a>";
        return MessageFormat.format("{0} souhaite toujours recevoir son idée \"{1}\". Consulter {2}.",
                                    u.getName(),
                                    i.getTextSummary(50),
                                    link);
    }),


    IDEA_ADDED_BY_FRIEND("Un ami vous a ajouté une idée", (u, i, g) -> {
        final String link = "<a href=\"protected/ajouter_idee\">ma liste</a>";
        return MessageFormat.format("{0} vous a ajouté une nouvelle idée \"{1}\". Consulter {2}.",
                                    u.getName(),
                                    i.getTextSummary(50),
                                    link);
    }),


    IDEA_RESTORED("Un amis a supprimé puis restoré une idée que vous aviez réservé.",
                  (u, i, g) -> i.getOwner().getName() + " a restoré son idée : \"" + i.getTextSummary(50) + "\".");

    /** The notification's description. */
    private final String description;

    /** Utility function to fetch the text given the specific notification parameters. */
    private final TextFetcher textFetcher;

    NType(String description, TextFetcher textFetcher) {
        this.description = description;
        this.textFetcher = textFetcher;
    }

    /**
     * @param user  The user parameter.
     * @param idea  The idea parameter.
     * @param group The group parameter.
     * @return This notification's text.
     */
    public String getText(final User user, final Idee idea, final IdeaGroup group) {
        return textFetcher.getText(user, idea, group);
    }

    /**
     * @return A new notification corresponding to this type, with no other parameters.
     */
    public Notification buildDefault() {
        return NotificationFactory.builder(this).build();
    }

    /**
     * @param userParameter The user parameter.
     * @return A new notification corresponding to this type, with a user parameter.
     */
    public Notification with(User userParameter) {
        return NotificationFactory.builder(this).withUserParameter(userParameter).build();
    }

    /**
     * @param userParameter  The user parameter.
     * @param ideaParameter  The idea parameter.
     * @param groupParameter The group parameter.
     * @return A new notification corresponding to this type, with a user parameter.
     */
    // TODO éventuellement supprimer le groupID => on le récupère depuis l'idée
    public Notification with(User userParameter, Idee ideaParameter, IdeaGroup groupParameter) {
        return NotificationFactory.builder(this)
                                  .withUserParameter(userParameter)
                                  .withIdeaParameter(ideaParameter)
                                  .withGroupParameter(groupParameter)
                                  .build();
    }

    /**
     * @param userParameter The user parameter.
     * @param ideaParameter The idea parameter.
     * @return A new notification corresponding to this type, with a user parameter.
     */
    public Notification with(User userParameter, Idee ideaParameter) {
        return NotificationFactory.builder(this)
                                  .withUserParameter(userParameter)
                                  .withIdeaParameter(ideaParameter)
                                  .build();
    }

    /**
     * @return The notification's description.
     */
    public String getDescription() {
        return description;
    }

    @FunctionalInterface
    private interface TextFetcher {
        String getText(final User user, final Idee idea, final IdeaGroup group);
    }

    public static boolean exists(String value) {
        try {
            NType.valueOf(value);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
