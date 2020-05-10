<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<t:normal_protected>
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
        <c:if test="${fn:length(errors) > 0}">
            <div class="alert alert-danger">
                <p>Des erreurs ont empêché la réservation d'une partie de cette idée:</p>
                <ul>
                    <c:forEach var="error" items="${errors}">
                        <li>${error}</li>
                    </c:forEach>
                </ul>
            </div>
        </c:if>
        <c:if test="${not fait_parti_sous_reservation}">
            <div>
                <h3 class="mt-2">Ajouter la vôtre !</h3>
                <c:if test="${not empty idee}">
                    <div class="container">
                        <form action="protected/sous_reserver" method="post" >
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
                </c:if>
                <c:if test="${empty idee}">
                    L'idée que vous souhaitez réserver n'existe pas, ou vous n'avez pas les droits pour le faire.
                </c:if>
            </div>
        </c:if>
    </jsp:body>
</t:normal_protected>