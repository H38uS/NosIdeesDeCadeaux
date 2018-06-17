function deleteNotification(e) {

	e.preventDefault();
	var id = getURLParameter($(this).attr("href"), 'notif_id');

	var tr = $(this).closest('tr');
	var table = tr.closest('table');
	var count = table.find('tr').length;
	
	servicePost('protected/service/notification_delete',
				{ notif_id : id },
				function(data) {
					if (count == 2) {
						table.fadeOut('slow');
					} else {
						tr.fadeOut('slow');
					}
				},
				'Suppression de la notification en cours...',
				'La notification a bien été supprimée.');
}

$(document).ready(function() {
	$("a.notif_delete").click(deleteNotification);
});