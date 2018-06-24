previous_idea_selected = null;

$(document).ready(function() {
	$(".idea_square").click(function() {
		
		if (previous_idea_selected != null && previous_idea_selected != this) {
			$(previous_idea_selected).find(".outer_top_tooltiptext").hide();
			$(previous_idea_selected).removeClass("idea_square_selected");
		}
		previous_idea_selected = this;
		
		$(this).toggleClass("idea_square_selected");
		$(this).find(".outer_top_tooltiptext").toggle("slide", { direction: "right" });
	});
});