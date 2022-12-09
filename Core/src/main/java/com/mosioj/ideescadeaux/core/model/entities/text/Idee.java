package com.mosioj.ideescadeaux.core.model.entities.text;

import com.google.gson.annotations.Expose;
import com.mosioj.ideescadeaux.core.model.entities.*;
import com.mosioj.ideescadeaux.core.utils.date.MyDateFormatViewer;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Entity(name = "IDEES")
public class Idee extends EntityWithText {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Expose
    private int id;

    @ManyToOne
    @JoinColumn(name = "owner")
    @Expose
    public User owner;

    @ManyToOne
    @JoinColumn(name = "reserve")
    public User bookedBy;

    @ManyToOne
    @JoinColumn(name = "type")
    @Expose
    public Categorie categorie;

    @ManyToOne
    @JoinColumn(name = "groupe_kdo_id")
    public IdeaGroup group;

    @ManyToOne
    @JoinColumn(name = "priorite")
    @Expose
    public Priority priority;

    @ManyToOne
    @JoinColumn(name = "surprise_par")
    @Expose
    private User surpriseBy;

    @Column(length = 100)
    @Expose
    public String image;

    @Column(name = "reserve_le")
    public LocalDateTime bookedOn;

    /** La date de modification dans un format lisible. */
    @Column(name = "modification_date")
    public LocalDateTime lastModified;

    // FIXME : référencer directement la sous réservation -- le mettre dans la nouvelle table booking info
    @Column(length = 1, name = "a_sous_reservation")
    public String isSubBooked = "N";

    @ManyToOne
    @JoinColumn(updatable = false, name = "cree_par")
    public User createdBy;

    @Column(updatable = false, name = "cree_le")
    @CreationTimestamp
    private LocalDateTime createdOn;

    @Column(length = 50)
    public String status;

    // ===========================
    // +++++++++++++++++++++++++++
    // Other fields, non DB stored
    // +++++++++++++++++++++++++++
    // ===========================

    /** La date de modification dans un format lisible. */
    @Transient
    @Expose
    private String modificationDate;

    @Transient
    @Expose
    private BookingInformation bookingInformation;

    @Transient
    @Expose
    public boolean hasBeenDeleted;

    /**
     * Class constructor.
     */
    public Idee() {
        // For Hibernate
    }

    @PostLoad
    public void postLoad() {
        this.modificationDate = MyDateFormatViewer.formatOrElse(lastModified, "-- on ne sait pas --");
        this.bookingInformation = BookingInformation.fromAllPossibilities(bookedBy, group, isSubBooked, bookedOn);
        this.hasBeenDeleted = "DELETED".equals(status);
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

    // Getters

    /**
     * @return True if this idea has been deleted.
     */
    public boolean isDeleted() {
        return hasBeenDeleted;
    }

    /**
     * @return The booking type - or NONE if no booking so far.
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
    public Priority getPriority() {
        return priority;
    }

    /**
     * @return The idea identifier.
     */
    public int getId() {
        return id;
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
     * @return The idea group if any.
     */
    public Optional<IdeaGroup> getGroup() {
        return Optional.ofNullable(group);
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
        private Priority priority;
        private LocalDateTime lastModified;
        private User surpriseBy;
        private BookingInformation bookingInformation;
        private User createdBy;

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
         * @param priority The priority of this idea.
         */
        public IdeaBuilder withPriority(Priority priority) {
            this.priority = priority;
            return this;
        }

        /**
         * @param lastModified The timestamp of the last modification of this idea.
         * @return The idea build.
         */
        public IdeaBuilder withLastModificationDate(LocalDateTime lastModified) {
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
         * @param createdBy The user that created this idea.
         * @return The idea build.
         */
        public IdeaBuilder withCreatedBy(User createdBy) {
            this.createdBy = createdBy;
            return this;
        }

        /**
         * @return Builds the idea with the given parameters.
         */
        public Idee build() {
            Idee idee = new Idee();
            idee.id = id;
            idee.owner = owner;
            idee.setText(text);
            idee.categorie = categorie;
            idee.image = image;
            idee.priority = priority;
            idee.lastModified = lastModified;
            idee.surpriseBy = surpriseBy;
            idee.bookingInformation = bookingInformation;
            idee.createdBy = createdBy;
            return idee;
        }

    }
}
