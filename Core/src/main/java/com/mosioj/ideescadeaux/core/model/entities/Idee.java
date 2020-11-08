package com.mosioj.ideescadeaux.core.model.entities;

import com.google.gson.annotations.Expose;
import com.mosioj.ideescadeaux.core.utils.Escaper;
import com.mosioj.ideescadeaux.core.utils.date.MyDateFormatViewer;

import java.sql.Timestamp;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

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
    private final Priorite priorite;

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
     * @return The booking type - or NONE is no booking so far.
     */
    public BookingInformation.BookingType getBookingType() {
        return getBookingInformation().map(BookingInformation::getBookingType)
                                      .orElse(BookingInformation.BookingType.NONE);
    }

    /**
     * @return The owner of the surprise if it exists
     */
    public Optional<User> getSurpriseBy() {
        return Optional.ofNullable(surpriseBy);
    }

    /**
     * @return True if and only if this is a surprise.
     */
    public boolean isASurprise() {
        return getSurpriseBy().isPresent();
    }

    /**
     * @return All people that have booked this idea. Can be by direct booking, by a group, or by a partial booking.
     */
    public List<User> getBookers() {
        return getBookingInformation().map(bi -> bi.getBookers(getId())).orElse(Collections.emptyList());
    }

    /**
     * @return The priority of this idea.
     */
    public Priorite getPriorite() {
        return priorite;
    }

    /**
     * @return The idea identifier.
     */
    public int getId() {
        return id;
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
    public Optional<BookingInformation> getBookingInformation() {
        return Optional.ofNullable(bookingInformation);
    }

    /**
     * Mask booking information when providing the idea of the connected user.
     */
    public void maskBookingInformation() {
        bookingInformation = null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Idee idee = (Idee) o;
        return id == idee.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.valueOf(id);
    }
}
