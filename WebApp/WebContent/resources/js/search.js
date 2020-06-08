const minLength = 3;
var completed = true;

function doSearch(value, only_non_friend, page = 1) {

    if (value.length < minLength || !completed)
        return;

    completed = false;
    if (only_non_friend) only_non_friend = "on";

    doLoading("Recherche en cours...");
    $("#res").empty();

    $.get('protected/service/rechercher_personne',
          {     name : value,
                only_non_friend : only_non_friend,
                page : page
          },
          function(data) {

        completed = true;

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
                    Aucun utilisateur trouvé correspondant aux critères de recherches.
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
            doSearch(value, only_non_friend, $(this).attr('href').substring(5));
        });

        closeModal();
    }).fail(function() {
        actionError("Une erreur est survenue... Veuillez réessayer.<br/> Si cela se reproduit, envoyer un email à jordan.mosio@hotmail.fr avec la description de l'action.");
    });
}

// FIXME : update the address bar when a search is done : https://github.com/browserstate/history.js

$(document).ready(function() {
    // input text
    $("#name").keyup(function() {
        var text = $(this).val();
        doSearch(text, $("#only_non_friend").is(':checked'));
    });
    // check box label
    $("#label_only_non_friend").click(function() {
        setTimeout(function() {
            // nécessaire pour passer la bonne valeur
            doSearch($("#name").val(), $("#only_non_friend").is(':checked'));
        }, 150);
    });
    // check box
    $("#only_non_friend").click(function() {
        setTimeout(function() {
            // nécessaire pour passer la bonne valeur
            doSearch($("#name").val(), $("#only_non_friend").is(':checked'));
        }, 150);
    });
    $("#rechercherPersonForm").find("button").click(function(e) {
        e.preventDefault();
        doSearch($("#name").val(), $("#only_non_friend").is(':checked'));
    });

    // First search if parameter are there
    doSearch($("#name").val(), $("#only_non_friend").is(':checked'));
});