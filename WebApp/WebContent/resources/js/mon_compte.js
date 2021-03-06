function submitMainForm(e) {

    if (window.FormData !== undefined) // for HTML5 browsers
    {
        e.preventDefault();
        doLoading('Enregistrement en cours...');

        var my_form = $("#form_main_change_mon_compte");

        var formData = new FormData();
        formData.append('fileName', selectedPictureName);
        formData.append('email', my_form.find("#email").val());
        formData.append('name', my_form.find("#name").val());
        formData.append('birthday', my_form.find("#birthday").val());
        formData.append('old_picture', my_form.find("#old_picture").val());
        formData.append('new_password', my_form.find("#new_password").val());
        formData.append('conf_password', my_form.find("#conf_password").val());
        formData.append('modif_info_gen', "true");
        formData.append('image', selectedPicture);

        $.ajax({
            url : 'protected/service/enregistrement_mon_compte',
            type : 'POST',
            data : formData,
            dataType: "json",
            mimeType : "multipart/form-data",
            contentType : false,
            cache : false,
            processData : false,
            success : function(data, textStatus, jqXHR) {
                if ( typeof data.status === "undefined" || data.status !== 'OK' ) {
                    var errorMessage = "Echec de la mise à jour. Des erreurs ont empêché la sauvegarde:<br/>" + data.message;
                    actionError(errorMessage);
                } else {
                    actionDone("Mise à jour effectuée avec succès.");
                    var my_link = my_form.find("#avatar_picture");
                    my_link.attr("href", avatarsPath + "/large/" + data.message.avatar);
                    my_link.children().attr("src", avatarsPath + "/small/" + data.message.avatar);
                    my_link.next().attr("value", data.message.avatar);
                }
            },
            error : function(jqXHR, textStatus, errorThrown) {
                actionError("Une erreur est survenue: " + jqXHR.statusText + " (" + jqXHR.status + ")<br/> Si cela se reproduit, envoyer un email à jordan.mosio@hotmail.fr avec la description de l'action.")
            }
        });
    }

}

function submitNotificationForm(e) {

    e.preventDefault();

    var form = $(this).closest( "form" );
    var name = form.find("input[name=name]").val();
    var value = form.find("select[name=value]").val();

    servicePost('protected/service/update_notification_parameter',
            { name : name, value : value },
            function(data) {},
            "Enregistrement de la configuraiton de la notification...",
            "Modification sauvegardée !");
}

function addParent(e) {
    e.preventDefault();
    var name = $("#input_add_parent").val();
    servicePost('protected/service/ajouter_parent',
            { name : name },
            function(data) {
                var my_ul = $("#parent_names_list");
                if ( typeof my_ul.html() === "undefined" ) {
                    var form = $("#input_add_parent").closest( "form" );
                    form.prev().remove();
                    form.before('<ul id="parent_names_list"></ul>');
                    my_ul = form.prev();
                }
                my_ul.append("<li>" + data.message + "</li>");
            },
            "Ajout du parent en cours...",
            "Ajout effectué avec succès !");
}

$(document).ready(function() {
    $("#submit_main_form").click(submitMainForm);
    $(".notification_form_submit").click(submitNotificationForm);
    $("#btn_add_parent").click(addParent);
    personAutoComplete("#input_add_parent",
                       -1,
                       function(event, ui) {
                           $("#input_add_parent").val(ui.item.email);
                           addParent(event);
                           return false;
                       },
                       "#mobile_res_search_mon_compte");
});