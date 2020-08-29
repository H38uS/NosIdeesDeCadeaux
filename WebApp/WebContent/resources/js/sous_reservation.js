function sousReserver() {
    var ideaId = $("#sousReserverForm").find("input[name=idee]").val();
    servicePost('protected/service/sous_reserver',
                {
                    comment : $("#comment").val(),
                    idee    : ideaId
                },
                function(data) {
                    refreshIdea($(".idea_square"), ideaId);
                },
                "Sous réservation de l'idée en cours...",
                "Sous réservation effectuée.");
}

$(document).ready(function() {
    $("#sousReserverForm").find("button").click(function(e) {
        e.preventDefault();
        sousReserver();
    });
});

