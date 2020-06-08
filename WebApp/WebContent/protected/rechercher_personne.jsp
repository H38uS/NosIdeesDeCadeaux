<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<t:template_head_includes>
    <script src="resources/js/search.js" type="text/javascript"></script>
</t:template_head_includes>
<t:template_body_protected>
    <jsp:body>
        <h2>Rechercher une personne pour l'ajouter</h2>
        <form id="rechercherPersonForm" class="mb-3" method="POST" action="protected/rechercher_personne">
            <div class="form-group">
                <label for="name" class="required">Nom / Email de la personne</label>
                <input type="text" class="form-control" name="name" id="name" value="${name}" placeholder="Taper au moins trois caractères pour commencer une recherche..." />
            </div>
            <div class="form-check">
                <c:if test="${onlyNonFriend}">
                    <input type="checkbox" class="form-check-input" name="only_non_friend" id="only_non_friend" checked="checked" />
                </c:if>
                <c:if test="${not onlyNonFriend}">
                    <input type="checkbox" class="form-check-input" name="only_non_friend" id="only_non_friend" />
                </c:if>
                <label id="label_only_non_friend" for="only_non_friend">Afficher uniquement les non-amis</label>
            </div>
            <div class="center">
                <button class="btn btn-primary" type="submit" name="submit" id="submit">Rechercher !</button>
            </div>
            <input type="hidden" name="page" value="1" />
        </form>
        <div id="res">
        </div>
    </jsp:body>
</t:template_body_protected>