function deleteIdea(e) {

	e.preventDefault();
	var id = getURLParameter($(this).attr("href"), 'ideeId');
	var idea = $(this).closest('.idea_square');
	
	servicePost('protected/service/delete_idea',
				{ ideeId : id },
				function(data) {
					idea.fadeOut('slow');
				},
				"Suppression de l'idée en cours...",
				"L'idée a bien été supprimée.");
}

$(document).ready(function() {
	$("a.idea_remove").click(deleteIdea);
});