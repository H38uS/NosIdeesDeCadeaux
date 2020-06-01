/*
    afficher reseau card-footer

    <c:choose>
        <c:when test="${relation.second.id != connected_user.id && relation.secondIsInMyNetwork}">
            Aller voir <a href="protected/voir_liste?id=${relation.second.id}">sa liste</a>.<br/>
            Aller voir <a href="protected/afficher_reseau?id=${relation.second.id}">ses amis</a>.<br/>
            <a href="protected/suggerer_relations?id=${relation.second.id}">Suggérer</a> des relations.<br/>
            Lui <a href="protected/ajouter_idee_ami?id=${relation.second.id}">ajouter</a> une idée.<br/>
            <a class="drop_relationship" href="protected/supprimer_relation?id=${relation.second.id}">Supprimer</a> cette personne.
        </c:when>
        <c:when test="${relation.second.id == connected_user.id}">
            Vous ne pouvez pas intéragir avec vous-même !
        </c:when>
        <c:otherwise>
            Vous n'êtes pas encore ami avec cette personne.<br/>
            <c:choose>
                <c:when test="${not empty relation.second.freeComment}">
                    ${relation.second.freeComment}
                </c:when>
                <c:otherwise>
                    <form method="POST" action="protected/demande_rejoindre_reseau">
                        <input type="hidden" name="user_id" value="${relation.second.id}" >
                        <input type="submit" name="submit" id="submit" value="Envoyer une demande" />
                    </form>
                </c:otherwise>
            </c:choose>
        </c:otherwise>
    </c:choose>



*/

function getRechercherPersonneCardFooterAsHTML(jsonUser) {
    var footerDiv = $("<div>");
    if (jsonUser.isInMyNetwork) {
        footerDiv.append(`
            <span class="verticalcenter_helper"></span>
            <img class="verticalcenter"
                 alt="${jsonUser.name} fait déjà parti de vos amis."
                 title="${jsonUser.name} fait déjà parti de vos amis."
                 src="resources/image/friend.png">
        `);
    } else if (typeof jsonUser.freeComment !== 'undefined') {
        footerDiv.append(`
            <span class="verticalcenter_helper"></span>
            <img class="verticalcenter"
                 alt="${jsonUser.freeComment}"
                 title="${jsonUser.freeComment}"
                 src="resources/image/demande_envoyee.jpg">
        `);
    } else {
        footerDiv.append(`
            <form method="POST" action="protected/demande_rejoindre_reseau">
                <input type="hidden" name="user_id" value="${jsonUser.id}" >
                <button class="envoyer_demande_reseau btn btn-primary" type="submit" name="submit">Envoyer une demande</button>
            </form>
        `);
    }
    return footerDiv.html();
}

function getUserDiv(connectedUser, jsonUser) {
    var userDiv = $(`
        <div class="card col-auto px-0 m-3 person_card">
            <a href="${avatarPicturePath}/large/${jsonUser.avatar}" class="thickbox img">
                <div class="row align-items-center mx-auto person_card_pic">
                    <img src="${avatarPicturePath}/small/${jsonUser.avatar}" />
                </div>
            </a>
            <div class="card-body">
                <h5 class="card-title">
                    <a href="protected/voir_liste?id=${jsonUser.id}">${jsonUser.name}</a>
                    <div><small>Né le ${jsonUser.readableBirthday}</small></div>
                </h5>
            </div>
            <div class="card-footer text-center">
                ${getRechercherPersonneCardFooterAsHTML(jsonUser)}
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

    $(".envoyer_demande_reseau").off("click");
    $(".envoyer_demande_reseau").click(sendRequest);

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