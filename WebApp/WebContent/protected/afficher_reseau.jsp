<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<t:template_head_includes>
</t:template_head_includes>
<t:template_body_protected>
    <jsp:body>
        <span id="userId" class="d-none">${id}</span>
        <c:if test="${not empty accepted}">
            <h3>Succès</h3>
            <div class="alert alert-success">
                Les demandes suivantes ont été acceptées avec succès.
                <ul>
                    <c:forEach var="accept" items="${accepted}">
                        <li>
                            ${accept.name} :
                            <a href="protected/suggerer_relations.jsp?id=${accept.id}">Suggérer</a> des relations
                        </li>
                    </c:forEach>
                </ul>
            </div>
        </c:if>
        <div id="res_demandes"></div>
        <div id="new_friend_suggestions"></div>
        <h3 class="pb-1">Rechercher des personnes dans le réseau ${name}</h3>
        <form id="form_rechercher_dans_reseau" method="GET" action="protected/service/rechercher_reseau">
            <div class="form-row justify-content-start align-items-center mx-0">
                <div class="col-auto d-none d-md-block pl-0 pr-3">
                    <label for="name">Nom / Email de la personne</label>
                </div>
                <div class="col-12 col-sm-9 col-md-5 col-lg-5 col-xl-4 px-0">
                    <input type="text" class="form-control" name="looking_for" id="looking_for" value="${looking_for}" placeholder="Entrer trois catactères pour filtrer les amis ${name}" />
                </div>
                <div class="col-auto px-0">
                    <button class="btn btn-primary d-none d-sm-block ml-2" type="submit">Rechercher !</button>
                </div>
            </div>
            <input type="hidden" id="look_in_network" name="id" value="${id}" />
            <input type="hidden" name="page" value="1" />
        </form>
        <div id="mobile_res_search_afficher_reseau" class="mobile_res_search"></div>
        <h3 class="pt-4">Réseau ${name}</h3>
        <div id="res">
        </div>
        <script src="resources/js/afficher_reseau.js" type="text/javascript"></script>
    </jsp:body>
</t:template_body_protected>
</html>
