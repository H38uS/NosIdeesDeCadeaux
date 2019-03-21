function submitMainForm(e) {

	if (window.FormData !== undefined) // for HTML5 browsers 
	{
		e.preventDefault();
		doLoading('Enregistrement en cours...');
		
		var my_form = $("#form_main_change_mon_compte");
		var formData = new FormData(my_form[0]);
		$.ajax({
			url : 'protected/service/enregistrement_mon_compte',
			type : 'POST',
			data : formData,
			dataType: "json",
			mimeType : "multipart/form-data",
			contentType : false,
			cache : false,
			processData : false,
			success : function(data, textStatus, jqXHR) {
				if ( typeof data.status === "undefined" || data.status !== 'ok' ) {
					var errorMessage = "Echec de la mise à jour. Des erreurs ont empêché la sauvegarde:<br/>" + data.errors;
					actionError(errorMessage);
				} else {
					var successMessage = "Mise à jour effectuée avec succès.";
					actionDone(successMessage);
					
					var my_link = my_form.find("#avatar_picture");
					my_link.attr("href", data.avatars + "/" + data.avatarLarge);
					my_link.children().attr("src", data.avatars + "/" + data.avatarSmall);
					my_link.next().attr("value", data.avatar);
				}
			},
			error : function(jqXHR, textStatus, errorThrown) {
				actionError("Une erreur est survenue: " + jqXHR.statusText + " (" + jqXHR.status + ")<br/> Si cela se reproduit, envoyer un email à jordan.mosio@hotmail.fr avec la description de l'action.")
			}
		});
	}

}

function submitNotificationForm(e) {

	e.preventDefault();

	var form = $(this).closest( "form" );
	var name = form.find("input[name=name]").val();
	var value = form.find("select[name=value]").val();

	servicePost('protected/service/update_notification_parameter',
			{ name : name, value : value },
			function(data) {},
			"Enregistrement de la configuraiton de la notification...",
			"Modification sauvegardée !");
}

function addParent(e) {
	e.preventDefault();
	var name = $("#input_add_parent").val();
	servicePost('protected/service/ajouter_parent',
			{ name : name },
			function(data) {
				var my_ul = $("#parent_names_list");
				if ( typeof my_ul.html() === "undefined" ) {
					var form = $("#input_add_parent").closest( "form" );
					form.prev().remove();
					form.before('<ul id="parent_names_list"></ul>');
					my_ul = form.prev();
				}
				my_ul.append("<li>" + data.name + "</li>");
			},
			"Ajout du parent en cours...",
			"Ajout effectué avec succès !");
}

$(document).ready(function() {
	$("#submit_main_form").click(submitMainForm);
	$(".notification_form_submit").click(submitNotificationForm);
	$("#btn_add_parent").click(addParent);
	$("#input_add_parent").autocomplete({
		source : "protected/service/name_resolver",
		minLength : 2,
		select : function(event, ui) {
			$("#input_add_parent").val(ui.item.email);
			addParent(event);
			return false;
		}
	});
});