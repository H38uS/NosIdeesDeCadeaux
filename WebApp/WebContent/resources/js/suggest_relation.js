var userId = getURLParameter($(location).attr("href"), 'id');
var name = $("#name").val();

function submit_suggestions() {

    var selected = [];
    $('input[name^="selected_"]').each(function() {
        selected.push([$(this).attr("id"), $(this).is(":checked")]);
    });
    servicePost('protected/service/suggestion_rejoindre_reseau',
                {
                    id : userId,
                    selected : selected
                },
                function(data) {
                    loadPossibleSuggestions(userId, $("#name").val());
                },
                'Envoie des suggestions en cours...',
                'Les suggestions ont bien été envoyées !');
}

function loadPossibleSuggestions(userId, name) {
    $.get("protected/service/possible_relation_suggestions",
          { id: userId, name: name})
    .done(function (data) {
        var rawData = JSON.parse(data);
        if (rawData.status !== 'OK') {
            actionError(rawData.message);
            return;
        }

        var resDiv = $("#res_possible_suggestions");
        resDiv.empty().hide();

        var container = $(`<div class="container border border-info bg-light rounded my-3 p-2"></div>`);

        var jsonData = rawData.message;
        if (jsonData.length === 0) {
            container.append("Aucune suggestion possible à faire...");
            return;
        }

        $.each(jsonData, function(i, suggestion) {
            var content = $(`<div class="col-12 mx-2"></div>`);
            if (suggestion.isPossible) {
                var suggestion_user = suggestion.possibleSuggestion;
                content.append(`
                    <input type="checkbox" class="form-check-input" name="selected_${suggestion_user.id}" id="selected_${suggestion_user.id}" />
                    <label class="form-check-label d-inline-block" for="selected_${suggestion_user.id}">${suggestion_user.name} - </label>
                    <label class="form-check-label d-inline-block" for="selected_${suggestion_user.id}">${suggestion_user.email}</label>
                `);
            } else {
                content.text(suggestion.reasonIfNotPossible);
            }
            container.append(content);
        });
        container.append(`
            <div class="center w-100">
                <button class="btn btn-primary" type="submit" id="submit_suggestions">Envoyer les suggestions</button>
            </div>
        `);

        resDiv.append(container).fadeIn();
        $("#submit_suggestions").click(submit_suggestions);

    }).fail(function (data) {
        actionError(data.status + " - " + data.statusText);
    });
}

loadPossibleSuggestions(userId, name);

$("#submit_filter").click(function () {
    loadPossibleSuggestions(userId, $("#name").val());
});
