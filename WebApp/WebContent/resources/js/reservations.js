function loadReservations() {
	doLoading("Récupération des réservations en cours...");
	$.get(	"protected/service/mes_reservations",
			{}
	).done(function (data) {
		
		var rawData = JSON.parse(data);
		if (rawData.status !== 'OK') {
			actionError(rawData.message);
			return;
		}
		
		var content = $("<div></div>");
		var jsonData = rawData.message;
		if (jsonData.length === 0) {
			content.addClass("alert alert-warning");
			content.text("Vous n'avez aucune réservation pour le moment !");
		}

		$.each(jsonData, function(i, ownerIdeas) {
			var title = $("<h5></h5>");
			var owner = $('<a href="protected/voir_liste?id=' + ownerIdeas.owner.id + '" >' + ownerIdeas.owner.name + '</a>');
			title.html(owner);
			var ideaNb = ownerIdeas.ideas.length;
			var lastText = ideaNb > 1 ? " idées réservées" : " idée réservée";
			title.append(" (" + ownerIdeas.ideas.length + lastText + ")");
			content.append(title);
			var myul = $("<ul></ul>");
			content.append(myul);
			$.each(ownerIdeas.ideas, function(j, ideas) {
				var row = $("<li></li>");
				row.addClass("");
				row.html(ideas.text);
				myul.append(row);
			});
		});
		
		$("#reservation_res_area").hide().html(content).fadeIn();
		closeModal();
	}).fail(function (data) {
		actionError(data.status + " - " + data.statusText);
	});
}

loadReservations();
