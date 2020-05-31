const minLength = 3;
const minTempsReflexion = 500;

var completed = true;

function doSearch(value, only_non_friend) {

    if (value.length < minLength || !completed)
        return;

    completed = false;
    if (only_non_friend) only_non_friend = "on";

    doLoading("Recherche en cours...");
    $("#res").empty();

    $.get('protected/service/rechercher_personne', { name : value, only_non_friend : only_non_friend }, function(data) {

        completed = true;
        $("#res").hide();

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
            actionDone('Recherche terminée');
            return;
        }

        // Préparation des résultats
        var usersDiv = $('<div class="row align-items-start mx-0 justify-content-center">');
        $("#res").append(usersDiv);

        // printing users
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
});