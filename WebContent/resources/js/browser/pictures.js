$(document).ready(function() {

	$('#newImage').text("Faites glisser une image ici, ou coller votre impression d'écran (Greenshot)");
	
	// Drag & drop
	var dragTarget = $('#newImage').closest("form");

	dragTarget.on('dragover', function(e) {
		e.preventDefault();
		e.stopPropagation();
	});
	dragTarget.on('dragenter', function(e) {
		e.preventDefault();
		e.stopPropagation();
		$('#newImage').toggleClass("picture_drag_over");
		$('#newImage').toggleClass("picture_not_drag");
	});
	dragTarget.on('dragleave', function(e) {
		e.preventDefault();
		e.stopPropagation();
		$('#newImage').toggleClass("picture_drag_over");
		$('#newImage').toggleClass("picture_not_drag");
	});
	dragTarget.on(
		'drop',
		function(e) {
			var files = (e.dataTransfer || e.originalEvent.dataTransfer).files;
			if (files.length == 1) {

				var name = files[0].name;
				var ext = name.split('.').reverse()[0].toLowerCase();
				if (jQuery.inArray(ext, ['jpg', 'jpeg', 'png']) < 0) {
					alert("L'extension n'est pas supportée !");
					return;
				}

				e.preventDefault();
				e.stopPropagation();

				$('#newImage').toggleClass("picture_drag_over");
				$('#newImage').toggleClass("picture_not_drag");
				$('#imageFile').prop('files', files);
				pictureNeedsRefresh = true;
				loadPreview();
			}
		});
});

//Copy / paste
jQuery(document).bind("paste", function(event) {

	var files = (event.clipboardData || event.originalEvent.clipboardData).files;

	// Make Sure Only One File is Copied* /
	if (files.length != 1) {
		return;
	}

	var file = files[0];
	var filename = file.name;
	var ext = filename.split('.').reverse()[0].toLowerCase();
	if (jQuery.inArray(ext, [ 'jpg', 'jpeg', 'png' ]) > -1) {
		event.preventDefault();
		event.stopPropagation();
		$('#imageFile').prop('files', files);
		pictureNeedsRefresh = true;
		loadPreview();
	}
});
