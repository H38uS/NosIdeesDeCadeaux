<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<t:normal_protected>
    <jsp:body>
        <h3>Création d'un groupe</h3>
        <c:if test="${fn:length(errors) > 0}">
            <div class="alert alert-danger">
                <p>Des erreurs ont empêché la création de ce groupe:</p>
                <ul>
                    <c:forEach var="error" items="${errors}">
                        <li>${error}</li>
                    </c:forEach>
                </ul>
            </div>
        </c:if>
        <div>
            <c:if test="${not empty idee}">
                <div class="container">
                    <form action="protected/create_a_group" method="post" >
                        <div class="form-group">
                            <label for="total">Montant minimum (environ) pour ce cadeau, en incluant ta participation</label>
                            <input class="form-control" id="total" name="total" type="text" value="${total}" required />
                        </div>
                        <div class="form-group">
                            <label for="amount">Ta participation</label>
                            <input class="form-control" id="amount" name="amount" type="text" value="${amount}" required />
                        </div>
                        <input type="hidden" name="idee" value="${idee.id}">
                        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
                        <div class="center">
                            <button class="btn btn-primary" type="submit" name="submit" id="submit">Créer le groupe!</button>
                        </div>
                    </form>
                </div>
                <h3 class="mt-3">Rappel de l'idée</h3>
                <div class="container">
                    <div id="idea_placeholder"></div>
                    <script>
                        refreshIdea($("#idea_placeholder"), ${idee.id});
                    </script>
                </div>
            </c:if>
            <c:if test="${empty idee}">
                <div class="alert alert-danger">
                    L'idée sur laquelle vous souhaitez créer un groupe n'existe pas, ou vous n'avez pas les droits pour le faire.
                </div>
            </c:if>
        </div>
    </jsp:body>
</t:normal_protected>