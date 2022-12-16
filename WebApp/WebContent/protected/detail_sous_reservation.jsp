<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<t:normal_protected>
    <jsp:body>
        <div id="titleDiv"></div>
        <div>
            <h3>Le text de l'idée</h3>
            <div class="container">
                <div id="idea_placeholder"></div>
                <script>
                    var ideaId = getURLParameter($(location).attr('href'), "idee");
                    refreshIdea($("#idea_placeholder"), ideaId);
                </script>
            </div>
        </div>
        <div>
            <h3>Les sous réservations existantes</h3>
            <div class="container" id="res_sous_reservation"></div>
        </div>
        <div>
            <h3 class="mt-2">Ajouter une sous-réservation</h3>
            <div class="container">
                <div>
                    <div class="form-group">
                        <label for="comment" class="d-none d-md-inline-block">Commentaire de la réservation</label>
                        <textarea id="comment" class="form-control" name="comment" required="required" cols="50" rows="5" placeholder="Je prends le tome 42 de la série..." ></textarea>
                    </div>
                    <div class="center">
                        <button class="btn btn-primary" type="submit" name="submit" id="submit">Réserver !</button>
                    </div>
                </div>
            </div>
        </div>
        <script src="resources/js/sous_reservation.js" type="text/javascript"></script>
    </jsp:body>
</t:normal_protected>
</html>