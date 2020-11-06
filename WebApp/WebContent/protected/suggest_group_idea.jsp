<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<t:normal_protected>
    <jsp:body>
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
        <h2>Rappel de l'idée</h2>
        <div class="container">
            <div id="idea_placeholder"></div>
            <script>
                refreshIdea($("#idea_placeholder"), ${idee.id});
            </script>
        </div>
        <div id="groupDetail">
        </div>
        <script src="resources/js/group.js" type="text/javascript"></script>
        <script>
            refreshGroup(false); // hiding the suggest link
        </script>
    </jsp:body>
</t:normal_protected>