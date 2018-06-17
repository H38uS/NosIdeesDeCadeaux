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

function estAJourIdea(e) {
	
	e.preventDefault();
	var id = getURLParameter($(this).attr("href"), 'idee');
	
	servicePost('protected/service/est_a_jour',
			{ idee : id },
			function(data) {},
			"Création de la demande en cours...",
			"La demande a bien été créée.",
			"Impossible de créer la demande... Peut-être existe-t-elle déjà ?");
}

function reserverIdea(e) {

	e.preventDefault();
	var id = getURLParameter($(this).attr("href"), 'idee');
	var idea = $(this).closest('.idea_square');
	
	servicePost('protected/service/reserver',
				{ idee : id },
				function(data) {
					// TODO mettre à jour les status d'idées...
				},
				"Réservation de l'idée en cours...",
				"L'idée a bien été réservée.");
}

function dereserverIdea(e) {

	e.preventDefault();
	var id = getURLParameter($(this).attr("href"), 'idee');
	var idea = $(this).closest('.idea_square');
	
	servicePost('protected/service/dereserver',
				{ idee : id },
				function(data) {
					// TODO mettre à jour les status d'idées...
				},
				"Annulation de la réservation en cours...",
				"La réservation de l'idée a bien été annulée.");
}

$(document).ready(function() {
	$("a.idea_remove").click(deleteIdea);
	$("a.idea_est_a_jour").click(estAJourIdea);
	$("a.idea_reserver").click(reserverIdea);
	$("a.idea_dereserver").click(dereserverIdea);
});