const minLength = 3;
const minTempsReflexion = 500;

var completed = true;

function doSearch(value, only_non_friend) {

    if (value.length < minLength || !completed)
        return;

    completed = false;
    if (only_non_friend) only_non_friend = "on";

    doLoading("Recherche en cours...");
    $("#res").html();

    $.get('protected/service/rechercher_personne', { name : value, only_non_friend : only_non_friend }, function(data) {

        completed = true;
        $("#res").hide();
        $("#res").empty();

        // Récupération des données
        var rawData = JSON.parse(data);
        if (rawData.status !== 'OK') {
            actionError(rawData.message);
            return;
        }

        // Préparation des résultats
        var usersDiv = $('<div class="row align-items-start mx-0 justify-content-center">');
        $("#res").append(usersDiv);

        var jsonData = rawData.message;
        var jsonUsers = jsonData.theContent
        $.each(jsonUsers, function(i, jsonUser) {
            usersDiv.append(getUserDiv(jsonData.connectedUser, jsonUser));
        });

        $("#res").fadeIn('slow');

        actionDone('Recherche terminée');
    }).fail(function() {
        actionError("Une erreur est survenue... Veuillez réessayer.<br/> Si cela se reproduit, envoyer un email à jordan.mosio@hotmail.fr avec la description de l'action.");
    });
}

$(document).ready(function() {
    $("#name").keyup(function() {
        var text = $(this).val();
        doSearch(text, $("#only_non_friend").is(':checked'));
    });
    $("#label_only_non_friend").click(function() {
        setTimeout(function() {
            doSearch($("#name").val(), $("#only_non_friend").is(':checked'));
        }, 150);
    });
    $("#span_only_non_friend").click(function() {
        doSearch($("#name").val(), $("#only_non_friend").is(':checked'));
    });
});