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

function getRechercherPersonneCardFooterAsHTML(connectedUser, jsonDecoratedUser) {
    var footerDiv = $("<div>");
    if (connectedUser.id === jsonDecoratedUser.user.id) {
        footerDiv.append(`
            Vous ne pouvez pas intéragir avec vous-même !
        `);
    } else if (jsonDecoratedUser.isInMyNetwork) {
        footerDiv.append(`
            Aller voir <a href="protected/voir_liste?id=${jsonDecoratedUser.user.id}">sa liste</a>.<br/>
            Aller voir <a href="protected/afficher_reseau?id=${jsonDecoratedUser.user.id}">ses amis</a>.<br/>
            <a href="protected/suggerer_relations.jsp?id=${jsonDecoratedUser.user.id}">Suggérer</a> des relations.<br/>
            Lui <a href="protected/ajouter_idee_ami?id=${jsonDecoratedUser.user.id}">ajouter</a> une idée.<br/>
            <a class="drop_relationship" href="protected/supprimer_relation?id=${jsonDecoratedUser.user.id}">Supprimer</a> cette relation.
        `);
    } else if (jsonDecoratedUser.hasSentARequest) {
        footerDiv.append(`
            <span class="verticalcenter_helper"></span>
            <img class="verticalcenter"
                 alt="Vous avez déjà envoyé une demande à ${jsonDecoratedUser.user.name}"
                 title="Vous avez déjà envoyé une demande à ${jsonDecoratedUser.user.name}"
                 src="resources/image/demande_envoyee.jpg">
        `);
    } else {
        footerDiv.append(`
            <form method="POST" action="protected/demande_rejoindre_reseau">
                <input type="hidden" name="user_id" value="${jsonDecoratedUser.user.id}" >
                <button class="envoyer_demande_reseau btn btn-primary" type="submit" name="submit">Envoyer une demande</button>
            </form>
        `);
    }
    return footerDiv.html();
}

function getUserDiv(connectedUser, jsonDecoratedUser) {
    var userDiv = $(`
        <div class="card col-auto px-0 m-3 person_card">
            <a href="${avatarPicturePath}/large/${jsonDecoratedUser.user.avatar}" class="thickbox img">
                <div class="row align-items-center mx-auto person_card_pic">
                    <img src="${avatarPicturePath}/small/${jsonDecoratedUser.user.avatar}" />
                </div>
            </a>
            <div class="card-body">
                <h5 class="card-title">
                    <a href="protected/voir_liste?id=${jsonDecoratedUser.user.id}">${jsonDecoratedUser.user.name}</a>
                    <div class="font-italic"><small>${jsonDecoratedUser.user.email}</small></div>
                    <div><small>Né le ${jsonDecoratedUser.readableBirthday}</small></div>
                </h5>
            </div>
            <div class="card-footer text-center">
                ${getRechercherPersonneCardFooterAsHTML(connectedUser, jsonDecoratedUser)}
            </div>
        </div>
    `);

    // Gestion des actions + rendu
    // Initialisation des liens thickbox
    userDiv.find("a.thickbox, area.thickbox,input.thickbox ").click(function () {
        var t = this.title || this.name || null;
        var a = (this.href || this.alt).replace(/'/g, "%27"); // correction IE et chrome
        var g = this.rel || false;
        tb_show(t,a,g);
        this.blur();
        return false;
    });

    userDiv.find(".envoyer_demande_reseau").off("click");
    userDiv.find(".envoyer_demande_reseau").click(sendRequest);
    userDiv.find(".drop_relationship").click(dropRelationship);

    return userDiv;
}

function sendRequest(e) {

    e.preventDefault();

    var form = $(this).closest("form");
    var userId = form.find("input[name=user_id]").val();

    servicePost('protected/service/demande_rejoindre_reseau',
                { user_id : userId },
                function(data) {},
                "Envoie d'une demande en cours...",
                "Envoie de la demande avec succès.");
}