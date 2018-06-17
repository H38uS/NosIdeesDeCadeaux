myTooltipsterInfoParam={delay: 200, position: 'bottom', theme: 'tooltipster-default'};
myTooltipsterPrioParam={delay: 800, position: 'bottom', contentAsHTML: true, theme: 'tooltipster-html'};

$(document).ready(function() {
	$("span.checkbox").click(function() {
		var checkBoxes = $(this).prev();
		checkBoxes.prop("checked", !checkBoxes.prop("checked"));
	});
	$('#imageFile').change(function() {
		$('#newImage').text($(this).val());
	});

	if (typeof ($().tooltipster) === "function") {
		// Tooltip pour tout sauf les images : information supplémentaire
		$('[title]').not('img').tooltipster(myTooltipsterPrioParam);

		// Tooltip pour les images : information sur l'action
		$('img[title]').tooltipster(myTooltipsterInfoParam);
	}
	
	$(".menu_icon").click(function () {
		$("#menu_content").toggle("slide");
	});
	
	$("#ma_liste_deplier").click(function () {
		$(this).hide();
		$(this).parent().parents("#ma_liste_table_ajouter").find("tr").show("slow");
	});
});

function getPictureWidth() {
	if ( $("#mobile_res_search").css('display') == 'none' ) {
		return 30;
	} else {
		return 80;
	}
}

function doLoading(message) {
	$("#loading_message_div").hide()
							 .removeClass()
							 .html(message)
							 .addClass('loading')
							 .slideDown();
}
function actionDone(message) {
	$("#loading_message_div").hide()
							 .removeClass()
							 .html(message)
							 .addClass('success')
							 .slideDown();
	setTimeout(function() {
		$("#loading_message_div").fadeOut('slow');
	}, 7000);
}
function actionError(message) {
	$("#loading_message_div").hide()
							 .removeClass()
							 .html(message)
							 .addClass('fail')
							 .slideDown();
	setTimeout(function() {
		$("#loading_message_div").fadeOut('slow');
	}, 7000);
}

function servicePost(url, params, successHandler, loadingMessage, successMessage, errorMessage) {

	doLoading('<img src="resources/image/loading.gif" width="' + getPictureWidth() + '" />' + loadingMessage);

	$.post(url, params, function(data) {
		if ( typeof data.status === "undefined" || data.status !== 'ok' ) {
			if ( typeof errorMessage === "undefined" ) {
				errorMessage = "Echec de la mise à jour, veuillez réessayer.<br/> Si cela ne fonctionne pas à nouveau, essayez de recharger la page (touche F5).";
			}
			actionError('<img src="resources/image/ko.png" width="' + getPictureWidth() + '" /> ' + errorMessage);
		} else {
			actionDone('<img src="resources/image/ok.png" width="' + getPictureWidth() + '" />' + successMessage);
			successHandler(data);
		}
	}, "json")
	.fail(function() {
		actionError('<img src="resources/image/ko.png" width="' + getPictureWidth() + '" />' + "Une erreur est survenue... Veuillez réessayer.<br/> Si cela se reproduit, envoyer un email à jordan.mosio@hotmail.fr avec la description de l'action.");
	});
}

function getURLParameter(url, name) {
	var results = new RegExp('[\?&]' + name + '=([^&]*)').exec(url);
	if (results == null) {
		return null;
	} else {
		return results[1] || 0;
	}
}