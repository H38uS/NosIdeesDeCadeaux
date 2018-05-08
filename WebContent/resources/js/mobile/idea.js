previous = null;

$(document).ready(function() {
	$(".idea_square").click(function() {
		
		if (previous != null && previous != this) {
			$(previous).find(".outer_top_tooltiptext").hide();
			$(previous).removeClass("idea_square_selected");
		}
		previous = this;
		
		$(this).toggleClass("idea_square_selected");
		$(this).find(".outer_top_tooltiptext").toggle("slide", { direction: "right" });
	});
});