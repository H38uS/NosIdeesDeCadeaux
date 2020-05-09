function deleteIdea(e) {

	e.preventDefault();
	var id = getURLParameter($(this).attr("href"), 'ideeId');
	var idea = $(this).closest('.idea_square');
	
	servicePost('protected/service/delete_idea',
				{ ideeId : id },
				function(data) {
					idea.fadeOut('slow');
				},
				"Suppression de l'idée en cours...",
				"L'idée a bien été supprimée.");
}

function estAJourIdea(e) {
	
	e.preventDefault();
	var id = getURLParameter($(this).attr("href"), 'idee');
	var from = getURLParameter($(this).attr("href"), 'from');
	var idea = $(this).closest('.idea_square');
	
	servicePost('protected/service/est_a_jour',
			{ idee : id },
			function(data) {
				refreshIdea(idea, id, from);
			},
			"Création de la demande en cours...",
			"La demande a bien été créée.",
			"Impossible de créer la demande... Peut-être existe-t-elle déjà ?");
}

function reserverIdea(e) {

	e.preventDefault();
	var id = getURLParameter($(this).attr("href"), 'idee');
	var from = getURLParameter($(this).attr("href"), 'from');
	var idea = $(this).closest('.idea_square');
	
	servicePost('protected/service/reserver',
				{ idee : id },
				function(data) {
					refreshIdea(idea, id, from);
				},
				"Réservation de l'idée en cours...",
				"L'idée a bien été réservée.");
}

function dereserverIdea(e) {

	e.preventDefault();
	var id = getURLParameter($(this).attr("href"), 'idee');
	var from = getURLParameter($(this).attr("href"), 'from');
	var idea = $(this).closest('.idea_square');
	
	servicePost('protected/service/dereserver',
				{ idee : id },
				function(data) {
					refreshIdea(idea, id, from);
				},
				"Annulation de la réservation en cours...",
				"La réservation de l'idée a bien été annulée.");
}

function refreshIdea(idea, id, from) {
	$.get("protected/service/get_idea_of_friend",
		  {idee : id, from : from},
		  function (data) {
			idea.hide();
			idea.wrap("<span></span>");
			var div = idea.parent();
			div.html(data);
			var newIdea = div.children();
			newIdea.hide();
			newIdea.unwrap();
			setIdeaActionsToJs(newIdea);
			newIdea.fadeIn('slow');
		  },
		  "html")
	.fail(function () {
		// Osef pour le moment, on a pas réussi à changer l'idée
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
}

var postIdea = function(form) {
	var xhr = new XMLHttpRequest();
	xhr.open('POST', form.attr("action"));
	xhr.onload = function(e) {
	    var resp = JSON.parse(xhr.responseText);
        if (resp.status !== 'OK') {
            actionError(resp.message);
        } else {
            actionDone(resp.message);
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
	xhr.send(formData);
};

/* ********************************************************************* */
/* ***************** ==== Construction de DIV idee === ***************** */
/* ********************************************************************* */

function getMobileActionModalBodyAsHTML(connectedUser, jsonIdea) {
    var modalBodyDiv = $("<div>");
    if (jsonIdea.bookingInformation.type === "NONE") {
        modalBodyDiv.append(`
            <div class="row align-items-center">
                <div class="col-3 pr-0">
                    <a href="protected/reserver?idee=${jsonIdea.id}" class="img idea_reserver">
                        <img src="resources/image/reserver.png"
                             class="clickable"
                             title="Réserver l'idée"
                             width="${getPictureWidth()}px" />
                    </a>
                </div>
                <div class="col-9 pl-0 text-left">
                    Réserver l'idée
                </div>
            </div>
            <div class="row align-items-center">
                <div class="col-3 pr-0">
                    <a href="protected/sous_reserver?idee=${jsonIdea.id}" class="img">
                        <img src="resources/image/sous_partie.png"
                             class="clickable"
                             title="Réserver une sous-partie de l'idée"
                             width="${getPictureWidth()}px" />
                    </a>
                </div>
                <div class="col-9 pl-0 text-left">
                    Réserver une sous-partie de l'idée
                </div>
            </div>
            <div class="row align-items-center">
                <div class="col-3 pr-0">
                    <a href="protected/create_a_group?idee=${jsonIdea.id}" class="img">
                        <img src="resources/image/grouper.png"
                             class="clickable"
                             title="Créer un groupe"
                             width="${getPictureWidth()}px" />
                    </a>
                </div>
                <div class="col-9 pl-0 text-left">
                    Créer un groupe
                </div>
            </div>
        `);
    }
    if (typeof jsonIdea.surpriseBy === 'undefined') {
        modalBodyDiv.append(`
            <div class="row align-items-center">
                <div class="col-3 pr-0">
                    <a href="protected/est_a_jour?idee=${jsonIdea.id}" class="img idea_est_a_jour">
                        <img src="resources/image/a_jour.png"
                             class="clickable"
                             title="Demander si c'est à jour."
                             width="${getPictureWidth()}px" />
                    </a>
                </div>
                <div class="col-9 pl-0 text-left">
                    Demander si c'est à jour
                </div>
            </div>
            <div class="row align-items-center">
                <div class="col-3 pr-0">
                    <a href="protected/idee_questions?idee=${jsonIdea.id}" class="img">
                        <img src="resources/image/questions.png"
                             class="clickable"
                             title="Poser une question à ${jsonIdea.owner.name} / voir les existantes"
                             width="${getPictureWidth()}px" />
                    </a>
                </div>
                <div class="col-9 pl-0 text-left">
                    Poser une question à ${jsonIdea.owner.name} / voir les existantes
                </div>
            </div>
        `);
    }
    modalBodyDiv.append(`
        <div class="row align-items-center">
            <div class="col-3 pr-0">
                <a href="protected/idee_commentaires?idee=${jsonIdea.id}" class="img">
                    <img src="resources/image/commentaires.png"
                         title="Ajouter un commentaire / voir les existants"
                         width="${getPictureWidth()}px" />
                </a>
            </div>
            <div class="col-9 pl-0 text-left">
                Ajouter un commentaire / voir les existants
            </div>
        </div>
    `);
    return modalBodyDiv.html();
}

function getImageDivAsHTML(jsonIdea) {
    var imageDiv = $("<div>");
    if (typeof jsonIdea.image !== 'undefined') {
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
            content.text(`Idée surprise créée le ${jsonIdea.modificationDate} par ${jsonIdea.surpriseBy.name}.`);
        } else {
            content.html(`
                Idée surprise créée le ${jsonIdea.modificationDate} par vous - la
                // FIXME 00 --- il faut un service pour supprimer les idées...
                <a href="protected/supprimer_surprise?idee=${jsonIdea.id}">
                    supprimer
                </a>.
            `);
        }
        surpriseDiv.append(content);
    }
    return surpriseDiv.html();
}

function getReservationText(connectedUser, jsonIdea) {
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

function getPriorityIconAsHTML(jsonIdea) {
    var priorityIconDiv = $("<div>");
    if (typeof jsonIdea.priorite.image !== 'undefined') {
        priorityIconDiv.append(`
            <div class="col-auto pr-0 pl-1">${jsonIdea.priorite.image}</div>
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

function getAskIfUpToDateIconAsHTML(jsonIdea) {
    var askIfUpToDateIconDiv = $("<div>");
    if (jsonIdea.hasAskedIfUpToDate) {
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

function getCommentAndQuestionIconAsHTML(jsonIdea) {
    var commentAndQuestionIconDiv = $("<div>");
    if (jsonIdea.hasComment) {
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
    if (jsonIdea.hasQuestion) {
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
    if ($("#mobile_res_search").css('display') !== 'none') {
        mobileActionDiv.append(`
            <div class="col-auto ml-auto" data-toggle="modal" data-target="#actions-idea-${jsonIdea.id}">
                <button class="btn btn-primary" >Actions...</button>
            </div>
        `);
    }
    return mobileActionDiv.html();
}

function getActionTooltipForNonMobile(jsonIdea) {
    if ($("#mobile_res_search").css('display') !== 'none') {
        // no tooltip in Mobile view
        return "";
    }
    var actionTooltipSpan = $('<span class="outer_top_tooltiptext">');
    var content = $('<span class="top_tooltiptext">');
    if (jsonIdea.bookingInformation.type === "NONE") {
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
    if (typeof jsonIdea.surpriseBy === 'undefined') {
        content.append(`
            <a href="protected/est_a_jour?idee=${jsonIdea.id}" class="img idea_est_a_jour">
                <img src="resources/image/a_jour.png"
                     class="clickable"
                     title="Demander si c'est à jour."
                     width="${getPictureWidth()}px" />
            </a>
            <a href="protected/idee_questions?idee=${jsonIdea.id}" class="img">
                <img src="resources/image/questions.png"
                     class="clickable"
                     title="Poser une question à ${jsonIdea.owner.name} / voir les existantes"
                     width="${getPictureWidth()}px" />
            </a>
        `);
    }
    content.append(`
        <a href="protected/idee_commentaires?idee=${jsonIdea.id}" class="img">
            <img src="resources/image/commentaires.png"
                 title="Ajouter un commentaire / voir les existants"
                 width="${getPictureWidth()}px" />
        </a>
    `);
    actionTooltipSpan.append(content);
    return actionTooltipSpan;
}

function getIdeaDiv(connectedUser, jsonIdea) {

    var mainDiv = $(`<div id="idea-${jsonIdea.id}" class="idea_square top_tooltip ${jsonIdea.displayClass} col-lg-12 my-3 px-2"></div>`);
    var ideaContainer = $('<div class="p-2"></div>');

    // Mobile action div - only for Mobile view
    var ideaMobileModalContainer = "";
    if ($("#mobile_res_search").css('display') !== 'none') {
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
            ${getAskIfUpToDateIconAsHTML(jsonIdea)}
            ${getCommentAndQuestionIconAsHTML(jsonIdea)}
            ${getMobileActionButtonAsHTML(jsonIdea)}
        </div>
    `);

    // Action tooltip: must be on the Icon div.
    ideaIcons.append(getActionTooltipForNonMobile(jsonIdea));

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
    var status = $(`
        <div class="idea_square_modif_date" >
            Dernière modification le ${jsonIdea.modificationDate}.<br/>
            ${getSurpriseDivAsHTMl(connectedUser, jsonIdea)}
            ${getReservationText(connectedUser, jsonIdea)}
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
/* ********************************************************************* */

$(document).ready(function() {

    $("a.idea_remove").click(deleteIdea);
    $("a.idea_est_a_jour").click(estAJourIdea);
    $("a.idea_reserver").click(reserverIdea);
    $("a.idea_dereserver").click(dereserverIdea);

    var theForm = $(".post_idea").closest('form');
    if (typeof theForm.attr("action") !== 'undefined') {
        theForm.attr("action", theForm.attr("action").replace("protected/", "protected/service/"));
        $(".post_idea").click(function (e) {
            e.preventDefault();
            var form = $(this).closest('form');
            postIdea(form);
        });
    }
});