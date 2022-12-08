function loadReservations(page = 1) {
    doLoading("Récupération des réservations en cours...");
    $.get(  "protected/service/mes_reservations",
            {
                page : page
            }
    ).done(function (data) {

        var content = $("<div></div>");
        $("#reservation_res_area").hide().html(content);

        var rawData = JSON.parse(data);
        if (rawData.status !== 'OK') {
            actionError(rawData.message);
            return;
        }

        var jsonData = rawData.message;
        if (jsonData.length === 0) {
            content.addClass("alert alert-warning");
            content.text("Vous n'avez aucune réservation pour le moment !");
            $("#reservation_res_area").hide().html(content).fadeIn();
            closeModal();
            return;
        }

        var connectedUser = rawData.connectedUser;
        var ideaContainer = $("<div>").addClass("container");
        content.append(ideaContainer);

        // Ajout des pages si besoin
        ideaContainer.append(getPagesDiv(jsonData.pages));

        if (jsonData.theContent.length == 0) {
            ideaContainer.append(`
                <div class="alert alert-warning">Vous n'avez actuellement aucune réservation.</div>
            `);
        }

        $.each(jsonData.theContent, function(i, ownerIdeas) {
            ideaContainer.append(getH2UserTitle(ownerIdeas.owner, ownerIdeas.isDeletedIdeas, connectedUser));
            $.each(ownerIdeas.ideas, function(j, idea) {
                ideaContainer.append(getIdeaDiv(connectedUser, idea));
            });
        });

        // Ajout des pages si besoin
        ideaContainer.append(getPagesDiv(jsonData.pages));

        // actions des pages
        $("a.page-link").click(function(e) {
            e.preventDefault();
            var thePage = $(this).attr('href').substring(5);
            ChangeUrl("protected/mes_reservations?page=" + thePage);
            loadReservations(thePage);
        });

        $("#reservation_res_area").fadeIn();
        closeModal();

    }).fail(function (data) {
        actionError(data.status + " - " + data.statusText);
    });
}

loadReservations(getURLParameter($(location).attr('href'), "page"));
