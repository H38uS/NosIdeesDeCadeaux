function removeCard(card) {
    var myRow = card.closest(".row");
    card.fadeOut('slow', function () {
        card.remove();
        if (myRow.children(".card").length === 0) {
            var emptyZone = $("<div></div>");
            emptyZone.addClass("alert alert-info");
            emptyZone.text("Vous n'avez aucune notification pour le moment.");
            $("#mes_notifs_place").html(emptyZone);
        }
    });
    var nb = $("#my_notif_count").text();
    nb--;
    $("#my_notif_count").text(nb);
}

function deleteNotification(e) {
    e.preventDefault();
    var id = getURLParameter($(this).attr("href"), 'notif_id');

    var card = $(this).closest('.card');
    servicePost('protected/service/notification_delete',
                { notif_id : id },
                function(data) {
                    removeCard(card);
                },
                'Suppression de la notification en cours...',
                'La notification a bien été supprimée.');
}

function removeIdea(e) {
    e.preventDefault();
    var id = getURLParameter($(this).attr("href"), 'idee');
    var card = $(this).closest('.card');
    servicePost('protected/service/delete_idea',
                { ideeId : id },
                function(data) {
                    removeCard(card);
                },
                "Suppression de l'idée en cours...",
                "L'idée a bien été supprimée.");
}

$(document).ready(function() {
    $("a.notif_delete").click(deleteNotification);
    $(".removeIdea").click(removeIdea);
});