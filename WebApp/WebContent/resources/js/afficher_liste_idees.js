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

function displayUsersIdeasList(identicCallBack, callBack, page) {

    if (page == null) {
        page = getURLParameter($(location).attr('href'), "page");
        if (page == null) {
            page = 1;
        }
    }

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
            if (isMobileView()) {
                resultDiv.append(`
                    <p class="alert alert-danger">Aucune liste trouvée...</p>
                    <p class="alert alert-info">
                        Affichez <a href="protected/afficher_reseau?id=${connectedUser.id}">mes amis</a> !
                    </p>
                `);
            } else {
                resultDiv.append(`
                    <p class="alert alert-danger">Aucune liste trouvée...</p>
                    <p class="alert alert-info">
                        Vous pouvez entrer un nouveau nom ci-dessous, ou cliquez sur <a href="protected/afficher_reseau?id=${connectedUser.id}">ce
                        lien</a>
                        pour afficher tous vos amis.
                    </p>
                    <div class="alert alert-warning">
                        <div class="row align-items-center">
                            <div class="col-auto">Recherchez une liste particulière</div>
                            <form id="afficherliste_bottommeslistes" class="form-inline" method="GET"
                                  action="protected/afficher_listes">
                                <input type="text" class="form-control" name="name"
                                       id="bottom_mes_listes_search" placeholder="Entrez un nom ou un email"/>
                                <button class="btn btn-primary mx-2" type="submit">Rechercher !</button>
                            </form>
                        </div>
                    </div>
                `);
                personAutoComplete("#bottom_mes_listes_search",
                                   -1,
                                   function(event, ui) {
                                       $("#bottom_mes_listes_search").val(ui.item.email);
                                       $("#afficherliste_bottommeslistes").submit();
                                       return false;
                                   },
                                   "#mobile_res_search",
                                   "right bottom",
                                   "right top");
            }
            resultDiv.fadeIn();
            closeModal();
            return;
        }

        // Shortcuts
        getListShortcutsDiv(identicCallBack, owners).insertAfter("#resultPlaceholderForShortcuts");

        // Ajout de la première possibilité de chercher d'autres personnes, si besoin
        // Forcément au moins une personne ici
        var isDeletedIdeas = owners[0].isDeletedIdeas;
        if (!isMobileView() && !isDeletedIdeas) {
            resultDiv.append(`
                <div class="alert alert-warning">
                    <div class="row align-items-center pb-2">
                        <div class="col-auto">
                            Vous ne trouvez pas votre bonheur ?<br/> Recherchez une liste particulière :
                        </div>
                        <form id="afficherliste_topmeslistes" class="form-inline" method="GET"
                              action="protected/afficher_listes">
                            <input type="text" class="form-control" name="name" id="top_mes_listes_search"
                                   placeholder="Entrez un nom ou un email"/>
                            <button class="btn btn-primary mx-2" type="submit">Rechercher !</button>
                        </form>
                    </div>
                </div>
            `);
            personAutoComplete("#top_mes_listes_search",
                               -1,
                               function(event, ui) {
                                   $("#top_mes_listes_search").val(ui.item.email);
                                   $("#afficherliste_topmeslistes").submit();
                                   return false;
                               },
                               "#mobile_res_search"); // osef: uniquement en non mobile
        }

        // Ajout des pages si besoin
        resultDiv.append(getPagesDiv(jsonData.pages));

        $.each(owners, function(i, ownerIdeas) {
            var ownerTitle = getH2UserTitle(ownerIdeas, connectedUser);
            resultDiv.append(ownerTitle);
            if (ownerIdeas.owner.id === connectedUser.id && !isDeletedIdeas) {
                resultDiv.append(`
                    <div class="alert alert-primary">
                        Votre historique d'idées se trouve <a href="protected/idee/historique">ici</a>.
                    </div>
                `);
            }
            $.each(ownerIdeas.ideas, function(j, idea) {
                resultDiv.append(getIdeaDiv(connectedUser, idea));
            });
        });

        // pages en bas si besoin
        resultDiv.append(getPagesDiv(jsonData.pages));

        // Ajout de la deuxième possibilité de chercher d'autres personnes, si besoin
        // Forcément au moins une personne ici
        if (!isMobileView() && !isDeletedIdeas) {
            if (owners.length === 1) {
                resultDiv.append(`
                    <div class="alert alert-warning">
                        <div class="row align-items-center">
                            <div class="col-auto">
                                Consultez une autre liste :
                            </div>
                            <form id="afficherliste_bottommeslistes" class="form-inline" method="GET"
                                  action="protected/afficher_listes">
                                <input type="text" class="form-control" name="name"
                                       id="bottom_mes_listes_search" placeholder="Entrez un nom ou un email"/>
                                <button class="btn btn-primary mx-2" type="submit">Rechercher !</button>
                            </form>
                        </div>
                    </div>
                `);
            } else {
                resultDiv.append(`
                    <div class="alert alert-warning">
                        <div class="row align-items-center">
                            <div class="col-auto">
                                Vous ne trouvez pas votre bonheur ?<br/> Recherchez une liste particulière :
                            </div>
                            <form id="afficherliste_bottommeslistes" class="form-inline" method="GET"
                                  action="protected/afficher_listes">
                                <input type="text" class="form-control" name="name"
                                       id="bottom_mes_listes_search" placeholder="Entrez un nom ou un email"/>
                                <button class="btn btn-primary mx-2" type="submit">Rechercher !</button>
                            </form>
                        </div>
                    </div>
                `);
            }
            personAutoComplete("#bottom_mes_listes_search",
                               -1,
                               function(event, ui) {
                                   $("#bottom_mes_listes_search").val(ui.item.email);
                                   $("#afficherliste_bottommeslistes").submit();
                                   return false;
                               },
                               "#mobile_res_search",
                               "right bottom",
                               "right top");
        }

        // actions des pages
        resultDiv.find("a.page-link").click(function(e) {
            e.preventDefault();
            var thePage = $(this).attr('href').substring(5);
            var newUrl = identicCallBack.includes('?') ? identicCallBack + "&page=" : identicCallBack + "?page=";
            ChangeUrl(newUrl + thePage);
            displayUsersIdeasList(identicCallBack, callBack, thePage);
        });

        shortcutResDiv.fadeIn();
        resultDiv.fadeIn();
        closeModal();

    }).fail(function (data) {
        actionError(data.status + " - " + data.statusText);
    });
}