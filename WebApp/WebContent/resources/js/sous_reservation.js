function sousReserver() {
    var ideaId = getURLParameter($(location).attr('href'), "idee");
    servicePost('protected/service/sous_reserver',
                {
                    comment : $("#comment").val(),
                    idee    : ideaId
                },
                function(data) {
                    refreshIdea($(".idea_square"), ideaId);
                    loadDetails();
                },
                "Sous réservation de l'idée en cours...",
                "Sous réservation effectuée.");
}

function annulerSousReservation(e) {
    e.preventDefault();
    var id = $(this).attr('id').substring(8);
    servicePost('protected/service/annuler_sous_reservation',
                { id : id },
                function(data) {
                    loadDetails();
                },
                "Annulation de la sous réservation en cours...",
                "Annulation faite !");
}

function loadDetails() {
    var ideaId = getURLParameter($(location).attr('href'), "idee");
    $.get("protected/service/sous_reserver",
          { idee : ideaId }
    ).done(function (data) {
        var rawData = JSON.parse(data);
        if (rawData.status !== 'OK') {
            actionError(rawData.message);
            return;
        }
        var content = $('<span>');
        if (rawData.message.length === 0) {
            content.append(`<div class="alert alert-info">Aucune sous-réservation trouvée. Ajoutez la vôtre !</div>`);
        } else {
            var connectedUser = rawData.connectedUser;
            $.each(rawData.message, function(i, subBooking) {
                if (connectedUser.id === subBooking.user.id) {
                    content.append(`
                        <div class="container">
                            <div class="alert alert-primary px-0 border border-primary">
                                <div class="px-3">${subBooking.htmlText}</div>
                                <div class="text-right px-3 font-italic">
                                    <small>
                                        Réservé par <strong>vous</strong> le ${subBooking.lastEditedOn} - l'<a id="booking-${subBooking.id}" class="delete_sub_booking" href="?idee=${ideaId}">annuler</a>
                                    </small>
                                </div>
                        </div>
                    `);
                } else {
                    content.append(`
                        <div class="container">
                            <div class="alert alert-warning px-0 border border-warning">
                                <div class="px-3">${subBooking.htmlText}</div>
                                <div class="text-right px-3 font-italic">
                                    <small>
                                        Réservé par <strong>${subBooking.user.name}</strong> le ${subBooking.lastEditedOn}
                                    </small>
                                </div>
                        </div>
                    `);
                }
            });
        }

        var res_sous_reservation = $("#res_sous_reservation");
        res_sous_reservation.empty().hide();
        res_sous_reservation.append(content);
        res_sous_reservation.find("p").addClass("mb-0");
        res_sous_reservation.find(".delete_sub_booking").click(annulerSousReservation);
        res_sous_reservation.fadeIn('slow');
    }).fail(function (data) {
        actionError(data.status + " - " + data.statusText);
    });
}

function loadH2Title() {
    var ideaId = getURLParameter($(location).attr('href'), "idee");
    $.get("protected/service/get_idea",
          {idee : ideaId}
    ).done(function (data) {
        var rawData = JSON.parse(data);
        $("#titleDiv").html(getH2UserTitle(rawData.message.idee.owner, false, rawData.connectedUser));
    }).fail(function (data) {
        actionError(data.status + " - " + data.statusText);
    });
}

loadH2Title();
loadDetails();
$("#submit").click(sousReserver);



