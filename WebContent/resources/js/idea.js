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
	var from = getURLParameter($(this).attr("href"), 'from');
	var idea = $(this).closest('.idea_square');
	
	servicePost('protected/service/est_a_jour',
			{ idee : id },
			function(data) {
				refreshIdea(idea, id, from);
			},
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
	my_idea.find(".mobile_actions").click(function () {
		my_idea.find(".modal").modal('show');
	});
	my_idea.find(".modal").on('show.bs.modal', function (e) {
		lastModalOpened = $(this);
	});
}

var postIdea = function(form) {
	var xhr = new XMLHttpRequest();
	xhr.open('POST', form.attr("action"));
	xhr.onload = function() {
		actionDone("L'idée a bien été crée ou mise à jour sur le serveur.");
	};
	xhr.onerror = function() {
		actionError(this.responseText)
	};
	xhr.upload.onprogress = function(event) {
		var percent = (event.loaded / event.total) * 100;
		doLoading("Envoie en cours (" + percent + "%)...")
	}

	var formData = new FormData();
	formData.append('myfile', selectedPicture);
	formData.append('text', form.find("#text").val());
	formData.append('type', form.find("#type").val());
	formData.append('priority', form.find("#priority").val());
	formData.append('old_picture', form.find("#old_picture").val());
	xhr.send(formData);
};

$(document).ready(function() {
	$("a.idea_remove").click(deleteIdea);
	$("a.idea_est_a_jour").click(estAJourIdea);
	$("a.idea_reserver").click(reserverIdea);
	$("a.idea_dereserver").click(dereserverIdea);
	$(".post_idea").click(function (e) {
		e.preventDefault();
		var form = $(this).closest('form');
		postIdea(form);
	});
});