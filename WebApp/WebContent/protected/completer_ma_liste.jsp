<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<t:template_head_includes>
    <c:if test="${not is_mobile}">
        <script src="resources/js/browser/pictures.js" type="text/javascript"></script>
    </c:if>
</t:template_head_includes>
<t:template_body_protected>
    <jsp:body>
        <h3>Ajouter une nouvelle idée</h3>
        <div class="alert alert-primary">
            Avant d'ajouter une idée, je voudrais consulter <a href="protected/voir_liste?id=${connected_user.id}">ma liste</a>.
            <div>
                Les idées sont affichées en utilisant le format <a href="https://commonmark.org/">markdown</a> (légèrement étendu).
                <ul class="mb-0">
                    <li>Allez voir le <a href="https://commonmark.org/help/tutorial/">tutoriel complet</a></li>
                    <li>Ou la liste des <a href="https://commonmark.org/help/">fonctionnalités de base</a></li>
                </ul>
            </div>
        </div>
        <div id="idea_creation_result" class="container">
        </div>
        <div class="container border border-info bg-light rounded mb-2 p-3">
            <form class="mw-50" action="protected/ajouter_idee?id=${connected_user.id}" method="post" enctype="multipart/form-data">
                <div class="form-group">
                    <label for="text" class="d-none d-md-inline-block">Le texte de l'idée</label>
                    <textarea id="text" class="form-control" name="text" cols="70" rows="6"></textarea>
                </div>
                <div class="form-group">
                    <label for="type" class="d-none d-md-inline-block">Type</label>
                    <select id="type" class="form-control" name="type">
                        <option value="">Sélectionnez un type</option>
                        <c:forEach var="type" items="${types}">
                            <option value="${type.name}">${type.alt}</option>
                        </c:forEach>
                        <option value="">Autre</option>
                    </select>
                </div>
                <div class="form-group">
                    <label for="priority" class="d-none d-md-inline-block">Priorité</label>
                    <select id="priority" class="form-control" name="priority">
                        <option value="1">Sélectionnez une priorité</option>
                        <c:forEach var="priorite" items="${priorites}">
                            <option value="${priorite.id}">${priorite.name}</option>
                        </c:forEach>
                    </select>
                </div>
                <div class="form-group">
                    <input id="imageFile" name="image" type="file" accept="image/jpg, image/jpeg, image/png" />
                    <div>
                        <label for="imageFile" class="btn btn-secondary">Ajouter une image</label>
                    </div>
                    <span class="d-none d-md-inline-block">Fichier Choisi: </span>
                    <span id="newImage" class="picture_not_drag input"></span>
                    <div class="center">
                        <img id="imageFilePreview" alt="" src="" width="300" />
                    </div>
                </div>
                <div class="center">
                    <button type="submit" class="btn btn-primary post_idea" name="submit" id="submit">Ajouter</button>
                </div>
            </form>
        </div>
        <c:if test="${fn:length(errors) > 0}">
            <div class="alert alert-danger">
                <p>Des erreurs ont empêché la création de cette nouvelle idée:</p>
                <ul>
                    <c:forEach var="error" items="${errors}">
                        <li>${error}</li>
                    </c:forEach>
                </ul>
            </div>
        </c:if>
    </jsp:body>
</t:template_body_protected>
</html>