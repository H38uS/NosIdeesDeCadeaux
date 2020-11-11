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

    @Expose
    private final boolean hasBeenDeleted;

    /** Le text tel que rentré par l'utilisateur. N'est pas échappé. */
    private final String text;

    private Idee(int pId,
                 User owner,
                 String pText,
                 Categorie categorie,
                 String image,
                 Priorite priorite,
                 Timestamp lastModified,
                 User surpriseBy,
                 BookingInformation bookingInformation,
                 boolean hasBeenDeleted) {
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
        this.hasBeenDeleted = hasBeenDeleted;
    }

    /**
     * @return True if this idea has been deleted.
     */
    public boolean isDeleled() {
        return hasBeenDeleted;
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


    /**
     * @return The builder.
     */
    public static IdeaBuilder builder() {
        return new IdeaBuilder();
    }

    /**
     * Idea builder.
     */
    public static final class IdeaBuilder {

        // Idea constructor fields
        private int id;
        private User owner;
        private String text;
        private Categorie categorie;
        private String image;
        private Priorite priorite;
        private Timestamp lastModified;
        private User surpriseBy;
        private BookingInformation bookingInformation;
        private boolean hasBeenDelete;

        /**
         * @param id The identifier of this idea.
         * @return The idea build.
         */
        public IdeaBuilder withId(int id) {
            this.id = id;
            return this;
        }

        /**
         * @param owner The owner of this idea.
         * @return The idea build.
         */
        public IdeaBuilder withOwner(User owner) {
            this.owner = owner;
            return this;
        }

        /**
         * @param text The text of this idea.
         * @return The idea build.
         */
        public IdeaBuilder withText(String text) {
            this.text = text;
            return this;
        }

        /**
         * @param categorie The categorie of this idea.
         * @return The idea build.
         */
        public IdeaBuilder withCategory(Categorie categorie) {
            this.categorie = categorie;
            return this;
        }

        /**
         * @param image The picture of this idea.
         * @return The idea build.
         */
        public IdeaBuilder withPicture(String image) {
            this.image = image;
            return this;
        }

        /**
         * @param priorite The priority of this idea.
         * @return The idea build.
         */
        public IdeaBuilder withPriority(Priorite priorite) {
            this.priorite = priorite;
            return this;
        }

        /**
         * @param lastModified The timestamp of the last modification of this idea.
         * @return The idea build.
         */
        public IdeaBuilder withLastModificationDate(Timestamp lastModified) {
            this.lastModified = lastModified;
            return this;
        }

        /**
         * @param surpriseBy The user that created this surprise.
         * @return The idea build.
         */
        public IdeaBuilder withSurpriseOwner(User surpriseBy) {
            this.surpriseBy = surpriseBy;
            return this;
        }

        /**
         * @param bookingInformation The booking information of this idea.
         * @return The idea build.
         */
        public IdeaBuilder withBookingInformation(BookingInformation bookingInformation) {
            this.bookingInformation = bookingInformation;
            return this;
        }

        /**
         * @param hasBeenDelete True if the idea has been deleted.
         * @return The idea build.
         */
        public IdeaBuilder hasBeenDelete(boolean hasBeenDelete) {
            this.hasBeenDelete = hasBeenDelete;
            return this;
        }

        /**
         * @return Builds the idea with the given parameters.
         */
        public Idee build() {
            return new Idee(id,
                            owner,
                            text,
                            categorie,
                            image,
                            priorite,
                            lastModified,
                            surpriseBy,
                            bookingInformation,
                            hasBeenDelete);
        }

    }
}
