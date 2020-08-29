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
        <h2>Détail de la réservation partielle - <a href="protected/voir_liste?id=${idee.owner.id}">Liste ${idee.owner.myDName}</a></h2>
        <div>
            <h3>Le text de l'idée</h3>
            <div class="container">
                <div id="idea_placeholder"></div>
                <script>
                    refreshIdea($("#idea_placeholder"), ${idee.id});
                </script>
            </div>
        </div>
        <div>
            <h3>Les sous réservations existantes</h3>
            <div class="container">
                <c:forEach items="${sous_reservation_existantes}" var="resa">
                    <div class="row align-items-center justify-content-start mx-0 my-3">
                        <div class="col-auto">
                            <span class="badge badge-info">
                                <c:choose>
                                    <c:when test="${resa.user.id == connected_user.id}">Vous</c:when>
                                    <c:otherwise>${resa.user.name}</c:otherwise>
                                </c:choose>
                            </span>
                        </div>
                        <c:if test="${resa.user.id == connected_user.id}">
                            <div class="col-auto">
                                <form class="form-inline" action="protected/annuler_sous_reservation" method="post" >
                                    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
                                    <input type="hidden" name="idee" value="${idee.id}">
                                    <button class="btn btn-primary" type="submit" name="submit" id="submit">Annuler !</button>
                                </form>
                            </div>
                        </c:if>
                        <div class="col-9 col-sm">
                            ${resa.comment} - <small>${resa.bookedOn}</small>
                        </div>
                    </div>
                </c:forEach>
            </div>
        </div>
        <c:if test="${not fait_parti_sous_reservation}">
            <div>
                <h3 class="mt-2">Ajouter la vôtre !</h3>
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
            </div>
        </c:if>
    </jsp:body>
</t:template_body_protected>
</html>