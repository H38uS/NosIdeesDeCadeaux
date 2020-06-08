function getListShortcutsDiv(identicCallBack, owners) {
    if (owners.length < 2) {
        return $("<div>");
    }
    var mesListesListUsers = $(`<div id="mes_listes_list_users">`);
    if (isMobileView()) {
        mesListesListUsers.addClass("mb-2");
    } else {
        mesListesListUsers.addClass("d-none d-xl-inline-block col-xl-3");
    }
    $.each(owners, function(i, ownerIdeas) {
        if (isMobileView()) {
            mesListesListUsers.append(`
                <a href="${identicCallBack}#list_${ownerIdeas.owner.id}">${ownerIdeas.owner.name}</a> |
            `);
        } else {
            mesListesListUsers.append(`
                <a href="${identicCallBack}#list_${ownerIdeas.owner.id}" class="col-12 p-2">
                    <div class="center">
                        <img src="${avatarPicturePath}/large/${ownerIdeas.owner.avatar}"
                             style='height:80px' alt=""/>
                    </div>
                    <div class="center">
                        ${ownerIdeas.owner.name}
                    </div>
                </a>
            `);
        }
    });
    return mesListesListUsers;
}

function displayUsersIdeasList(identicCallBack, callBack, page = 1) {

    doLoading("Récupération des idées en cours...");
    var dataServiceURL = callBack.replace("protected/", "protected/service/");

    console.log("dataServiceURL - " + dataServiceURL);
    console.log("identicCallBack - " + identicCallBack);

    $.get(  dataServiceURL,
            { id   : getURLParameter(identicCallBack, "id"),
              name : getURLParameter(identicCallBack, "name"),
              page : page
            }
    ).done(function (data) {

        var rawData = JSON.parse(data);
        if (rawData.status !== 'OK') {
            actionError(rawData.message);
            return;
        }

        var resultDiv = $("#resultPlaceholderForLists");
        resultDiv.empty().hide();
        var shortcutResDiv = $("#resultPlaceholderForShortcuts");
        shortcutResDiv.next().remove();

        var jsonData = rawData.message;
        var connectedUser = rawData.connectedUser;
        var owners = rawData.message.theContent;

        if (owners.length < 1) {
            resultDiv.append(`
                <p class="alert alert-danger">Aucune liste trouvée...</p>
                <p class="alert alert-info">
                    Vous pouvez entrer un nouveau nom ci-dessous, ou cliquer sur <a href="protected/afficher_reseau?id=${connectedUser.id}">ce
                    lien</a>
                    pour afficher tous vos amis.
                </p>
            `);
            resultDiv.fadeIn();
            closeModal();
            return;
        }

        // Shortcuts
        getListShortcutsDiv(identicCallBack, owners).insertAfter("#resultPlaceholderForShortcuts");

        // Ajout des pages si besoin
        resultDiv.append(getPagesDiv(jsonData.pages));

        $.each(owners, function(i, ownerIdeas) {
            var ownerTitle = getH2UserTitle(ownerIdeas.owner, connectedUser);
            resultDiv.append(ownerTitle);
            $.each(ownerIdeas.ideas, function(j, idea) {
                resultDiv.append(getIdeaDiv(connectedUser, idea));
            });
        });

        // pages en bas si besoin
        resultDiv.append(getPagesDiv(jsonData.pages));

        // actions des pages
        resultDiv.find("a.page-link").click(function(e) {
            e.preventDefault();
            displayUsersIdeasList(identicCallBack, callBack, $(this).attr('href').substring(5));
        });

        shortcutResDiv.fadeIn();
        resultDiv.fadeIn();
        closeModal();

    }).fail(function (data) {
        actionError(data.status + " - " + data.statusText);
    });
}