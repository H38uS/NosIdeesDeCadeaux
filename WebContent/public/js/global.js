$(document).ready(function() {
	$("span.checkbox").click(function() {
		var checkBoxes = $(this).prev();
		checkBoxes.prop("checked", !checkBoxes.prop("checked"));
	})
});