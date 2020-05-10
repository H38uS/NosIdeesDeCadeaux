<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<t:normal_protected>
    <jsp:body>
        <c:if test="${not empty group}">
            <h2>Partager ce <a href="protected/detail_du_groupe?groupid=${group.id}">groupe</a></h2>
            <c:if test="${not empty sent_to_users}">
                <div class="alert alert-success">
                    La requête a bien été envoyé aux utilisateurs suivants :
                    <ul>
                        <c:forEach var="user" items="${sent_to_users}">
                            <li>${user.name}</li>
                        </c:forEach>
                    </ul>
                </div>
            </c:if>
            <c:if test="${not empty candidates}">
                <div class="container border border-info bg-light rounded my-3 p-2">
                    <h4>Partagez ce groupe avec</h4>
                    <form method="POST" class="form-inline" action="protected/suggerer_groupe_idee">
                        <input type="hidden" name="groupid" value="${group.id}" />
                        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
                        <c:forEach var="user" items="${candidates}">
                            <div class="col-12 col-sm-5 col-lg-3 mx-2">
                                <input type="checkbox"  class="form-check-input" id="cb${user.id}" name="${user.id}" />
                                <label class="d-inline-block" for="cb${user.id}">${user.name}</label>
                            </div>
                        </c:forEach>
                        <div class="center w-100">
                            <button class="btn btn-primary" type="submit" name="submit" id="submit">Suggérer !</button>
                        </div>
                        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
                        <input type="hidden" name="userId" value="${user.id}" />
                    </form>
                </div>
            </c:if>
            <c:if test="${empty candidates}">
                Vous ne connaissez personne qui serait susceptible de participer à ce cadeau...
            </c:if>
            <h3>Rappel de l'idée</h3>
            <div class="container">
                <div id="idea_placeholder"></div>
                <script>
                    refreshIdea($("#idea_placeholder"), ${idee.id});
                </script>
            </div>
            <h3>Détail de ce groupe</h3>
            <div>
                <p>Montant total souhaité : ${group.totalAmount}€</p>
                <div>
                    <c:choose>
                        <c:when test="${empty group.shares}">
                            Aucune participation pour le moment.
                        </c:when>
                        <c:otherwise>
                            <ul>
                                <c:forEach var="share" items="${group.shares}">
                                    <li>${share.user.name} : ${share.shareAmount}€ - <small>depuis le ${share.joinDate}</small></li>
                                </c:forEach>
                            </ul>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
            <c:if test="${fn:length(errors) > 0}">
                <div class="alert alert-danger">
                    <p>Des erreurs sont survenues:</p>
                    <ul>
                        <c:forEach var="error" items="${errors}">
                            <li>${error}</li>
                        </c:forEach>
                    </ul>
                </div>
            </c:if>
        </c:if>
        <c:if test="${empty group}">
            Le groupe n'existe pas ou vous ne pouvez pas intéragir avec.
        </c:if>
    </jsp:body>
</t:normal_protected>