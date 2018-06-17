const minLength = 3;
const minTempsReflexion = 500;

var completed = true;

function doSearch(value, only_non_friend) {

	if (value.length < minLength || !completed)
		return;

	completed = false;
	if (only_non_friend) only_non_friend = "on";
	
	doLoading('<img src="resources/image/loading.gif" width="' + getPictureWidth() + '" />' + "Recherche en cours...");
	$("#res").html();

	$.post('protected/service/rechercher_personne', { name : value, only_non_friend : only_non_friend }, function(data) {
		$("#res").html(data);
		$("#res li").hide();
		setTimeout(function() {
			$("#res li").each(function(i) {
			    $(this).delay(100 * i).fadeIn('slow');
			});
			completed = true;
		}, minTempsReflexion);
		actionDone('<img src="resources/image/ok.png" width="' + getPictureWidth() + '" />' + 'Recherche terminée');
	}).fail(function() {
		actionError('<img src="resources/image/ko.png" width="' + getPictureWidth() + '" />' + "Une erreur est survenue... Veuillez réessayer.<br/> Si cela se reproduit, envoyer un email à jordan.mosio@hotmail.fr avec la description de l'action.");
	});

}

$(document).ready(function() {
	$("#name").keyup(function() {
		var text = $(this).val();
		doSearch(text, $("#only_non_friend").is(':checked'));
	});
	$("#label_only_non_friend").click(function() {
		setTimeout(function() {
			doSearch($("#name").val(), $("#only_non_friend").is(':checked'));
		}, 150);
	});
	$("#span_only_non_friend").click(function() {
		doSearch($("#name").val(), $("#only_non_friend").is(':checked'));
	});
});