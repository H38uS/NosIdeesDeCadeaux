package com.mosioj.ideescadeaux.webapp.entities;

import com.google.gson.annotations.Expose;
import com.mosioj.ideescadeaux.core.model.entities.*;
import com.mosioj.ideescadeaux.core.model.repositories.CommentsRepository;
import com.mosioj.ideescadeaux.core.model.repositories.IdeesRepository;
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

        boolean tempComment = false;
        try {
            tempComment = CommentsRepository.getNbComments(idee.getId()) > 0;
        } catch (SQLException e) {
            logger.error(e);
        }
        boolean tempQuestion = false;
        try {
            tempQuestion = QuestionsRepository.getNbQuestions(idee.getId()) > 0;
        } catch (SQLException e) {
            logger.error(e);
        }

        this.idee = idee;
        hasComment = tempComment;
        hasQuestion = tempQuestion;
        hasAskedIfUpToDate = IdeesRepository.hasUserAskedIfUpToDate(idee.getId(), connectedUser.id);

        // calcul de la display class
        final BookingInformation bookingInfo = idee.getBookingInformation();
        if (bookingInfo.getBookingType() == BookingInformation.BookingType.SINGLE_PERSON) {

            displayClass = bookingInfo.getBookingOwner()
                                      .filter(connectedUser::equals)
                                      .map(u -> "booked_by_me_idea")
                                      .orElse("booked_by_others_idea");

        } else if (bookingInfo.getBookingType() == BookingInformation.BookingType.GROUP) {

            displayClass = bookingInfo.getBookingGroup().filter(g -> g.contains(connectedUser))
                                      .map(g -> "booked_by_me_idea")
                                      .orElse("shared_booking_idea");

        } else if (bookingInfo.getBookingType() == BookingInformation.BookingType.PARTIAL) {

            displayClass = SousReservationRepository.getSousReservation(idee.getId())
                                                    .stream()
                                                    .map(SousReservationEntity::getUser)
                                                    .filter(connectedUser::equals)
                                                    .map(u -> "booked_by_me_idea")
                                                    .findFirst()
                                                    .orElse("shared_booking_idea");

        } else {
            // No booking
            displayClass = "";
        }

        if (device.isMobile()) {
            Priorite priorite = idee.getPriorite();
            if (priorite != null && priorite.getImage() != null) {
                priorite.image = priorite.getImage().replaceAll("width=\"[0-9]+px\"",
                                                                "width=\"" +
                                                                ParametersUtils.MOBILE_PICTURE_WIDTH +
                                                                "px\"");
            }
        }
    }

    /**
     * @return The idea's owner.
     */
    public User getIdeaOwner() {
        return idee.getOwner();
    }

    /**
     *
     * @return The idea.
     */
    public Idee getIdee() {
        return idee;
    }
}