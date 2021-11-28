function dropRelationship(e) {

    e.preventDefault();

    if (!confirm("Etes-vous sûr de supprimer cette relation ?")) {
        return;
    }

    var userId = getURLParameter($(this).attr("href"), 'id');
    var card = $(this).closest(".card");
    servicePost('protected/service/supprimer_relation',
                { id : userId },
                function(data) {
                    card.fadeOut('slow');
                },
                'Suppression de la relation en cours...',
                'La relation a bien été supprimée.');
}

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
        $.each(jsonData, function(i, suggestion) {
            infoDiv.append(`<h3>De la part de ${suggestion.suggestedBy.name}</h3>`);
            var suggestionTable = $(`<table>
                                         <thead>
                                             <tr>
                                                 <th>Nom</th>
                                                 <th>Email</th>
                                                 <th>Envoyer une demande</th>
                                                 <th>Ne rien faire</th>
                                             </tr>
                                         </thead>
                                     </table>`);
            $.each(suggestion.suggestions, function(j, suggestedUser) {
                suggestionTable.append(`
                    <tr>
                        <td>
                            <label for="selected_${suggestedUser.id}" >${suggestedUser.name}</label>
                        </td>
                        <td>
                            <label for="selected_${suggestedUser.id}" >${suggestedUser.email}</label>
                        </td>
                        <td class="center">
                            <input type="checkbox" name="selected_${suggestedUser.id}" id="selected_${suggestedUser.id}" />
                            <span class="checkbox"></span>
                        </td>
                        <td class="center">
                            <input type="checkbox" name="rejected_${suggestedUser.id}" id="rejected_${suggestedUser.id}" />
                            <span class="checkbox"></span>
                        </td>
                    </tr>
                `);
            })
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
        });

        container.append(infoDiv);
        resDiv.append(container).fadeIn();
    }).fail(function (data) {
        actionError(data.status + " - " + data.statusText);
    });
}

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
