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

$(document).ready(function() {
	$(".drop_relationship").click(dropRelationship);
});