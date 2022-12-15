function loadComments() {
    var ideaId = $("input[name=idee]").val();
    $.get("protected/service/idea_comments",
          {idea : ideaId}
    ).done(function (data) {
        var comments = $('#res_comments');
        var rawData = JSON.parse(data);
        if (rawData.status !== 'OK') {
            actionError(rawData.message);
            return;
        }

        var content = $('<span>');
        if (rawData.message.length === 0) {
            content.append(`<div class="alert alert-info">Aucun commentaire sur l'idée pour le moment.</div>`);
        } else {

            var connectedUser = rawData.connectedUser;
            $.each(rawData.message, function(i, comment) {
                if (connectedUser.id === comment.writtenBy.id) {
                    content.append(`
                        <div class="comment comment_mine">
                            <div class="comment_header_mine">
                                Posté par vous le ${comment.lastEditedOn} - le <a class="delete_comment" href="protected/service/delete_comment?id=${comment.id}">supprimer</a>
                            </div>
                            <div class="comment_text">${comment.htmlText}</div>
                        </div>
                    `);
                } else {
                    content.append(`
                        <div class="comment comment_other">
                            <div class="comment_header_other">Posté par ${comment.writtenBy.name} le ${comment.lastEditedOn}</div>
                            <div class="comment_text">${comment.htmlText}</div>
                        </div>
                    `);
                }
            });
        }

        comments.empty().hide();
        comments.append(content);
        comments.find(".delete_comment").click(deleteMessage);
        comments.fadeIn('slow');
    }).fail(function (data) {
        actionError(data.status + " - " + data.statusText);
    });
}

function postMessage() {
    servicePost("protected/service/idea_comments",
                {
                  idea : $("input[name=idee]").val(),
                  text : $("#text").val()
                },
                function(data) {
                    loadComments();
                },
                "Enregistrement du message en cours...",
                "Message publié avec succès !");
}

function deleteMessage(e) {
    e.preventDefault();
    if (!confirm("Etes-vous sûr de supprimer ce message ?")) {
        return;
    }
    var messageId = getURLParameter($(this).attr("href"), 'id');
    servicePost("protected/service/delete_comment",
                {
                  id : messageId
                },
                function(data) {
                    loadComments();
                },
                "Suppression du message en cours...",
                "Message supprimé avec succès !");
}

loadComments();
$("#postMessage").click(postMessage);
