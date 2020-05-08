package com.mosioj.ideescadeaux.core.model.entities;

import com.google.gson.annotations.Expose;
import com.mosioj.ideescadeaux.core.model.repositories.SousReservationRepository;
import com.mosioj.ideescadeaux.core.utils.Escaper;
import com.mosioj.ideescadeaux.core.utils.date.MyDateFormatViewer;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Idee {

    @Expose
    private final int id;

    @Expose
    public final User owner;

    @Expose
    private Categorie cat;

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
    private final BookingInformation bookingInformation;

    @Expose
    public String displayClass = "";

    /** Le text tel que rentré par l'utilisateur. N'est pas échappé. */
    private final String text;

    private final User bookingOwner;
    private final IdeaGroup group;
    private final Priorite priorite;
    private final Timestamp bookedOn;
    private final boolean isPartiallyBooked;
    public boolean hasComment = false;
    public boolean hasQuestion = false;
    public boolean hasAskedIfUpToDate = false;

    public Idee(int pId,
                User owner,
                String pText,
                User pBookingOwner,
                String image,
                Priorite priorite,
                Timestamp bookedOn,
                Timestamp lastModified,
                String isPartiallyBooked,
                IdeaGroup group,
                User surpriseBy) {
        id = pId;
        text = pText;
        htmlText = Escaper.interpreteMarkDown(text);
        bookingOwner = pBookingOwner;
        this.image = image;
        this.owner = owner;
        this.priorite = priorite;
        this.bookedOn = bookedOn;
        this.isPartiallyBooked = "Y".equals(isPartiallyBooked);
        this.surpriseBy = surpriseBy;
        modificationDate = MyDateFormatViewer.formatOrElse(lastModified, "-- on ne sait pas --");
        this.group = group;

        // Compute the type
        if (bookingOwner == null) {
            if (group == null) {
                if (this.isPartiallyBooked) {
                    bookingInformation = BookingInformation.fromAPartialReservation(bookedOn);
                } else {
                    bookingInformation = BookingInformation.noBooking();
                }
            } else {
                bookingInformation = BookingInformation.fromAGroup(group, bookedOn);
            }
        } else {
            bookingInformation = BookingInformation.fromASingleUser(bookingOwner, bookedOn);
        }
    }

    /**
     * Associates a new category with this idea.
     *
     * @param categorie The new category.
     */
    public void withCategorie(Categorie categorie) {
        this.cat = categorie;
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
    public List<User> getBookers() throws SQLException {
        List<User> bookers = new ArrayList<>();

        if (isBooked()) {
            // Ajout de la personne qui a réservé
            getBookingOwner().ifPresent(bookers::add);
            // Ajout de toutes les personnes qui sont dans le groupe
            getGroupKDO().ifPresent(g -> g.getShares().forEach(s -> bookers.add(s.getUser())));
        } else if (isPartiallyBooked()) {
            // Réservé par plusieurs personnes, mais pas dans un groupe
            SousReservationRepository.getSousReservation(getId()).forEach(s -> bookers.add(s.user));
        }

        return bookers;
    }

    /**
     * @return True if the idea is booked (by a owner, or a group)
     */
    public boolean isBooked() {
        return bookingOwner != null || group != null;
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

    public boolean isPartiallyBooked() {
        return isPartiallyBooked;
    }

    public String getBookingDate() {
        return MyDateFormatViewer.formatMine(bookedOn);
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
        return cat;
    }

    /**
     * @return The person's idea.
     */
    public User getOwner() {
        return owner;
    }

    /**
     * @return The person who booked this idea. Null if nobodies books it, or if a group did it.
     */
    public Optional<User> getBookingOwner() {
        return Optional.ofNullable(bookingOwner);
    }

    public Optional<IdeaGroup> getGroupKDO() {
        return Optional.ofNullable(group);
    }

    /**
     * @return The reservation group ID or -1 if it does not exist.
     */
    public int getGroupKDOId() {
        return getGroupKDO().map(IdeaGroup::getId).orElse(-1);
    }
}
