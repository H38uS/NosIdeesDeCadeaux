var rechercheReseauCompleted = true;

function searchNetwork(theName, networkOfUserId, page = 1) {

    if (!rechercheReseauCompleted)
        return;

    if (page == null) {
        page = 1;
    }

    rechercheReseauCompleted = false;
    doLoading("Recherche en cours...");
    $("#res").empty();

    $.get('protected/service/rechercher_reseau',
          {     looking_for : theName,
                id : networkOfUserId,
                page : page
          },
          function(data) {

        rechercheReseauCompleted = true;

        // Récupération des données
        var rawData = JSON.parse(data);
        if (rawData.status !== 'OK') {
            actionError(rawData.message);
            return;
        }

        // Gettings the response
        var jsonData = rawData.message;
        var jsonUsers = jsonData.theContent;

        // Si pas de résultat
        if (jsonUsers.length == 0) {
            $("#res").append(`
                <div class="alert alert-info">
                    Aucun ami trouvé correspondant aux critères de recherches.
                </div>
            `);
            $("#res").fadeIn('slow');
            closeModal();
            return;
        }

        // Ajout des pages si besoin
        $("#res").append(getPagesDiv(jsonData.pages));

        // Préparation des résultats
        var usersDiv = $('<div class="row align-items-start mx-0 justify-content-center">');
        $("#res").append(usersDiv);
        $("#res").append(getPagesDiv(jsonData.pages));

        // printing users
        $.each(jsonUsers, function(i, jsonUser) {
            usersDiv.append(getUserDiv(rawData.connectedUser, jsonUser));
        });

        // actions des pages
        $("a.page-link").click(function(e) {
            e.preventDefault();
            var thePage = $(this).attr('href').substring(5);
            ChangeUrl("protected/afficher_reseau?id=" + networkOfUserId + "&looking_for=" + theName + "&page=" + thePage);
            searchNetwork(theName, networkOfUserId, thePage);
        });

        ChangeUrl("protected/afficher_reseau?id=" + networkOfUserId + "&looking_for=" + theName + "&page=" + page);

        closeModal();
    }).fail(function() {
        actionError("Une erreur est survenue... Veuillez réessayer.<br/> Si cela se reproduit, envoyer un email à jordan.mosio@hotmail.fr avec la description de l'action.");
    });
}

function reloadSuggestionIfAny() {

    $.get("protected/service/suggestion_amis")
    .done(function (data) {

        var rawData = JSON.parse(data);
        if (rawData.status !== 'OK') {
            actionError(rawData.message);
            return;
        }

        var resDiv = $("#new_friend_suggestions");
        resDiv.empty().hide();
        var jsonData = rawData.message;
        if (jsonData.length === 0) {
            return;
        }

        var container = $("<div></div>");
        container.append(`<h3 class="pb-1">Suggestions de nouveaux amis</h3>`);
        var infoDiv = $(`<div class="alert alert-info"></div>`);
        var previous;
        var suggestionTable;
        $.each(jsonData, function(i, suggestion) {
            console.log(previous)
            if (typeof previous === 'undefined' || previous !== suggestion.suggestedBy.id) {
                // new user detected
                infoDiv.append(`<h3>De la part de ${suggestion.suggestedBy.name}</h3>`);
                suggestionTable = $(`<table>
                                             <thead>
                                                 <tr>
                                                     <th>Nom</th>
                                                     <th>Email</th>
                                                     <th>Envoyer une demande</th>
                                                     <th>Ne rien faire</th>
                                                 </tr>
                                             </thead>
                                         </table>`);
                infoDiv.append(suggestionTable);
                submitBtn = $(`
                    <button class="btn btn-primary" type="submit" name="submit" id="submit-suggestion-${suggestion.suggestedBy.id}">Sauvegarder</button>
                `);
                submitBtn.click(function () {
                    var selected = [];
                    $('input[name^="selected_"]').each(function() {
                        selected.push([$(this).attr("id"), $(this).is(":checked")]);
                    });
                    var rejected = [];
                    $('input[name^="rejected_"]').each(function() {
                        rejected.push([$(this).attr("id"), $(this).is(":checked")]);
                    });
                    servicePost('protected/service/suggestion_amis',
                                {
                                    selected : selected,
                                    rejected : rejected
                                },
                                function(data) {
                                    reloadSuggestionIfAny();
                                },
                                'Mise à jour des suggestions et envois des demandes...',
                                'Les demandes ont bien été envoyées / les suggestions bien supprimés !');
                });
                infoDiv.append(submitBtn);
            }
            // adding all the suggestions
            suggestionTable.append(`
                <tr>
                    <td>
                        <label for="selected_${suggestion.suggestion.id}" >${suggestion.suggestion.name}</label>
                    </td>
                    <td>
                        <label for="selected_${suggestion.suggestion.id}" >${suggestion.suggestion.email}</label>
                    </td>
                    <td class="center">
                        <input type="checkbox" name="selected_${suggestion.suggestion.id}" id="selected_${suggestion.suggestion.id}" />
                        <span class="checkbox"></span>
                    </td>
                    <td class="center">
                        <input type="checkbox" name="rejected_${suggestion.suggestion.id}" id="rejected_${suggestion.suggestion.id}" />
                        <span class="checkbox"></span>
                    </td>
                </tr>
            `);
            previous = suggestion.suggestedBy.id;
        });

        container.append(infoDiv);
        resDiv.append(container).fadeIn();
    }).fail(function (data) {
        actionError(data.status + " - " + data.statusText);
    });
}

function submit_requests() {
    var selected = [];
    $('input[name^="acc_choix_"]').each(function() {
        selected.push([$(this).attr("id"), $(this).is(":checked")]);
    });
    var rejected = [];
    $('input[name^="ref_choix_"]').each(function() {
        rejected.push([$(this).attr("id"), $(this).is(":checked")]);
    });
    servicePost('protected/service/resoudre_demande_ami',
                {
                    selected : selected,
                    rejected : rejected
                },
                function(data) {
                    reloadFriendShipIfAny();
                },
                'Mise à jour des demandes et envoie des choix...',
                'Décisions bien prises en compte !');
}

function reloadFriendShipIfAny() {

    $.get("protected/service/resoudre_demande_ami")
    .done(function (data) {

        var rawData = JSON.parse(data);
        if (rawData.status !== 'OK') {
            actionError(rawData.message);
            return;
        }

        var resDiv = $("#res_demandes");
        resDiv.empty().hide();
        var jsonData = rawData.message;
        if (jsonData.length === 0) {
            return;
        }

        var container = $(`<div class="container border border-info bg-light rounded p-3 mb-3"></div>`);
        container.append("<h3>Demandes reçues</h3>");
        var requestsDiv = $(`<div></div>`);

        $.each(jsonData, function(i, request) {
            requestsDiv.append(`
                <div class="row align-items-center">
                    <div class="col-6 word-break-all">
                        <span>${request.sent_by.name} (${request.sent_by.email})</span>
                    </div>
                    <div class="col-3">
                        <input type="radio" id="acc_choix_${request.sent_by.id}" name="acc_choix_${request.sent_by.id}" value="Accepter">
                        <label for="acc_choix_${request.sent_by.id}">Accepter</label>
                    </div>
                    <div class="col-3">
                        <input type="radio" id="ref_choix_${request.sent_by.id}" name="ref_choix_${request.sent_by.id}" value="Refuser">
                        <label for="ref_choix_${request.sent_by.id}">Refuser</label>
                    </div>
                </div>
            `);
            container.append(requestsDiv);
        });

        container.append(`
            <div class="center">
                <button class="btn btn-primary" type="submit" id="submit_requests" name="submit_requests">Sauvegarder</button>
            </div>
        `);
        resDiv.append(container).fadeIn();
        $("#submit_requests").click(submit_requests);

    }).fail(function (data) {
       actionError(data.status + " - " + data.statusText);
    });
}

// Loading new friendship requests
reloadFriendShipIfAny();

// Loading suggestions
reloadSuggestionIfAny();

// Initialization
$(".drop_relationship").click(dropRelationship);
// auto search
$("#looking_for").keyup(function () {
    var searchItem = $("#looking_for").val();
    if (searchItem.length > 2) {
        $("#form_rechercher_dans_reseau").find("button").click();
    }
});
$("#form_rechercher_dans_reseau").find("button").click(function(e) {
    e.preventDefault();
    searchNetwork($("#looking_for").val(), $("#look_in_network").val());
});
// Initial search
searchNetwork($("#looking_for").val(), $("#look_in_network").val(), getURLParameter($(location).attr('href'), "page"));
