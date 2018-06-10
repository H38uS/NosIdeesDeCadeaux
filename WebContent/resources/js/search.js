const minLength = 3;
const minTempsReflexion = 500;

var completed = true;

function doSearch(value, only_non_friend) {

	if (value.length < minLength || !completed)
		return;

	completed = false;
	if (only_non_friend) only_non_friend = "on";
	$("#res").html('<img alt="Chargement..." src="resources/image/big_ajax_loader.gif" height="280" width="280" />');

	$.post('protected/service/rechercher_personne', { name : value, only_non_friend : only_non_friend }, function(data) {
		$("#res").html(data);
		$("#res li").hide();
		setTimeout(function() {
			$("#res li").each(function(i) {
			    $(this).delay(100 * i).fadeIn('slow');
			});
			completed = true;
		}, minTempsReflexion);
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