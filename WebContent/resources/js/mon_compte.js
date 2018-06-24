function submitMainForm(e) {

	if (window.FormData !== undefined) // for HTML5 browsers 
	{
		doLoading('<img src="resources/image/loading.gif" width="' + getPictureWidth() + '" /> Enregistrement en cours...');
		
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
					actionError('<img src="resources/image/ko.png" width="' + getPictureWidth() + '" /> ' + errorMessage);
				} else {
					var successMessage = "Mise à jour effectuée avec succès.";
					actionDone('<img src="resources/image/ok.png" width="' + getPictureWidth() + '" />' + successMessage);
				}
			},
			error : function(jqXHR, textStatus, errorThrown) {
				actionError('<img src="resources/image/ko.png" width="' + getPictureWidth() + '" />' + "Une erreur est survenue: " + jqXHR.statusText + " (" + jqXHR.status + ")<br/> Si cela se reproduit, envoyer un email à jordan.mosio@hotmail.fr avec la description de l'action.")
			}
		});
		e.preventDefault();
	}

}

$(document).ready(function() {
	$("#submit_main_form").click(submitMainForm);
});