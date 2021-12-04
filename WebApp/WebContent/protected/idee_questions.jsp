<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<t:normal_protected>
    <jsp:body>
        <div class="alert alert-primary">
            <div>
                Cette page permet d'échanger des questions entre le propriétaire de l'idée et les intéressés. Donc:
                <ul>
                    <li>Le propriétaire de l'idée verra tout ce qui est écris ici...</li>
                    <li>... Mais les commentaires seront anonymes.</li>
                </ul>
            </div>
            <div>
                Les idées sont affichées en utilisant le format <a href="https://commonmark.org/">markdown</a> (légèrement étendu).
                <ul class="mb-0">
                    <li>Allez voir le <a href="https://commonmark.org/help/tutorial/">tutoriel complet</a></li>
                    <li>Ou la liste des <a href="https://commonmark.org/help/">fonctionnalités de base</a></li>
                </ul>
            </div>
        </div>
        <c:choose>
            <c:when test="${isOwner}">
                <h2>Répondez à des questions sur une de vos idées</h2>
            </c:when>
            <c:otherwise>
                <h2>Posez une question sur une idée</h2>
            </c:otherwise>
        </c:choose>
        <h3>Rappel de l'idée</h3>
        <div class="container">
            <div id="idea_placeholder"></div>
            <script>
                refreshIdea($("#idea_placeholder"), ${idee.id});
            </script>
        </div>

        <c:if test="${not empty success}">
            Votre nouveau commentaire a bien été ajouté.
        </c:if>

        <div>
            <c:choose>
                <c:when test="${isOwner}">
                    <h3>Répondez aux questions / Laissez un message pour cette idée</h3>
                </c:when>
                <c:otherwise>
                    <h3>Posez une question à ${idee.owner.name}</h3>
                </c:otherwise>
            </c:choose>
            <div class="container">
                <form action="protected/idee_questions" method="post">
                    <div class="form-group">
                        <div class="d-none d-md-inline-block">
                            <c:choose>
                                <c:when test="${isOwner}">
                                    <label>Votre réponse / commentaire</label>
                                </c:when>
                                <c:otherwise>
                                    <label>Votre question</label>
                                </c:otherwise>
                            </c:choose>
                        </div>
                        <textarea id="text" class="form-control" name="text" cols="70" rows="6" required="required" placeholder="Votre question / réponse. Visible de tous (y compris le propriétaire de l'idée)."></textarea>
                    </div>
                    <div class="center">
                        <button class="btn btn-primary" type="submit" name="submit">Ajouter !</button>
                    </div>
                    <input type="hidden" name="idee" value="${idee.id}" />
                    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
                </form>
            </div>
        </div>

        <h3 class="mt-2">Questions / réponses existantes</h3>
        <div class="container">
            <c:choose>
                <c:when test="${empty comments}">
                    Aucun commentaire sur l'idée pour le moment.
                </c:when>
                <c:otherwise>
                    <c:forEach var="comment" items="${comments}" >
                        <c:choose>
                            <c:when test="${connected_user.id == comment.writtenBy.id}">
                                <div class="comment comment_mine">
                                    <div class="comment_header_mine">Posté par vous le ${comment.time} - le <a href="protected/supprimer_question?id=${comment.id}">supprimer</a></div>
                                    <div class="comment_text">${comment.getHtml()}</div>
                                </div>
                            </c:when>
                            <c:when test="${comment.writtenBy.id == idee.owner.id}">
                                <div class="comment comment_owner">
                                    <div class="comment_header_owner">Posté par ${idee.owner.name} le ${comment.time}</div>
                                    <div class="comment_text">${comment.getHtml()}</div>
                                </div>
                            </c:when>
                            <c:otherwise>
                                <div class="comment comment_other">
                                    <div class="comment_header_other">Posté par quelqu'un le ${comment.time}</div>
                                    <div class="comment_text">${comment.getHtml()}</div>
                                </div>
                            </c:otherwise>
                        </c:choose>
                    </c:forEach>
                </c:otherwise>
            </c:choose>
        </div>
    </jsp:body>
</t:normal_protected>