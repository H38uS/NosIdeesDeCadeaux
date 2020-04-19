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

        var reservationsTable = $(`
        <table class="table table-striped">
            <thead class="thead-dark">
                <tr>
                    <th scope="col">L'heureux(se) élu(e)</th>
                    <th scope="col">Idée</th>
                </tr>
            </thead>
            <tbody></tbody>
        </table>`);
        var tableBody = reservationsTable.find("tbody");

        $.each(jsonData, function(i, ownerIdeas) {
            $.each(ownerIdeas.ideas, function(j, idea) {
                var category = idea.cat == null ? "":  `<img src="resources/image/type/${idea.cat.image}"
                                                               title="${idea.cat.title}"
                                                               alt="${idea.cat.alt}"
                                                               width="${getPictureWidth()}px"/>`;
                var image = idea.image == null ? "" : `<img src="${picturePath}/small/${idea.image}"
                                                          width="150"/>
                                                       <span class="d-lg-none"><br/></span>
                                                      `;
                var newRow = $(`
                <tr>
                    <td>
                        <a href="protected/voir_liste?id=${ownerIdeas.owner.id}">
                            ${ownerIdeas.owner.name}
                        </a>
                    </td>
                    <td>
                        ${category}
                        ${image}
                        ${idea.htmlText}
                    </td>
                </tr>
                `);
                tableBody.append(newRow);
            });
        });

        $("#reservation_res_area").hide().html(reservationsTable).fadeIn();
        closeModal();
    }).fail(function (data) {
        actionError(data.status + " - " + data.statusText);
    });
}

loadReservations();
