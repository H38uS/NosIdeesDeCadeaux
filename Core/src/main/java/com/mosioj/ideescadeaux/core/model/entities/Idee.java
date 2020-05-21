package com.mosioj.ideescadeaux.core.model.entities;

import com.google.gson.annotations.Expose;
import com.mosioj.ideescadeaux.core.utils.Escaper;
import com.mosioj.ideescadeaux.core.utils.date.MyDateFormatViewer;

import java.sql.Timestamp;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.List;

public class Idee {

    @Expose
    private final int id;

    @Expose
    public final User owner;

    @Expose
    private final Categorie categorie;

    @Expose
    private final String image;

    /** Le text échappé de l'utilisateur, converti en markdown. */
    @Expose
    private final String htmlText;

    /** La date de modification dans un format lisible. */
    @Expose
    private final String modificationDate;

    @Expose
    private final User surpriseBy;

    @Expose
    private BookingInformation bookingInformation;

    @Expose
    public String displayClass = "";

    @Expose
    private final Priorite priorite;

    @Expose
    public boolean hasAskedIfUpToDate = false;

    @Expose
    public boolean hasComment = false;

    @Expose
    public boolean hasQuestion = false;

    /** Le text tel que rentré par l'utilisateur. N'est pas échappé. */
    private final String text;

    public Idee(int pId,
                User owner,
                String pText,
                Categorie categorie,
                String image,
                Priorite priorite,
                Timestamp lastModified,
                User surpriseBy,
                BookingInformation bookingInformation) {
        id = pId;
        text = pText;
        this.categorie = categorie;
        htmlText = Escaper.interpreteMarkDown(text);
        this.image = image;
        this.owner = owner;
        this.priorite = priorite;
        this.surpriseBy = surpriseBy;
        modificationDate = MyDateFormatViewer.formatOrElse(lastModified, "-- on ne sait pas --");
        this.bookingInformation = bookingInformation;
    }

    /**
     * @return the hasAskedIfUpToDate
     */
    public boolean hasAskedIfUpToDate() {
        return hasAskedIfUpToDate;
    }

    /**
     * @return The owner of the surprise if it exists
     */
    public User getSurpriseBy() {
        return surpriseBy;
    }

    /**
     * @return True if and only if there are some comments on this idea
     */
    public boolean hasComment() {
        return hasComment;
    }

    /**
     * @return True if and only if there are some questions on this idea
     */
    public boolean hasQuestion() {
        return hasQuestion;
    }

    /**
     * @return All people that have booked this idea. Can be by direct booking, by a group, or by a partial booking.
     */
    public List<User> getBookers() {
        if (getBookingInformation() != null) {
            return getBookingInformation().getBookers(getId());
        }
        return Collections.emptyList();
    }

    public Priorite getPriorite() {
        return priorite;
    }

    /**
     * @return The css class to use for this idea.
     */
    public String getDisplayClass() {
        return displayClass;
    }

    public int getId() {
        return id;
    }

    /**
     * @return The last modified date as a readable string.
     */
    public String getModificationDate() {
        return modificationDate;
    }

    /**
     * @return The text displayed in textarea, with \n.
     */
    public String getText() {
        return text;
    }

    /**
     * @param maxLength Maximum number of character for the summary.
     * @return The idea text, with a maximum of maxLength characters.
     */
    public String getTextSummary(int maxLength) {

        String initial = getText();
        if (initial.length() > maxLength) {
            StringBuilder sb = new StringBuilder();
            boolean needSemiColon = false;
            for (int i = 0; i < maxLength - 3; i++) {
                char c = initial.charAt(i);
                sb.append(c);
                if (needSemiColon && c == ';') {
                    needSemiColon = false;
                }
                if (c == '&') {
                    needSemiColon = true;
                }
            }
            int i = maxLength - 3;
            while (needSemiColon && i != initial.length()) {
                char c = initial.charAt(i);
                sb.append(c);
                i++;
                if (c == ';') {
                    break;
                }
            }
            sb.append("...");
            return sb.toString();
        }

        return initial;
    }

    /**
     * @return The idea text stored in DB, that will be presented to the browser.
     */
    public String getHtml() {
        return htmlText;
    }

    public String getImage() {
        return image;
    }

    public String getImageSrcSmall() {
        return MessageFormat.format("small/{0}", image);
    }

    public String getImageSrcLarge() {
        return MessageFormat.format("large/{0}", image);
    }

    public Categorie getCategory() {
        return categorie;
    }

    /**
     * @return The person's idea.
     */
    public User getOwner() {
        return owner;
    }

    /**
     * @return The booking information of this idea.
     */
    public BookingInformation getBookingInformation() {
        return bookingInformation;
    }

    /**
     * Mask booking information when providing the idea of the connected user.
     */
    public void maskBookingInformation() {
        bookingInformation = null;
    }
}
