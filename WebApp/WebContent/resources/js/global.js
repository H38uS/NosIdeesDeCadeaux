myTooltipsterInfoParam={delay: 200, position: 'bottom', theme: 'tooltipster-default'};
myTooltipsterPrioParam={delay: 800, position: 'bottom', contentAsHTML: true, theme: 'tooltipster-html'};

var avatarsPath = "protected/files/uploaded_pictures/avatars";
pictureNeedsRefresh = false;
selectedPicture = "";
selectedPictureName = "";

var dataURLToBlob = function(dataURL) {
	var BASE64_MARKER = ';base64,';
	if (dataURL.indexOf(BASE64_MARKER) == -1) {
		var parts = dataURL.split(',');
		var contentType = parts[0].split(':')[1];
		var raw = parts[1];

		return new Blob([ raw ], { type : contentType });
	}

	var parts = dataURL.split(BASE64_MARKER);
	var contentType = parts[0].split(':')[1];
	var raw = window.atob(parts[1]);
	var rawLength = raw.length;

	var uInt8Array = new Uint8Array(rawLength);

	for (var i = 0; i < rawLength; ++i) {
		uInt8Array[i] = raw.charCodeAt(i);
	}
	return new Blob([ uInt8Array ], { type : contentType });
};

function loadPreview(e) {

	if (!pictureNeedsRefresh) {
		return;
	}
	pictureNeedsRefresh = false;
	
	var inputFiles = $('#imageFile').prop('files');
	if (inputFiles == undefined || inputFiles.length == 0)
		return;
	var inputFile = inputFiles[0];
	
	var fileName = inputFile.name;
	selectedPictureName = fileName;
	$('#newImage').text(fileName);

	var reader = new FileReader();
	reader.onload = function(event) {
		
		// Retaillage de l'image
		var image = new Image();
		image.src = event.target.result;
		
		image.onload = function() {

			var maxWidth = 1920, maxHeight = 1080;
			var imageWidth = image.width, imageHeight = image.height;
			
			// Calcul de la nouvelle taille
			var newWidth = imageWidth > maxWidth ? maxWidth : imageWidth;
			var newHeight = (newWidth * imageHeight) / imageWidth;
			if (newHeight > maxHeight) {
				newWidth = (maxHeight * newWidth) / newHeight;
				newHeight = maxHeight;
			}
		
			var canvas = document.createElement('canvas');
			canvas.width = newWidth;
			canvas.height = newHeight;
			image.width = newWidth;
			image.height = newHeight;
			var ctx = canvas.getContext("2d");
			ctx.drawImage(this, 0, 0, newWidth, newHeight);
				
			$("#imageFilePreview").attr("src", canvas.toDataURL(inputFile.type));
			selectedPicture = dataURLToBlob(canvas.toDataURL(inputFile.type));
		};
	};
		
	// no error for the preview --> reader.onerror = function(event) {};
	reader.readAsDataURL(inputFile);
}

var lastModalOpened;

$(document).ready(function() {
	$("span.checkbox").click(function() {
		var checkBoxes = $(this).prev();
		checkBoxes.prop("checked", !checkBoxes.prop("checked"));
	});
	$('#imageFile').on("change", function() {
		pictureNeedsRefresh = true;
		loadPreview();
	});

	if (typeof ($().tooltipster) === "function") {
		// Tooltip pour tout sauf les images : information supplémentaire
		$('[title]').not('img').tooltipster(myTooltipsterPrioParam);

		// Tooltip pour les images : information sur l'action
		$('img[title]').tooltipster(myTooltipsterInfoParam);
	}
	
	$('.modal').on('show.bs.modal', function (e) {
		lastModalOpened = $(this);
	});
	
	if ( $("#mobile_res_search").css('display') !== 'none' ) {
		// Mode mobile, on masque le reste quand on gagne le focus
		$("#header_name").focus(function () {
			$("nav").slideUp("fast");
		});
		$("#header_name").focusout(function () {
			$("nav").slideDown("fast");
		});
	}
});

function getPictureWidth() {
	if ( $("#mobile_res_search").css('display') == 'none' ) {
		return 30;
	} else {
		return 45;
	}
}

var picturePath = "protected/files/uploaded_pictures/ideas";

/* ********************* */
/* *** Loading Stuff *** */
/* ********************* */

var loadingTimeout; // Time before we display the loading animation
var timer;

function closeModal() {
	if (typeof lastModalOpened != 'undefined') {
		lastModalOpened.modal('hide');
	}
	$("body").removeClass("modal-open");
	$(".modal-backdrop").remove();
	clearTimeout(loadingTimeout);
	clearTimeout(timer);
	$("#loading_message_div").hide().removeClass();
}

function getHTMLPopUpMessage(image, message) {

    var row = $("<div></div>");
    row.addClass("row align-items-center");

    var pic = $("<div></div>");
    pic.addClass("col-auto");
    pic.html('<img src="resources/image/' + image + '" width="' + getPictureWidth() + '" />');

    var mess = $("<div></div>");
    mess.addClass("col");
    mess.html(message);

    row.append(pic);
    row.append(mess);

    return row;
}

function doLoading(message) {
	closeModal();
	loadingTimeout = setTimeout(function() {
		$("#loading_message_div").html(getHTMLPopUpMessage("loading.gif", message))
								 .addClass('loading')
								 .slideDown();
	}, 400);
}
function actionDone(message) {
	closeModal();
	$("#loading_message_div").html(getHTMLPopUpMessage("ok.png", message))
							 .addClass('success')
							 .slideDown();
	timer = setTimeout(function() {
		$("#loading_message_div").fadeOut('slow');
	}, 5000);
}
function actionError(message) {
	closeModal();
	$("#loading_message_div").html(getHTMLPopUpMessage("ko.png", message))
							 .addClass('fail')
							 .slideDown();
	timer = setTimeout(function() {
		$("#loading_message_div").fadeOut('slow');
	}, 5000);
}

/* ************************ */
/* *** Post / URL Stuff *** */
/* ************************ */

function servicePost(url, params, successHandler, loadingMessage, successMessage, errorMessage) {

	doLoading(loadingMessage);

	$.post(url, params, function(data) {
		if ( typeof data.status === "undefined" || data.status !== 'OK' ) {
			if ( typeof data.message === "undefined" || data.message === '' ) {
				if ( typeof errorMessage === "undefined" ) {
					errorMessage = "Echec de la mise à jour, veuillez réessayer.<br/> Si cela ne fonctionne pas à nouveau, essayez de recharger la page (touche F5).";
				}
			} else {
				errorMessage = 'Une erreur est survenue: ' + data.message;
			}
			actionError(errorMessage);
		} else {
			actionDone(successMessage);
			successHandler(data);
		}
	}, "json")
	.fail(function() {
		actionError("Une erreur est survenue... Veuillez réessayer.<br/> Si cela se reproduit, envoyer un email à jordan.mosio@hotmail.fr avec la description de l'action.");
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