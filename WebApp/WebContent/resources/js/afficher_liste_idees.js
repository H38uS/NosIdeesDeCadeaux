function displayUsersIdeasList(identicCallBack, callBack) {
    doLoading("Récupération des idées en cours...");
    var dataServiceURL = callBack.replace("protected/", "protected/service/");
    $.get(  dataServiceURL,
            { id   : getURLParameter(identicCallBack, "id"),
              name : getURLParameter(identicCallBack, "name")
            }
    ).done(function (data) {

        var rawData = JSON.parse(data);
        if (rawData.status !== 'OK') {
            actionError(rawData.message);
            return;
        }

        var resultDiv = $("#resultPlaceholderForLists");
        resultDiv.empty().hide();

        var jsonData = rawData.message;
        var connectedUser = rawData.connectedUser;

        $.each(jsonData, function(i, ownerIdeas) {
            var ownerTitle = getH2UserTitle(ownerIdeas.owner, connectedUser);
            resultDiv.append(ownerTitle);
            $.each(ownerIdeas.ideas, function(j, idea) {
                resultDiv.append(getIdeaDiv(connectedUser, idea));
            });
        });

        resultDiv.fadeIn();
        closeModal();

    }).fail(function (data) {
        actionError(data.status + " - " + data.statusText);
    });
}