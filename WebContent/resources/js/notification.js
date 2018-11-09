function deleteNotification(e) {

	e.preventDefault();
	var id = getURLParameter($(this).attr("href"), 'notif_id');

	var card = $(this).closest('.card');
	servicePost('protected/service/notification_delete',
				{ notif_id : id },
				function(data) {
					card.fadeOut('slow');
				},
				'Suppression de la notification en cours...',
				'La notification a bien été supprimée.');
}

$(document).ready(function() {
	$("a.notif_delete").click(deleteNotification);
});