function actualDelete(id, idea) {
    servicePost('protected/service/delete_idea',
                { ideeId : id },
                function(data) {
                    idea.fadeOut('slow');
                },
                "Suppression de l'idée en cours...",
                "L'idée a bien été supprimée.");
}

function deleteIdea(e) {

    e.preventDefault();
    var id = getURLParameter($(this).attr("href"), 'ideeId');
    var idea = $(this).closest('.idea_square');

    var countDown = 5;
    var content = $(`
        <div class="container">
            <div class="row">
                Marde marde marde... Je ne voulais pas supprimer cette idée !
            </div>
            <div class="row">
                Pas de soucis... Elle ne sera supprimée que dans&nbsp;<span id="countDown" class="font-weight-bold">${countDown}</span>s...
            </div>
            <div class="row">
                <a href="" id="annulerSuppression">Annuler</a>&nbsp;ou la&nbsp;<a href="" id="confSuppression">supprimer maintenant</a>.
            </div>
            <div class="row">
                Attention à ne pas changer de page, cela annulerait la suppression de cette idée.
            </div>
        </div>`);
    var countDownSpan = content.find("#countDown");
    doLoading(content);

    // Loop to let the user stop the delete
    var timerForCancelDelete = setInterval(function() {
        countDown--;
        if (countDown == 0) {
            actualDelete(id, idea);
        } else {
            countDownSpan.text(countDown);
        }
    }, 1000);

    // Cancel the delete
    content.find("#annulerSuppression").click((e) => {
        e.preventDefault();
        clearInterval(timerForCancelDelete);
        closeModal();
    });

    // Force delete NOW
    content.find("#confSuppression").click((e) => {
        e.preventDefault();
        clearInterval(timerForCancelDelete);
        actualDelete(id, idea);
    });
}

function restore(ideaSquare, ideaId, withBooking) {
    servicePost('protected/service/idee/restore',
                {
                    idee : ideaId,
                    restoreBooking : withBooking
                },
                function(data) {
                    refreshIdea(ideaSquare, ideaId);
                },
                "Recréation de l'idée en cours...",
                "L'idée a bien été recréé.");
}

function restoreEmpty(e) {
    e.preventDefault();
    var id = getURLParameter($(this).attr("href"), 'idee');
    var idea = $(this).closest('.idea_square');
    restore(idea, id, false);
}

function restoreWithBooking(e) {
    e.preventDefault();
    var id = getURLParameter($(this).attr("href"), 'idee');
    var idea = $(this).closest('.idea_square');
    restore(idea, id, true);
}

function estAJourIdea(e) {

    e.preventDefault();
    var id = getURLParameter($(this).attr("href"), 'idee');
    var idea = $(this).closest('.idea_square');

    servicePost('protected/service/est_a_jour',
        { idee : id },
        function(data) {
            refreshIdea(idea, id);
        },
        "Création de la demande en cours...",
        "La demande a bien été créée.",
        "Impossible de créer la demande... Peut-être existe-t-elle déjà ?");
}

function reserverIdea(e) {

    e.preventDefault();
    var id = getURLParameter($(this).attr("href"), 'idee');
    var idea = $(this).closest('.idea_square');

    servicePost('protected/service/reserver',
                { idee : id },
                function(data) {
                    refreshIdea(idea, id);
                },
                "Réservation de l'idée en cours...",
                "L'idée a bien été réservée.");
}

function dereserverIdea(e) {

    e.preventDefault();
    var id = getURLParameter($(this).attr("href"), 'idee');
    var idea = $(this).closest('.idea_square');

    servicePost('protected/service/dereserver',
                { idee : id },
                function(data) {
                    refreshIdea(idea, id);
                },
                "Annulation de la réservation en cours...",
                "La réservation de l'idée a bien été annulée.");
}

function refreshIdea(idea, id) {
    $.get("protected/service/get_idea",
          {idee : id}
    ).done(function (data) {

        var rawData = JSON.parse(data);
        if (rawData.status !== 'OK') {
          actionError(rawData.message);
          return;
        }

        idea.wrap("<span></span>");
        var div = idea.parent();
        idea.remove();

        var newIdea = getIdeaDiv(rawData.connectedUser, rawData.message);
        newIdea.hide();
        div.append(newIdea);
        newIdea.unwrap();

        newIdea.fadeIn('slow');

    }).fail(function (data) {
        actionError(data.status + " - " + data.statusText);
    });
}

function setIdeaActionsToJs(my_idea) {
    my_idea.find("a.idea_remove").click(deleteIdea);
    my_idea.find("a.idea_est_a_jour").click(estAJourIdea);
    my_idea.find("a.idea_reserver").click(reserverIdea);
    my_idea.find("a.idea_dereserver").click(dereserverIdea);
    my_idea.find(".mobile_actions").click(function () {
        my_idea.find(".modal").modal('show');
    });
    my_idea.find("a.jeLaVeuxEncore").click(function(e) {
        e.preventDefault();
        var id = getURLParameter($(this).attr("href"), 'idee');
        servicePost('protected/service/je_la_veux_encore',
                    { idee : id },
                    function(data) { },
                    "Annulation des réservations en cours...",
                    "Toutes les réservations (s'il y en avait) ont bien été supprimées sur cette idée.");
    });
    my_idea.find("a.supprimerSurprise").click(function (e) {
        e.preventDefault();
        var id = getURLParameter($(this).attr("href"), 'idee');
        servicePost('protected/service/supprimer_surprise',
                    { idee : id },
                    function(data) {
                        my_idea.fadeOut('slow');
                    },
                    "Suppression de la surprise en cours...",
                    "La surprise a bien été supprimée.");
    });
    my_idea.find(".modal").on('show.bs.modal', function (e) {
        lastModalOpened = $(this);
    });
    // Initialisation des liens thickbox
    my_idea.find("a.thickbox, area.thickbox,input.thickbox ").click(function () {
        var t = this.title || this.name || null;
        var a = (this.href || this.alt).replace(/'/g, "%27"); // correction IE et chrome
        var g = this.rel || false;
        tb_show(t,a,g);
        this.blur();
        return false;
    });
    // Restoration de l'idée
    my_idea.find("a.restore_with_booking").click(restoreWithBooking);
    my_idea.find("a.restore_empty").click(restoreEmpty);
}

var postIdea = function(form) {
    var xhr = new XMLHttpRequest();
    xhr.open('POST', form.attr("action"));
    xhr.onload = function(e) {
        var resp = JSON.parse(xhr.responseText);
        if (resp.status !== 'OK') {
            actionError(resp.message);
        } else {
            if ($("#ideaResPlaceholder").length) {
                var resIdea = $("#ideaResPlaceholder").children(":first");
            } else {
                var resDiv = $("#idea_creation_result");
                var resIdea = $("<div>");
                resDiv.append(resIdea);
            }
            refreshIdea(resIdea, resp.message)
            actionDone("L'idée a bien été créée / mise à jour sur le serveur.");
        }
    };
    xhr.onerror = function() {
        actionError(this.responseText)
    };
    xhr.upload.onprogress = function(event) {
        var percent = (event.loaded / event.total) * 100;
        doLoading("Envoie en cours (" + percent + "%)...")
    }

    var formData = new FormData();
    formData.append('fileName', selectedPictureName);
    formData.append('text', form.find("#text").val());
    formData.append('type', form.find("#type").val());
    formData.append('priority', form.find("#priority").val());
    formData.append('old_picture', form.find("#old_picture").val());
    formData.append('myfile', selectedPicture);
    formData.append('est_surprise', form.find("#est_surprise").is(":checked") ? "on" : "off");
    xhr.send(formData);
};

/* ********************************************************************* */
/* ***************** ==== Construction de DIV idee === ***************** */
/* ********************************************************************* */

function isTheOwnerConnected(jsonIdea) {
    return typeof jsonIdea.bookingInformation === 'undefined';
}

function getMobileActionModalBodyAsHTML(connectedUser, jsonIdea) {
    var modalBodyDiv = $("<div>");
    if (!isTheOwnerConnected(jsonIdea)) {
        if (jsonIdea.bookingInformation.type === "NONE") {
            modalBodyDiv.append(`
                <div class="row align-items-center">
                    <div class="col-4 text-center pr-0">
                        <a href="protected/reserver?idee=${jsonIdea.id}" class="img idea_reserver">
                            <img src="resources/image/reserver.png"
                                 class="clickable"
                                 title="Réserver l'idée"
                                 width="${getPictureWidth()}px" />
                        </a>
                    </div>
                    <div class="col-8 pl-0 text-left">
                        Réserver l'idée
                    </div>
                </div>
                <div class="row align-items-center">
                    <div class="col-4 text-center pr-0">
                        <a href="protected/sous_reserver?idee=${jsonIdea.id}" class="img">
                            <img src="resources/image/sous_partie.png"
                                 class="clickable"
                                 title="Réserver une sous-partie de l'idée"
                                 width="${getPictureWidth()}px" />
                        </a>
                    </div>
                    <div class="col-8 pl-0 text-left">
                        Réserver une sous-partie de l'idée
                    </div>
                </div>
                <div class="row align-items-center">
                    <div class="col-4 text-center pr-0">
                        <a href="protected/create_a_group?idee=${jsonIdea.id}" class="img">
                            <img src="resources/image/grouper.png"
                                 class="clickable"
                                 title="Créer un groupe"
                                 width="${getPictureWidth()}px" />
                        </a>
                    </div>
                    <div class="col-8 pl-0 text-left">
                        Créer un groupe
                    </div>
                </div>
            `);
        }
    } else {
        // Connected user is the owner
        modalBodyDiv.append(`
            <div class="row align-items-center">
                <div class="col-4 text-center">
                    <a href="protected/modifier_idee?id=${jsonIdea.id}" class="img">
                        <img src="resources/image/modifier.png"
                             title="Modifier cette idée"
                             width="${getPictureWidth()}px"/>
                    </a>
                </div>
                <div class="col-8 pl-0 text-left">
                    Modifier cette idée
                </div>
            </div>
            <div class="row align-items-center">
                <div class="col-4 text-center">
                    <a href="protected/remove_an_idea?ideeId=${jsonIdea.id}" class="img idea_remove">
                        <img src="resources/image/supprimer.png"
                             title="Supprimer cette idée"
                             width="${getPictureWidth()}px"/>
                    </a>
                </div>
                <div class="col-8 pl-0 text-left">
                    Supprimer cette idée
                </div>
            </div>
        `);
    }
    if (typeof jsonIdea.surpriseBy === 'undefined') {
        if (!isTheOwnerConnected(jsonIdea)) {
            modalBodyDiv.append(`
                <div class="row align-items-center">
                    <div class="col-4 text-center pr-0">
                        <a href="protected/est_a_jour?idee=${jsonIdea.id}" class="img idea_est_a_jour">
                            <img src="resources/image/a_jour.png"
                                 class="clickable"
                                 title="Demander si c'est à jour."
                                 width="${getPictureWidth()}px" />
                        </a>
                    </div>
                    <div class="col-8 pl-0 text-left">
                        Demander si c'est à jour
                    </div>
                </div>
            `);
        }
        modalBodyDiv.append(`
            <div class="row align-items-center">
                <div class="col-4 text-center pr-0">
                    <a href="protected/idee_questions?idee=${jsonIdea.id}" class="img">
                        <img src="resources/image/questions.png"
                             class="clickable"
                             title="Poser une question à ${jsonIdea.owner.name} / voir les existantes"
                             width="${getPictureWidth()}px" />
                    </a>
                </div>
                <div class="col-8 pl-0 text-left">
                    Poser une question à ${jsonIdea.owner.name} / voir les existantes
                </div>
            </div>
        `);
    }
    if (isTheOwnerConnected(jsonIdea)) {
        modalBodyDiv.append(`
            <div class="row align-items-center">
                <div class="col-4 text-center">
                    <a href="?idee=${jsonIdea.id}" class="img jeLaVeuxEncore">
                        <img src="resources/image/encore.png"
                             title="J'ai déjà reçu cette idée, mais je la veux à nouveau ou je veux la suite."
                             height="${getPictureWidth()}px"/>
                    </a>
                </div>
                <div class="col-8 pl-0 text-left">
                    Annuler toutes les réservations
                </div>
            </div>
        `);
    } else {
        modalBodyDiv.append(`
            <div class="row align-items-center">
                <div class="col-4 text-center pr-0">
                    <a href="protected/idee_commentaires?idee=${jsonIdea.id}" class="img">
                        <img src="resources/image/commentaires.png"
                             title="Ajouter un commentaire / voir les existants"
                             width="${getPictureWidth()}px" />
                    </a>
                </div>
                <div class="col-8 pl-0 text-left">
                    Ajouter un commentaire / voir les existants
                </div>
            </div>
        `);
    }
    return modalBodyDiv.html();
}

function getImageDivAsHTML(jsonIdea) {
    var imageDiv = $("<div>");
    if (typeof jsonIdea.image !== 'undefined' && jsonIdea.image != "") {
        imageDiv.append(`
            <div class="col-auto pl-2 pr-2">
                <a href="${picturePath}/large/${jsonIdea.image}" class="thickbox img" >
                    <img src="${picturePath}/small/${jsonIdea.image}" width="150" />
                </a>
            </div>
        `);
    }
    return imageDiv.html();
}

function getSurpriseDivAsHTMl(connectedUser, jsonIdea) {
    var surpriseDiv = $("<div>");
    if (typeof jsonIdea.surpriseBy !== 'undefined') {
        var content = $("<div>");
        if (connectedUser.id !== jsonIdea.surpriseBy.id) {
            content.html(`Idée surprise créée le ${jsonIdea.modificationDate} par ${jsonIdea.surpriseBy.name}.`);
        } else {
            content.html(`
                Idée surprise créée le ${jsonIdea.modificationDate} par vous - la
                <a class="supprimerSurprise" href="?idee=${jsonIdea.id}">
                    supprimer
                </a>.
            `);
        }
        surpriseDiv.append(content);
    }
    return surpriseDiv.html();
}

function getReservationText(connectedUser, jsonIdea) {
    if (isTheOwnerConnected(jsonIdea)) {
        return "";
    }
    if (jsonIdea.bookingInformation.type === "SINGLE_PERSON") {
        if (connectedUser.id !== jsonIdea.bookingInformation.bookingOwner.id) {
            return `
                Réservée par ${jsonIdea.bookingInformation.bookingOwner.name}
                le ${jsonIdea.bookingInformation.bookingReadableDate}
            `;
        }
        // Réservé par soi
        return `
            Réservée par vous le ${jsonIdea.bookingInformation.bookingReadableDate} -
            <a href="?idee=${jsonIdea.id}" class="idea_dereserver">Annuler</a> !
        `;
    } else if (jsonIdea.bookingInformation.type === "GROUP") {
        return `
            Réservée par un groupe (créé le ${jsonIdea.bookingInformation.bookingReadableDate}).
            <a href="protected/detail_du_groupe?groupid=${jsonIdea.bookingInformation.group.id}">Voir le détail du groupe</a>.
        `;
    } else if (jsonIdea.bookingInformation.type === "PARTIAL") {
        return `
            Une sous partie de l'idée est actuellement réservée depuis le ${jsonIdea.bookingInformation.bookingReadableDate}.
            <a href="protected/detail_sous_reservation?idee=${jsonIdea.id}">Voir le détail.</a>
        `;
    }
    // Dernier cas: pas réservée
    return "Non réservée.";
}

function getRestoreActions(connectedUser, jsonIdea) {
    if (jsonIdea.hasBeenDeleted) {
        return `La recréer :
            <a href="?idee=${jsonIdea.id}" class="restore_with_booking">avec réservations</a> (erreur de suppression) ou
            <a href="?idee=${jsonIdea.id}" class="restore_empty">sans</a> (recyclage).`;
    }
    return "";
}

function getPriorityIconAsHTML(jsonIdea) {
    var priorityIconDiv = $("<div>");
    if (typeof jsonIdea.priority.image !== 'undefined') {
        priorityIconDiv.append(`
            <div class="col-auto pr-0 pl-1">${jsonIdea.priority.image}</div>
        `);
    }
    return priorityIconDiv.html();
}

function getCategoryIconDivAsHTML(jsonIdea) {
    var categoryDiv = $("<div>");
    if (typeof jsonIdea.categorie !== 'undefined') {
        categoryDiv.append(`
            <div class="col-auto px-0">
                <img src="resources/image/type/${jsonIdea.categorie.image}"
                     title="${jsonIdea.categorie.title}"
                     alt="${jsonIdea.categorie.alt}"
                     width="${getPictureWidth()}px" />
            </div>
        `);
    }
    return categoryDiv.html();
}

function getSurpriseIconDivAsHTMl(jsonIdea) {
    var surpriseDiv = $("<div>");
    if (typeof jsonIdea.surpriseBy !== 'undefined') {
        surpriseDiv.append(`
            <div class="col-auto px-0">
                <img src="resources/image/surprise.png" title="Idée surprise" width="${getPictureWidth()}px" />
            </div>
        `);
    }
    return surpriseDiv.html();
}

function getBookingIconAsHTML(connectedUser, jsonIdea) {
    if (isTheOwnerConnected(jsonIdea)) {
        return "";
    }
    var bookingIconDiv = $("<div>");
    if (jsonIdea.bookingInformation.type === "SINGLE_PERSON") {
        if (connectedUser.id !== jsonIdea.bookingInformation.bookingOwner.id) {
            bookingIconDiv.append(`
                <div class="col-auto px-0">
                    <img src="resources/image/reserve-autre.png"
                         title="Une réservation d'une autre personne plus rapide..."
                         alt="Idée réservée par une autre personne"
                         width="${getPictureWidth()}px" />
                </div>
            `);
        } else {
            // Réservé par soi
            bookingIconDiv.append(`
                <div class="col-auto px-0">
                    <a href="?idee=${jsonIdea.id}" class="img idea_dereserver">
                        <img src="resources/image/reserve-moi.png"
                             title="Une de vos généreuses réservations - Cliquer pour annuler"
                             alt="Idée réservée par vous"
                             width="${getPictureWidth()}px" />
                    </a>
                </div>
            `);
        }
    } else if (jsonIdea.bookingInformation.type === "GROUP") {
        bookingIconDiv.append(`
            <div class="col-auto px-0">
                <a href="protected/detail_du_groupe?groupid=${jsonIdea.bookingInformation.group.id}" class="img">
                    <img src="resources/image/reserve-groupe.png"
                         title="Une réservation de groupe !"
                         alt="Idée réservée par un groupe"
                         width="${getPictureWidth()}px" />
                </a>
            </div>
        `);
    } else if (jsonIdea.bookingInformation.type === "PARTIAL") {
        bookingIconDiv.append(`
            <div class="col-auto px-0">
                <a href="protected/detail_sous_reservation?idee=${jsonIdea.id}" class="img">
                    <img src="resources/image/non-reserve.png"
                         title="Un sous-ensemble de cette idée est réservé. Voyez si vous pouvez compléter !"
                         alt="Sous partie de l'idée réservée"
                         width="${getPictureWidth()}px" />
                </a>
            </div>
        `);
    } else {
        // Dernier cas: pas réservée
        bookingIconDiv.append(`
            <div class="col-auto px-0">
                <img src="resources/image/non-reserve.png"
                     title="Cette idée est libre... Faite plaisir en l'offrant !"
                     alt="Idée non réservée"
                     width="${getPictureWidth()}px" />
            </div>
        `);
    }
    return bookingIconDiv.html();
}

function getAskIfUpToDateIconAsHTML(jsonDecoratedIdea) {
    var jsonIdea = jsonDecoratedIdea.idee;
    if (isTheOwnerConnected(jsonIdea)) {
        return "";
    }
    var askIfUpToDateIconDiv = $("<div>");
    if (jsonDecoratedIdea.hasAskedIfUpToDate) {
        askIfUpToDateIconDiv.append(`
            <div class="col-auto px-0">
                <img src="resources/image/a_jour.png"
                     title="Vous avez envoyé une demande pour savoir si c'est à jour"
                     alt="Demande est-ce à jour envoyée"
                     width="${getPictureWidth()}px" />
            </div>
        `);
    }
    return askIfUpToDateIconDiv.html();
}

function getCommentAndQuestionIconAsHTML(jsonDecoratedIdea) {
    var jsonIdea = jsonDecoratedIdea.idee;
    var commentAndQuestionIconDiv = $("<div>");
    if (jsonDecoratedIdea.hasComment && !jsonIdea.hasBeenDeleted) {
        commentAndQuestionIconDiv.append(`
            <div class="col-auto px-0">
                <a href="protected/idee_commentaires?idee=${jsonIdea.id}" class="img">
                    <img src="resources/image/commentaires.png"
                         title="Il existe des commentaires sur cette idée"
                         width="${getPictureWidth()}px" />
                </a>
            </div>
        `);
    }
    if (jsonDecoratedIdea.hasQuestion && !jsonIdea.hasBeenDeleted) {
        commentAndQuestionIconDiv.append(`
            <div class="col-auto px-0">
                <a href="protected/idee_questions?idee=${jsonIdea.id}" class="img">
                    <img src="resources/image/questions.png"
                         title="Il existe des questions/réponses sur cette idée"
                         width="${getPictureWidth()}px" />
                </a>
            </div>
        `);
    }
    return commentAndQuestionIconDiv.html();
}

function getMobileActionButtonAsHTML(jsonIdea) {
    var mobileActionDiv = $("<div>");
    if (isMobileView() && !jsonIdea.hasBeenDeleted) {
        mobileActionDiv.append(`
            <div class="col-auto ml-auto" data-toggle="modal" data-target="#actions-idea-${jsonIdea.id}">
                <button class="btn btn-primary" >Actions...</button>
            </div>
        `);
    }
    return mobileActionDiv.html();
}

function getActionTooltipForNonMobile(jsonIdea) {
    if (isMobileView()) {
        // no tooltip in Mobile view
        return "";
    }
    var actionTooltipSpan = $('<span class="outer_top_tooltiptext">');
    var content = $('<span class="top_tooltiptext">');
    if (!isTheOwnerConnected(jsonIdea)) {
        if (jsonIdea.bookingInformation.type === "NONE") {
            // *** Boutons pour réserver
            content.append(`
                <a href="protected/reserver?idee=${jsonIdea.id}" class="img idea_reserver">
                    <img src="resources/image/reserver.png"
                         class="clickable"
                         title="Réserver l'idée"
                         width="${getPictureWidth()}px" />
                </a>
                <a href="protected/sous_reserver?idee=${jsonIdea.id}" class="img">
                    <img src="resources/image/sous_partie.png"
                         class="clickable"
                         title="Réserver une sous-partie de l'idée"
                         width="${getPictureWidth()}px" />
                </a>
                <a href="protected/create_a_group?idee=${jsonIdea.id}" class="img">
                    <img src="resources/image/grouper.png"
                         class="clickable"
                         title="Créer un groupe"
                         width="${getPictureWidth()}px" />
                </a>
            `);
        }
    } else {
        // *** Boutons pour modifier supprimer l'idée
        content.append(`
            <a href="protected/modifier_idee?id=${jsonIdea.id}"
               class="img">
                <img src="resources/image/modifier.png"
                     title="Modifier cette idée"
                     width="${getPictureWidth()}px"/>
            </a>
            <a href="protected/remove_an_idea?ideeId=${jsonIdea.id}"
               class="img idea_remove">
                <img src="resources/image/supprimer.png"
                     title="Supprimer cette idée"
                     width="${getPictureWidth()}px"/>
            </a>
        `);
    }
    if (typeof jsonIdea.surpriseBy === 'undefined') {
        if (!isTheOwnerConnected(jsonIdea)) {
            // *** Boutons pour demander si à jour
            content.append(`
                <a href="protected/est_a_jour?idee=${jsonIdea.id}" class="img idea_est_a_jour">
                    <img src="resources/image/a_jour.png"
                         class="clickable"
                         title="Demander si c'est à jour."
                         width="${getPictureWidth()}px" />
                </a>
            `);
        }
        // *** Boutons pour poser des questions / y répondre
        content.append(`
            <a href="protected/idee_questions?idee=${jsonIdea.id}" class="img">
                <img src="resources/image/questions.png"
                     class="clickable"
                     title="Poser une question à ${jsonIdea.owner.name} / voir les existantes"
                     width="${getPictureWidth()}px" />
            </a>
        `);
        if (isTheOwnerConnected(jsonIdea)) {
            // *** Boutons pour annuler les réservations
            content.append(`
                <a href="?idee=${jsonIdea.id}"
                   class="jeLaVeuxEncore img">
                    <img src="resources/image/encore.png"
                         title="J'ai déjà reçu cette idée, mais je la veux à nouveau ou je veux la suite."
                         height="${getPictureWidth()}px"/>
                </a>
            `);
        }
    }
    if (!isTheOwnerConnected(jsonIdea)) {
        content.append(`
            <a href="protected/idee_commentaires?idee=${jsonIdea.id}" class="img">
                <img src="resources/image/commentaires.png"
                     title="Ajouter un commentaire / voir les existants"
                     width="${getPictureWidth()}px" />
            </a>
        `);
    }
    actionTooltipSpan.append(content);
    return actionTooltipSpan;
}

function getIdeaDiv(connectedUser, jsonDecoratedIdea) {

    var jsonIdea = jsonDecoratedIdea.idee;
    var mainDiv = $(`<div id="idea-${jsonIdea.id}" class="idea_square top_tooltip ${jsonDecoratedIdea.displayClass} col-lg-12 my-3 px-2"></div>`);
    var ideaContainer = $('<div class="p-2"></div>');

    // Mobile action div - only for Mobile view
    var ideaMobileModalContainer = "";
    if (isMobileView()) {
        ideaMobileModalContainer = $(`
            <div class="modal fade" id="actions-idea-${jsonIdea.id}" tabindex="-1" role="dialog" aria-hidden="true">
        `);
        var ideaMobileModalContent = $(`
            <div class="modal-dialog modal-dialog-centered" role="document">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title" id="exampleModalLongTitle">Choisissez une action</h5>
                        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                            <span aria-hidden="true">&times;</span>
                        </button>
                    </div>
                    <div class="modal-body">
                        ${getMobileActionModalBodyAsHTML(connectedUser, jsonIdea)}
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-primary" data-dismiss="modal">Fermer</button>
                    </div>
                </div>
            </div>
        `);
        ideaMobileModalContainer.append(ideaMobileModalContent);
    }

    // Icons - priority, category and so on
    var ideaIcons = $(`
        <div class="row justify-content-start align-items-center pb-2">
            ${getPriorityIconAsHTML(jsonIdea)}
            ${getCategoryIconDivAsHTML(jsonIdea)}
            ${getSurpriseIconDivAsHTMl(jsonIdea)}
            ${getBookingIconAsHTML(connectedUser, jsonIdea)}
            ${getAskIfUpToDateIconAsHTML(jsonDecoratedIdea)}
            ${getCommentAndQuestionIconAsHTML(jsonDecoratedIdea)}
            ${getMobileActionButtonAsHTML(jsonIdea)}
        </div>
    `);

    // Action tooltip: must be on the Icon div.
    if (!jsonIdea.hasBeenDeleted) {
        ideaIcons.append(getActionTooltipForNonMobile(jsonIdea));
    }

    // The idea : text + picture
    var theActualIdea = $(`
        <div class="row align-items-center">
            ${getImageDivAsHTML(jsonIdea)}
            <div class="left col word-break-all px-2">
                ${jsonIdea.htmlText}
            </div>
        </div>
    `);

    // Status text, modified date, reservation details etc.
    var modificationStatusText;
    if (jsonIdea.hasBeenDeleted) {
        modificationStatusText = `Idée supprimée le ${jsonIdea.modificationDate}`;
    } else {
        modificationStatusText = `Dernière modification le ${jsonIdea.modificationDate}`;
    }
    var status = $(`
        <div class="idea_square_modif_date" >
            ${modificationStatusText}.<br/>
            ${getSurpriseDivAsHTMl(connectedUser, jsonIdea)}
            ${getReservationText(connectedUser, jsonIdea)}
            ${getRestoreActions(connectedUser, jsonIdea)}
        </div>
    `);

    ideaContainer.append(ideaMobileModalContainer);
    ideaContainer.append(ideaIcons);
    ideaContainer.append(theActualIdea);
    ideaContainer.append(status);

    mainDiv.append(ideaContainer);
    setIdeaActionsToJs(mainDiv);
    return mainDiv;
}

/* ********************************************************************* */
/* *************** ==== Construction de H2 user list === *************** */
/* ********************************************************************* */

function getH2UserTitleTooltip(ownerIdeas, connectedUser) {
    var content = $('<div>');
    if (ownerIdeas.owner.id !== connectedUser.id) {
        content.append(`
            Aller voir <a href="protected/voir_liste?id=${ownerIdeas.owner.id}">sa liste</a>.<br/>
            Aller voir <a href="protected/afficher_reseau?id=${ownerIdeas.owner.id}">ses amis</a>.<br/>
            <a href="protected/suggerer_relations.jsp?id=${ownerIdeas.owner.id}">Suggérer</a> des relations.<br/>
            Lui <a href="protected/ajouter_idee_ami?id=${ownerIdeas.owner.id}">ajouter</a> une idée.<br/>
            <a class="drop_relationship" href="protected/supprimer_relation?id=${ownerIdeas.owner.id}">Supprimer</a> cette relation.
        `);
    } else {
        if (ownerIdeas.isDeletedIdeas) {
            content.append(`
                Aller voir <a href="protected/voir_liste?id=${ownerIdeas.owner.id}">ma liste</a>.<br/>
                Aller voir <a href="protected/afficher_reseau?id=${ownerIdeas.owner.id}">mes amis</a>.<br/>
                Aller voir <a href="protected/mes_reservations.jsp">mes réservations</a>.<br/>
                Je veux plus de <a href="protected/ajouter_idee">cadeaux</a>.<br/>
            `);
        } else {
            content.append(`
                Aller voir <a href="protected/voir_liste?id=${ownerIdeas.owner.id}">ma liste</a>.<br/>
                Aller voir <a href="protected/afficher_reseau?id=${ownerIdeas.owner.id}">mes amis</a>.<br/>
                Aller voir <a href="protected/mes_reservations.jsp">mes réservations</a>.<br/>
                Je veux plus de <a href="protected/ajouter_idee">cadeaux</a>.<br/>
                Aller voir mes <a href="protected/idee/historique">anciennes idées</a>.<br/>
            `);
        }
    }

    if (isMobileView()) {
        container = $(`
            <div class="modal fade" id="actions-user-${ownerIdeas.owner.id}" tabindex="-1" role="dialog" aria-hidden="true">
        `);
        var ideaMobileModalContent = $(`
            <div class="modal-dialog modal-dialog-centered" role="document">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title" id="exampleModalLongTitle">Choisissez une action</h5>
                        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                            <span aria-hidden="true">&times;</span>
                        </button>
                    </div>
                    <div class="modal-body normal_size">
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-primary" data-dismiss="modal">Fermer</button>
                    </div>
                </div>
            </div>
        `);
        ideaMobileModalContent.find(".modal-body").append(content);
        container.append(ideaMobileModalContent);
    } else {
        container = $('<div class="outer_top_tooltiptext">')
        content.addClass('top_tooltiptext pl-4');
        container.append(content);
    }

    return $('<div>').append(container).html();
}

function getH2UserTitle(ownerIdeas, connectedUser) {
    var ideaOwner = ownerIdeas.owner;
    var actionButtonMobile = '';
    if (isMobileView()) {
        actionButtonMobile = $(`
            <div>
                <div class="col-auto ml-auto my-auto" data-toggle="modal" data-target="#actions-user-${ownerIdeas.owner.id}">
                    <button class="btn btn-primary" >...</button>
                </div>
            </div>
        `).html();
    }
    if (ideaOwner.id !== connectedUser.id) {
        res = $(`
            <h2 id="list_${ideaOwner.id}" class="breadcrumb mt-4 h2_list col-12 top_tooltip">
                <div class="row align-items-center justify-content-center">
                    <div class="ml-2 ml-lg-none my-1">
                        <img src="protected/files/uploaded_pictures/avatars/small/${ideaOwner.avatar}"
                             alt="" style="height:50px;"/>
                    </div>
                    <div class="mx-2">
                        <span class="d-none d-lg-inline-block">${ideaOwner.name}</span>
                        <span class="d-inline-block d-lg-none">${ideaOwner.name}</span>
                    </div>
                </div>
                ${actionButtonMobile}
                ${getH2UserTitleTooltip(ownerIdeas, connectedUser)}
            </h2>
        `);
        res.find(".drop_relationship").click(dropRelationship);
        return res;
    } else {
        // Le user connecté
        if (ownerIdeas.isDeletedIdeas) {
            return $(`
                <h2 id="list_${ideaOwner.id}" class="breadcrumb mt-4 h2_list col-12 top_tooltip">
                    <div class="row align-items-center justify-content-center">
                        <div class="ml-2 ml-lg-none my-1">
                            <img src="protected/files/uploaded_pictures/avatars/small/${ideaOwner.avatar}"
                                 alt="" style="height:50px;"/>
                        </div>
                        <div class="mx-2">
                            <span class="d-none d-lg-inline-block">Mes idées de cadeaux supprimées</span>
                            <span class="d-inline-block d-lg-none">Idées supprimées</span>
                        </div>
                    </div>
                    ${actionButtonMobile}
                    ${getH2UserTitleTooltip(ownerIdeas, connectedUser)}
                </h2>
            `);
        } else {
            return $(`
                <h2 id="list_${ideaOwner.id}" class="breadcrumb mt-4 h2_list col-12 top_tooltip">
                    <div class="row align-items-center justify-content-center">
                        <div class="ml-2 ml-lg-none my-1">
                            <img src="protected/files/uploaded_pictures/avatars/small/${ideaOwner.avatar}"
                                 alt="" style="height:50px;"/>
                        </div>
                        <div class="mx-2">
                            <span class="d-none d-lg-inline-block">Mes idées de cadeaux</span>
                            <span class="d-inline-block d-lg-none">Mes idées</span>
                        </div>
                    </div>
                    ${actionButtonMobile}
                    ${getH2UserTitleTooltip(ownerIdeas, connectedUser)}
                </h2>
            `);
        }
    }
}

/* ********************************************************************* */
/* ********************************************************************* */

$(document).ready(function() {

    var theForm = $(".post_idea").closest('form');
    if (typeof theForm.attr("action") !== 'undefined') {
        theForm.attr("action", theForm.attr("action")
                                      .replace("protected/", "protected/service/")
                                      .replace("ajouter_idee_ami", "ajouter_idee")); // service mutualisé
        $(".post_idea").click(function (e) {
            e.preventDefault();
            var form = $(this).closest('form');
            postIdea(form);
        });
    }
});