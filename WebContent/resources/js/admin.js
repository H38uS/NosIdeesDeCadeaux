
function submitSuppressionCompte(e) {

	e.preventDefault();
	
	var userId = $(this).prev().val();
	var tr = $(this).closest("tr");
	var message;
	var userName;
	
	$.ajax({
		type: 'GET',
		url: 'protected/service/get_user_name',
		data : { userId : userId },
		success: function(data) {
			if ( typeof data.status === "undefined" || data.status !== 'ok' ) {
				message = "Êtes-vous sûr de vouloire supprimer ce compte ?";
				userName = '';
			} else {
				message = "Êtes-vous sûr de vouloire supprimer le compte " + data.message + " ?";
				userName = data.message + " ";
			}
		},
		dataType: "json",
		async:false
	});
	
	if (confirm(message)) {
		servicePost('protected/administration/service/supprimer_compte',
				{ userId : userId },
				function(data) {
					tr.fadeOut('slow');
				},
				"Suppression du compte " + userName + "en cours...",
		"Compte " + userName + "supprimé !");
	}
}

$(document).ready(function() {
	$('.form_suppression_compte_submit').click(submitSuppressionCompte);
});