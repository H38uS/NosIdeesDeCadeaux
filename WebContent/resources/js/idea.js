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
	var from = getURLParameter($(this).attr("href"), 'from');
	var idea = $(this).closest('.idea_square');
	
	servicePost('protected/service/reserver',
				{ idee : id },
				function(data) {
					refreshIdea(idea, id, from);
				},
				"Réservation de l'idée en cours...",
				"L'idée a bien été réservée.");
}

function dereserverIdea(e) {

	e.preventDefault();
	var id = getURLParameter($(this).attr("href"), 'idee');
	var from = getURLParameter($(this).attr("href"), 'from');
	var idea = $(this).closest('.idea_square');
	
	servicePost('protected/service/dereserver',
				{ idee : id },
				function(data) {
					refreshIdea(idea, id, from);
				},
				"Annulation de la réservation en cours...",
				"La réservation de l'idée a bien été annulée.");
}

function refreshIdea(idea, id, from) {
	$.get("protected/service/get_idea_of_friend",
		  {idee : id, from : from},
		  function (data) {
			idea.hide();
			idea.wrap("<span></span>");
			var div = idea.parent();
			div.html(data);
			var newIdea = div.children();
			newIdea.hide();
			newIdea.unwrap();
			setIdeaActionsToJs(newIdea);
			newIdea.fadeIn('slow');
		  },
		  "html")
	.fail(function () {
		// Osef pour le moment, on a pas réussi à changer l'idée
	});
}

function setIdeaActionsToJs(my_idea) {
	my_idea.find("a.idea_remove").click(deleteIdea);
	my_idea.find("a.idea_est_a_jour").click(estAJourIdea);
	my_idea.find("a.idea_reserver").click(reserverIdea);
	my_idea.find("a.idea_dereserver").click(dereserverIdea);
}

$(document).ready(function() {
	$("a.idea_remove").click(deleteIdea);
	$("a.idea_est_a_jour").click(estAJourIdea);
	$("a.idea_reserver").click(reserverIdea);
	$("a.idea_dereserver").click(dereserverIdea);
});