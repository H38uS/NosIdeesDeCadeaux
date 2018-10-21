$(document).ready(function() {

	var tr = $('#newImage').closest("tr");

	tr.on('dragover', function(e) {
		e.preventDefault();
		e.stopPropagation();
	});
	tr.on('dragenter', function(e) {
		e.preventDefault();
		e.stopPropagation();
		$('#newImage').toggleClass("picture_drag_over");
	});
	tr.on('dragleave', function(e) {
		e.preventDefault();
		e.stopPropagation();
		$('#newImage').toggleClass("picture_drag_over");
	});
	tr.on(
		'drop',
		function(e) {
			if (e.originalEvent.dataTransfer
					&& e.originalEvent.dataTransfer.files.length) {
				e.preventDefault();
				e.stopPropagation();
				$('#newImage').toggleClass("picture_drag_over");
				$('#imageFile').prop('files', e.originalEvent.dataTransfer.files);
			}
		});
});