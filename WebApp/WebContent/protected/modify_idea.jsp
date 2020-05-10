<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<t:template_head_includes>
    <link rel="stylesheet" type="text/css" href="resources/css/lib/thickbox.css" />
    <script src="resources/js/lib/thickbox.js" type="text/javascript"></script>
    <c:choose>
        <c:when test="${is_mobile}">
        </c:when>
        <c:otherwise>
            <script src="resources/js/browser/pictures.js" type="text/javascript"></script>
        </c:otherwise>
    </c:choose>
</t:template_head_includes>
<t:template_body_protected>
    <jsp:body>
        <h3>Modification d'idée</h3>
        <c:if test="${fn:length(errors) > 0}">
            <div class="alert alert-danger">
                <p>Des erreurs ont empêché la modification de cette idée:</p>
                <ul>
                    <c:forEach var="error" items="${errors}">
                        <li>${error}</li>
                    </c:forEach>
                </ul>
            </div>
        </c:if>
        <div>
            <c:if test="${not empty idea}">
                <div class="alert alert-info">
                    Modifications finies ? Aller voir ce que ça donne sur <a href="protected/voir_liste?id=${connected_user.id}">ma liste</a>...
                </div>
                <div id="idea_creation_result" class="container">
                    <div id="ideaResPlaceholder">
                        <div></div>
                        <script>
                            refreshIdea($("#ideaResPlaceholder").children(":first"), ${idea.id});
                        </script>
                    </div>
                </div>
                <div class="container border border-info bg-light rounded mb-2 p-3">
                    <form class="mw-50" action="protected/modifier_idee?id=${idea.id}&${_csrf.parameterName}=${_csrf.token}" method="post" enctype="multipart/form-data">
                        <input type="hidden" name="from" value="${from}" />
                        <div class="form-group">
                            <label for="text" class="d-none d-md-inline-block">Le texte de l'idée</label>
                            <textarea id="text" class="form-control" name="text" cols="70" rows="6">${idea.getText()}</textarea>
                        </div>
                        <div class="form-group">
                            <label for="type" class="d-none d-md-inline-block">Type</label>
                            <select id="type" class="form-control" name="type">
                                <option value="">Sélectionnez un type</option>
                                <c:forEach var="type" items="${types}">
                                    <c:choose>
                                        <c:when test="${type.name == idea.category.name}">
                                            <option value="${type.name}" selected="selected">${type.alt}</option>
                                        </c:when>
                                        <c:otherwise>
                                            <option value="${type.name}">${type.alt}</option>
                                        </c:otherwise>
                                    </c:choose>
                                </c:forEach>
                                <option value="">Autre</option>
                            </select>
                        </div>
                        <div class="form-group">
                            <label for="priority" class="d-none d-md-inline-block">Priorité</label>
                            <select id="priority" class="form-control" name="priority">
                                <option value="1">Sélectionnez une priorité</option>
                                <c:forEach var="priorite" items="${priorites}">
                                    <c:choose>
                                        <c:when test="${priorite.id == idea.priorite.id}">
                                            <option value="${priorite.id}" selected="selected">${priorite.name}</option>
                                        </c:when>
                                        <c:otherwise>
                                            <option value="${priorite.id}">${priorite.name}</option>
                                        </c:otherwise>
                                    </c:choose>
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

                        <c:if test="${not empty idea.image}">
                        <div class="form-group">
                            <span>Image actuelle</span>
                            <img class="form_img" src="${ideas_pictures}/${idea.imageSrcSmall}" width="150" />
                            <input type="hidden" id="old_picture" name="old_picture" value="${idea.image}" />
                        </div>
                        </c:if>

                        <div class="center">
                            <button type="submit" class="btn btn-primary post_idea" name="submit" id="submit">Modifier</button>
                        </div>
                    </form>
                </div>
            </c:if>
            <c:if test="${empty idea}">
                L'idée que vous souhaitez modifier n'existe pas, ou vous n'avez pas les droits pour modifier celle-ci.
            </c:if>
        </div>
    </jsp:body>
</t:template_body_protected>
</html>