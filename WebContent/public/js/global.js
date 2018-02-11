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
});