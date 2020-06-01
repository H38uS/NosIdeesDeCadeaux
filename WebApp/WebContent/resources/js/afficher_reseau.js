function dropRelationship(e) {

    e.preventDefault();

    if (!confirm("Etes-vous sûr de supprimer cette relation ?")) {
        return;
    }

    var userId = getURLParameter($(this).attr("href"), 'id');
    var card = $(this).closest(".card");
    servicePost('protected/service/supprimer_relation',
                { id : userId },
                function(data) {
                    card.fadeOut('slow');
                },
                'Suppression de la relation en cours...',
                'La relation a bien été supprimée.');
}

$(document).ready(function() {
    $(".drop_relationship").click(dropRelationship);

    // auto complete
    personAutoComplete("#looking_for",
                       $("#userId").html(),
                       function(event, ui) {
                           $("#looking_for").val(ui.item.email);
                           $("#form_rechercher_dans_reseau").submit();
                           return false;
                       },
                       "#mobile_res_search_afficher_reseau");
});