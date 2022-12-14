function loadQuestions() {
    var ideaId = $("input[name=idee]").val();
    $.get("protected/service/idea_questions",
          {idea : ideaId}
    ).done(function (data) {
        var questions = $('#res_questions');
        var rawData = JSON.parse(data);
        if (rawData.status !== 'OK') {
            actionError(rawData.message);
            return;
        }

        var content = $('<span>');
        if (rawData.message.length === 0) {
            content.append(`<div class="alert alert-info">Aucun commentaire sur l'idée pour le moment.</div>`);
        } else {
            $.each(rawData.message, function(i, comment) {
                if (comment.isMyMessage) {
                    content.append(`
                        <div class="comment comment_mine">
                            <div class="comment_header_mine">
                                Posté par vous le ${comment.lastEditedOn} - le <a class="delete_question" href="protected/service/delete_question?id=${comment.id}">supprimer</a>
                            </div>
                            <div class="comment_text">${comment.htmlText}</div>
                        </div>
                    `);
                } else if (comment.isFromIdeaOwner) {
                    content.append(`
                        <div class="comment comment_owner">
                            <div class="comment_header_owner">Posté par ${comment.ideaOwner.name} le ${comment.lastEditedOn}</div>
                            <div class="comment_text">${comment.htmlText}</div>
                        </div>
                    `);
                } else {
                    content.append(`
                        <div class="comment comment_other">
                            <div class="comment_header_other">Posté par quelqu'un le ${comment.lastEditedOn}</div>
                            <div class="comment_text">${comment.htmlText}</div>
                        </div>
                    `);
                }
            });
        }

        questions.empty().hide();
        questions.append(content);
        questions.find(".delete_question").click(deleteMessage);
        questions.fadeIn('slow');
    }).fail(function (data) {
        actionError(data.status + " - " + data.statusText);
    });
}

function postMessage() {
    servicePost("protected/service/idea_questions",
                {
                  idea : $("input[name=idee]").val(),
                  text : $("#text").val()
                },
                function(data) {
                    loadQuestions();
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
    servicePost("protected/service/delete_question",
                {
                  id : messageId
                },
                function(data) {
                    loadQuestions();
                },
                "Suppression du message en cours...",
                "Message supprimé avec succès !");
}

loadQuestions();
$("#postMessage").click(postMessage);
