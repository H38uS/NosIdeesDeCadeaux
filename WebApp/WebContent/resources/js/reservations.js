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

        $.each(jsonData.theContent, function(i, ownerIdeas) {
            var ownerTitle = $(`
                <h2 id="list_${ownerIdeas.owner.id}" class="breadcrumb mt-4 h2_list">
                    <div class="row align-items-center">
                        <div class="col-auto mx-auto my-1">
                            <img src="protected/files/uploaded_pictures/avatars/small/${ownerIdeas.owner.avatar}"
                                 alt="" style="height:50px;"/>
                        </div>
                        <div class="mx-1">
                            <span class="d-none d-lg-inline-block">${ownerIdeas.owner.name}</span>
                            <span class="d-inline-block d-lg-none">${ownerIdeas.owner.name}</span>
                        </div>
                    </div>
                </h2>
            `);
            ideaContainer.append(ownerTitle);
            $.each(ownerIdeas.ideas, function(j, idea) {
                ideaContainer.append(getIdeaDiv(connectedUser, idea));
            });
        });

        // Ajout des pages si besoin
        ideaContainer.append(getPagesDiv(jsonData.pages));

        // actions des pages
        $("a.page-link").click(function(e) {
            e.preventDefault();
            loadReservations($(this).attr('href').substring(5));
        });

        $("#reservation_res_area").fadeIn();
        closeModal();

    }).fail(function (data) {
        actionError(data.status + " - " + data.statusText);
    });
}

loadReservations();
