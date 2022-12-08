package com.mosioj.ideescadeaux.webapp.entities;

import com.google.gson.annotations.Expose;
import com.mosioj.ideescadeaux.core.model.entities.BookingInformation;
import com.mosioj.ideescadeaux.core.model.entities.Priority;
import com.mosioj.ideescadeaux.core.model.entities.SousReservationEntity;
import com.mosioj.ideescadeaux.core.model.entities.User;
import com.mosioj.ideescadeaux.core.model.entities.text.Idee;
import com.mosioj.ideescadeaux.core.model.repositories.CommentsRepository;
import com.mosioj.ideescadeaux.core.model.repositories.IsUpToDateQuestionsRepository;
import com.mosioj.ideescadeaux.core.model.repositories.QuestionsRepository;
import com.mosioj.ideescadeaux.core.model.repositories.SousReservationRepository;
import com.mosioj.ideescadeaux.webapp.utils.ParametersUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.mobile.device.Device;

import java.sql.SQLException;

public class DecoratedWebAppIdea {

    /** Class logger */
    private static final Logger logger = LogManager.getLogger(DecoratedWebAppIdea.class);

    @Expose
    private final Idee idee;

    @Expose
    private final boolean hasAskedIfUpToDate;

    @Expose
    private final boolean hasComment;

    @Expose
    private final boolean hasQuestion;

    @Expose
    private final String displayClass;

    /**
     * Class constructor.
     *
     * @param idee          The idea.
     * @param connectedUser The user connected when doing this request.
     * @param device        The device used by this user.
     */
    public DecoratedWebAppIdea(Idee idee, User connectedUser, Device device) {

        final boolean isOwnerByConnectedUser = connectedUser.equals(idee.getOwner());
        boolean tempComment = computeHasComment(isOwnerByConnectedUser, idee);
        boolean tempQuestion = false;
        try {
            tempQuestion = QuestionsRepository.getNbQuestions(idee.getId()) > 0;
        } catch (SQLException e) {
            logger.error("Une erreur est survenue...", e);
        }

        this.idee = idee;
        hasComment = tempComment;
        hasQuestion = tempQuestion;
        hasAskedIfUpToDate = IsUpToDateQuestionsRepository.associationExists(idee, connectedUser);

        // calcul de la display class
        displayClass = computeDisplayClass(idee, connectedUser);

        if (isOwnerByConnectedUser) {
            idee.maskBookingInformation();
        }

        if (device.isMobile()) {
            Priority priority = idee.getPriority();
            if (priority != null && priority.getImage() != null) {
                priority.image = priority.getImage().replaceAll("width=\"[0-9]+px\"",
                                                                "width=\"" +
                                                                ParametersUtils.MOBILE_PICTURE_WIDTH +
                                                                "px\"");
            }
        }
    }

    private boolean computeHasComment(boolean isOwnerByConnectedUser, Idee idee) {
        if (isOwnerByConnectedUser) {
            return false;
        }
        return CommentsRepository.getNbComments(idee.getId()) > 0;
    }

    /**
     * @param idee          The idea.
     * @param connectedUser The connected user.
     * @return The computed display class.
     */
    private String computeDisplayClass(Idee idee, User connectedUser) {

        // S'il s'agit de l'idée de l'utilisateur => pas de display class
        if (connectedUser.equals(idee.getOwner())) {
            return "";
        }

        // Classe lors d'une réservation complète par une personne
        final BookingInformation bookingInfo = idee.getBookingInformation().orElse(BookingInformation.noBooking());
        if (bookingInfo.getBookingType() == BookingInformation.BookingType.SINGLE_PERSON) {
            return bookingInfo.getBookingOwner()
                              .filter(connectedUser::equals)
                              .map(u -> "booked_by_me_idea")
                              .orElse("booked_by_others_idea");
        }

        // Classe lors d'une réservation par un groupe
        if (bookingInfo.getBookingType() == BookingInformation.BookingType.GROUP) {
            return bookingInfo.getBookingGroup().filter(g -> g.contains(connectedUser))
                              .map(g -> "booked_by_me_idea")
                              .orElse("shared_booking_idea");
        }

        // Classe lors d'une sous-réservation par au moins une personne
        if (bookingInfo.getBookingType() == BookingInformation.BookingType.PARTIAL) {
            return SousReservationRepository.getSousReservation(idee.getId())
                                            .stream()
                                            .map(SousReservationEntity::getUser)
                                            .filter(connectedUser::equals)
                                            .map(u -> "booked_by_me_idea")
                                            .findFirst()
                                            .orElse("shared_booking_idea");
        }

        // No booking
        return "";
    }

    /**
     * @return The idea's owner.
     */
    public User getIdeaOwner() {
        return idee.getOwner();
    }

    /**
     * @return The idea.
     */
    public Idee getIdee() {
        return idee;
    }

    /**
     * @return The computed display class.
     */
    public String getDisplayClass() {
        return displayClass;
    }
}