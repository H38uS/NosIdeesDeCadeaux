
function submitSuppressionCompte(e) {

	e.preventDefault();
	
	var userId = $(this).prev().val();
	var person = $(this).closest(".person_card");
	var message;
	var userName;
	
	$.ajax({
		type: 'GET',
		url: 'protected/service/get_user_name',
		data : { userId : userId },
		success: function(data) {
			console.log(data);
			if ( typeof data.status === "undefined" || data.status !== 'OK' ) {
				message = "Êtes-vous sûr de vouloire supprimer ce compte ?";
				userName = '';
			} else {
				message = "Êtes-vous sûr de vouloire supprimer le compte " + data.message.email + " ?";
				userName = data.message.email + " ";
			}
		},
		dataType: "json",
		async:false
	});
	
	if (confirm(message)) {
		servicePost('protected/administration/service/supprimer_compte',
				{ userId : userId },
				function(data) {
					person.fadeOut('slow');
				},
				"Suppression du compte " + userName + "en cours...",
		"Compte " + userName + "supprimé !");
	}
}

$(document).ready(function() {
	$('.form_suppression_compte_submit').click(submitSuppressionCompte);
});