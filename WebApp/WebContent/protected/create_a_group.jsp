<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<t:normal_protected>
    <jsp:body>
        <div id="creationGroupForm">
            <h3>Création d'un groupe</h3>
            <div class="form-group">
                <label for="total">Montant minimum (environ) pour ce cadeau, en incluant ta participation</label>
                <input class="form-control" id="total" name="total" type="text" value="${total}" required />
            </div>
            <div class="form-group">
                <label for="amount">Ta participation</label>
                <input class="form-control" id="amount" name="amount" type="text" value="${amount}" required />
            </div>
            <input type="hidden" name="idee" value="${idee.id}">
            <div class="center">
                <button class="btn btn-primary" type="submit" name="submit" id="submitGroupCreation">Créer le groupe!</button>
            </div>
        </div>
        <div id="groupDetail">
        </div>
        <script src="resources/js/group.js" type="text/javascript"></script>
        <script>
            function createGroup() {
                var ideaId = getURLParameter($(location).attr('href'), "idee");
                var total = $("#total").val();
                var amount = $("#amount").val();
                servicePost('protected/service/group/create',
                            {
                                idee   : ideaId,
                                total  : total,
                                amount : amount
                            },
                            function(data) {
                                $("#creationGroupForm").empty();
                                refreshGroupWithId(data.message, true); // displaying the suggest link
                                var idea = $(".idea_square");
                                refreshIdea(idea, idea.attr("id").substring(5));
                            },
                            "Création du groupe en cours...",
                            "Le groupe a bien été créé.");
            }
            $("#submitGroupCreation").click(createGroup);
        </script>
        <h3 class="mt-3">Rappel de l'idée</h3>
        <div class="container">
            <div id="idea_placeholder"></div>
            <script>
                refreshIdea($("#idea_placeholder"), ${idee.id});
            </script>
        </div>
    </jsp:body>
</t:normal_protected>