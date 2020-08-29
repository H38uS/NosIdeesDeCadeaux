<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<t:template_head_includes>
    <script src="resources/js/sous_reservation.js" type="text/javascript"></script>
</t:template_head_includes>
<t:template_body_protected>
    <jsp:body>
        <h2>Réservation d'une partie de l'idée</h2>
        <div class="container">
            <form action="protected/sous_reserver" method="post" id="sousReserverForm">
                <input type="hidden" name="idee" value="${idee.id}">
                <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
                <div class="form-group">
                    <label for="comment" class="d-none d-md-inline-block">Commentaire de la réservation</label>
                    <textarea id="comment" class="form-control" name="comment" required="required" cols="50" rows="5" placeholder="Je prends le tome 42 de la série..." >${comment}</textarea>
                </div>
                <div class="center">
                    <button class="btn btn-primary" type="submit" name="submit" id="submit">Réserver !</button>
                </div>
            </form>
        </div>
        <h2>Rappel de l'idée</h2>
        <div class="container">
            <div id="idea_placeholder"></div>
            <script>
                refreshIdea($("#idea_placeholder"), ${idee.id});
            </script>
        </div>
    </jsp:body>
</t:template_body_protected>
</html>