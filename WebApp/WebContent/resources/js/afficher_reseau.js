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
            actionDone('Recherche terminée');
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
            searchNetwork(theName, networkOfUserId, $(this).attr('href').substring(5));
        });

        actionDone('Recherche terminée');
    }).fail(function() {
        actionError("Une erreur est survenue... Veuillez réessayer.<br/> Si cela se reproduit, envoyer un email à jordan.mosio@hotmail.fr avec la description de l'action.");
    });
}

$(document).ready(function() {
    $(".drop_relationship").click(dropRelationship);

    // auto complete
    personAutoComplete("#looking_for",
                       $("#userId").html(),
                       function(event, ui) {
                           $("#looking_for").val(ui.item.email);
                           $("#form_rechercher_dans_reseau").submit();
                           return false;
                       },
                       "#mobile_res_search_afficher_reseau");

    $("#form_rechercher_dans_reseau").find("button").click(function(e) {
        e.preventDefault();
        searchNetwork($("#looking_for").val(), $("#look_in_network").val());
    });

    // Initial search
    searchNetwork($("#looking_for").val(), $("#look_in_network").val());
});