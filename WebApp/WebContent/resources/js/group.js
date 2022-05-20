
function getGroupDetailSection(groupContent) {
    var res = $("<div>");
    res.append("<h2>Détail du groupe</h2>");
    res.append(`<p>Montant total souhaité : ${groupContent.group.formattedTotal}€</p>`);
    var shares = $("<ul>");
    $.each(groupContent.group.ideaGroupContents, function(i, share) {
        shares.append(`<li>${share.user.name} : ${share.formattedAmount}€ - <small>depuis le ${share.formattedDate}</small></li>`);
    });
    res.append(shares);
    return res;
}

function getActionDiv(groupContent) {
    var res = $("<div>");
    res.append("<h2>Vos actions</h2>");
    if (groupContent.isInGroup) {
        res.append(`
            <div class="form-inline d-inline">
                <label for="amount" class="d-none d-lg-inline-block">Modifier le montant :</label>
                <input id="amount" class="form-control mt-2 mt-md-0" name="amount" type="text" value="${groupContent.userIdeaGroupContent.formattedAmount}" />
                <input type="hidden" name="groupid" value="${groupContent.group.id}" />
                <button class="btn btn-primary mt-2 mt-md-0" type="submit" name="submit" id="submitParticiper">Modifier !</button>
            </div>
            <div class="form-inline d-inline-block">
                <input type="hidden" name="groupid" value="${groupContent.group.id}" />
                <button class="btn btn-primary mt-2 mt-md-0" type="submit" name="submit" id="submitAnnulation">Annuler ma participation</button>
            </div>
        `);
    } else {
        res.append('<div>Vous ne participez pas encore à ce groupe.</div>');
        res.append(`
            <div class="form-inline">
                <label class="d-none d-sm-inline-block">S'inscrire: </label>
                <input class="form-control mx-2" name="amount" type="text" value="" />
                <input type="hidden" name="groupid" value="${groupContent.group.id}" />
                <button class="btn btn-primary mt-2 mt-sm-0 mx-auto mx-sm-0" type="submit" name="submit" id="submitParticiper">Participer</button>
            </div>
        `);
    }
    return res;
}

function getSuggestionDiv(groupContent) {
    var res = $("<div>");
    if (groupContent.group.total > groupContent.currentTotal) {
        res.append(`<h3 class="mt-3">Suggérer ce groupe à quelqu'un</h3>`);
        res.append(`Il manque un peu (${groupContent.remaining}€ très exactement)... N'hésitez plus, <a href="protected/suggerer_groupe_idee?groupid=${groupContent.group.id}">suggérer</a> ce groupe à d'autres personnes !`);
    }
    return res;
}

function participer() {
    var groupId = $(this).prev().val();
    var amount = $(this).prev().prev().val();
    servicePost('protected/service/group/participation',
                {
                    groupid : groupId,
                    amount  : amount
                },
                function(data) {
                    refreshGroup(showTheSuggestLink);
                },
                "Enregistrement de votre participation en cours...",
                "Votre participation a bien été enregistrée.");
}

function annulationParticipation() {
    var groupId = $(this).prev().val();
    servicePost('protected/service/group/annulation',
                { groupid : groupId },
                function(data) {
                    if (data.message) {
                        // true s'il reste encore des personnes dans ce groupe
                        refreshGroup(showTheSuggestLink);
                    } else {
                        $("#groupDetail").empty();
                        var idea = $(".idea_square");
                        refreshIdea(idea, idea.attr("id").substring(5));
                        actionDone("Vous êtiez le dernier participant... Le groupe a été supprimé !");
                    }
                },
                "Annulation de votre participation en cours...",
                "Votre participation a bien été annulée.");
}

var showTheSuggestLink = false;
function refreshGroup(showSuggest) {
    var groupId = getURLParameter($(location).attr('href'), "groupid");
    refreshGroupWithId(groupId, showSuggest);
}

function refreshGroupWithId(groupId, showSuggest) {

    showTheSuggestLink = showSuggest;

    $.get("protected/service/group/detail",
          { groupid : groupId }
    ).done(function (data) {

        var rawData = JSON.parse(data);
        if (rawData.status !== 'OK') {
          actionError(rawData.message);
          return;
        }
        var groupContent = rawData.message;

        $("#groupDetail").empty().hide();
        $("#groupDetail").append(getGroupDetailSection(groupContent).html());
        $("#groupDetail").append(getActionDiv(groupContent).html());
        if (showTheSuggestLink) {
            $("#groupDetail").append(getSuggestionDiv(groupContent).html());
        }
        $("#groupDetail").find("#submitParticiper").click(participer);
        $("#groupDetail").find("#submitAnnulation").click(annulationParticipation);

        $("#groupDetail").fadeIn();

    }).fail(function (data) {
        actionError(data.status + " - " + data.statusText);
    });
}

// ============== Suggestion de groupes

function suggestNames() {
    var selectedUsers = [];
    var form = $(this).closest("#suggestForm");
    form.find('input[type=checkbox]:checked').each(function(index) {
        selectedUsers.push($(this).attr("name").substring(2));
    });

    if (selectedUsers.length == 0) {
        // No user selected
        actionError("Vous devez au moins sélectionner une personne.");
        return;
    }

    var groupId = form.find("input[name=groupid]").val();
    servicePost('protected/service/group/suggest',
                {
                    groupid : groupId,
                    userIds : selectedUsers
                },
                function(data) {
                    refreshPossibleSuggestion();
                    if (data.message.length == 0) {
                        actionError("Aucune demande envoyée...");
                    } else {
                        var users = [];
                        $.each(data.message, function(i, user) {
                            users.push(user.name);
                        });
                        actionDone("Une demande à bien été envoyée à " + users);
                    }
                },
                "Annulation de votre participation en cours...",
                "");
}

function refreshPossibleSuggestion() {

    var groupId = getURLParameter($(location).attr('href'), "groupid");

    $.get("protected/service/group/suggest",
          { groupid : groupId }
    ).done(function (data) {

        var rawData = JSON.parse(data);
        if (rawData.status !== 'OK') {
          actionError(rawData.message);
          return;
        }

        var candidates = rawData.message;
        $("#suggestArea").empty().hide();

        if (candidates.length === 0) {
            $("#suggestArea").append(`
                <div class="alert alert-warning">
                    Vous ne connaissez personne qui serait susceptible de participer à ce cadeau...
                </div>
            `);
        } else {
            var suggestContainer = $(`<div class="alert alert-primary">`);
            $("#suggestArea").append(suggestContainer);
            suggestContainer.append("<h4>Partagez ce groupe avec</h4>");
            var suggestForm = $(`<div id="suggestForm">`);
            suggestContainer.append(suggestForm);
            suggestForm.append(`<input type="hidden" name="groupid" value="${groupId}" />`);
            $.each(candidates, function(i, candidate) {
                suggestForm.append(`
                    <div class="col-12 col-sm-5 col-lg-3 mx-2">
                        <input type="checkbox"  class="form-check-input" id="cb${candidate.id}" name="cb${candidate.id}" />
                        <label class="d-inline-block" for="cb${candidate.id}">${candidate.name}</label>
                    </div>
                `);
            });
            suggestForm.append(`
                <div class="center w-100">
                    <button class="btn btn-primary" type="submit" name="submit" id="submit-suggestion">Suggérer !</button>
                </div>
            `);

            suggestForm.find("#submit-suggestion").click(suggestNames);
        }

        $("#suggestArea").fadeIn();

    }).fail(function (data) {
        actionError(data.status + " - " + data.statusText);
    });
}
