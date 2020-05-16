<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<t:normal_protected>
    <jsp:body>

        <c:if test="${fn:length(entities) gt 1}">
            <c:choose>
                <c:when test="${is_mobile}">
                    <div id="mes_listes_list_users">
                        <c:forEach var="ownerIdeas" items="${entities}">
                            <a href="${identic_call_back}#list_${ownerIdeas.getOwner().id}">${ownerIdeas.getOwner().name}</a> |
                        </c:forEach>
                    </div>
                </c:when>
                <c:otherwise>
                    <div id="mes_listes_list_users" class="d-none d-xl-inline-block col-xl-3">
                        <c:forEach var="ownerIdeas" items="${entities}">
                            <a href="${identic_call_back}#list_${ownerIdeas.getOwner().id}" class="col-12 p-2">
                                <div class="center">
                                    <img src="protected/files/uploaded_pictures/avatars/${ownerIdeas.getOwner().avatarSrcSmall}"
                                         style='height:80px' alt=""/>
                                </div>
                                <div class="center">
                                    ${ownerIdeas.getOwner().name}
                                </div>
                            </a>
                        </c:forEach>
                    </div>
                </c:otherwise>
            </c:choose>
        </c:if>

        <div class="row justify-content-around">

            <div id="mes_listes_entities_container" class="col-12">

                <c:if test="${not is_mobile}">
                    <c:if test="${fn:length(entities) gt 1}">
                        <script type="text/javascript">
                            $(document).ready(function() {
                                $("#top_mes_listes_search").autocomplete({
                                    source : "protected/service/name_resolver",
                                    minLength : 2,
                                    select : function(event, ui) {
                                        $("#top_mes_listes_search").val(ui.item.email);
                                        $("#afficherliste_topmeslistes").submit();
                                        return false;
                                    }
                                }).data( "ui-autocomplete" )._renderItem = function( ul, item ) {
                                    return $( "<li class=\"ui-menu-item\"></li>" )
                                    .data( "item.autocomplete", item )
                                    .append( '<div class="ui-menu-item-wrapper"> <div class="row align-items-center"><div class="col-4 col-sm-3 col-md-2 center"><img class="avatar" src="' + item.imgsrc + '"/></div><div class="col-8 col-md-9">' + item.value + '</div></div></div>')
                                    .appendTo( ul );
                                };
                            });
                        </script>
                        <div class="alert alert-warning">
                            <div class="row align-items-center pb-2">
                                <div class="col-auto">
                                    Vous ne trouvez pas votre bonheur ?<br/> Recherchez une liste particulière :
                                </div>
                                <form id="afficherliste_topmeslistes" class="form-inline" method="POST"
                                      action="protected/afficher_listes">
                                    <input type="text" class="form-control" name="name" id="top_mes_listes_search"
                                           placeholder="Entrez un nom ou un email"/>
                                    <button class="btn btn-primary mx-2" type="submit">Rechercher !</button>
                                </form>
                            </div>
                        </div>
                    </c:if>
                </c:if>

                <c:if test="${not empty pages}">
                    <div class="my-3">
                        <ul class="pagination justify-content-center">
                            <c:choose>
                                <c:when test="${current != 1}">
                                    <li class="page-item">
                                        <a class="page-link" href="${call_back}?page=${current-1}${spec_parameters}">Précédent</a>
                                    </li>
                                </c:when>
                                <c:otherwise>
                                    <li class="page-item disabled">
                                        <a class="page-link" href="${call_back}?page=${current-1}${spec_parameters}">Précédent</a>
                                    </li>
                                </c:otherwise>
                            </c:choose>
                            <c:forEach var="page" items="${pages}">
                                <c:choose>
                                    <c:when test="${current != page.numero}">
                                        <li class="page-item">
                                            <a class="page-link"
                                               href="${call_back}?page=${page.numero}${spec_parameters}">${page.numero}</a>
                                        </li>
                                    </c:when>
                                    <c:otherwise>
                                        <li class="page-item active">
                                            <a class="page-link"
                                               href="${call_back}?page=${page.numero}${spec_parameters}">${page.numero}</a>
                                        </li>
                                    </c:otherwise>
                                </c:choose>
                            </c:forEach>
                            <c:choose>
                                <c:when test="${current != last}">
                                    <li class="page-item">
                                        <a class="page-link" href="${call_back}?page=${current+1}${spec_parameters}">Suivant</a>
                                    </li>
                                </c:when>
                                <c:otherwise>
                                    <li class="page-item disabled">
                                        <a class="page-link" href="${call_back}?page=${current+1}${spec_parameters}">Suivant</a>
                                    </li>
                                </c:otherwise>
                            </c:choose>
                        </ul>
                    </div>
                </c:if>

                <c:if test="${not empty entities}">

                    <div class="container">
                        <div id="resultPlaceholderForLists">
                        </div>
                        <script src="resources/js/afficher_liste_idees.js" type="text/javascript"></script>
                        <script>
                            displayUsersIdeasList("${identic_call_back}", "${call_back}");
                        </script>
                    </div>

                    <c:if test="${not empty pages}">
                        <div class="my-3">
                            <ul class="pagination justify-content-center">
                                <c:choose>
                                    <c:when test="${current != 1}">
                                        <li class="page-item">
                                            <a class="page-link"
                                               href="${call_back}?page=${current-1}${spec_parameters}">Précédent</a>
                                        </li>
                                    </c:when>
                                    <c:otherwise>
                                        <li class="page-item disabled">
                                            <a class="page-link"
                                               href="${call_back}?page=${current-1}${spec_parameters}">Précédent</a>
                                        </li>
                                    </c:otherwise>
                                </c:choose>
                                <c:forEach var="page" items="${pages}">
                                    <c:choose>
                                        <c:when test="${current != page.numero}">
                                            <li class="page-item">
                                                <a class="page-link"
                                                   href="${call_back}?page=${page.numero}${spec_parameters}">${page.numero}</a>
                                            </li>
                                        </c:when>
                                        <c:otherwise>
                                            <li class="page-item active">
                                                <a class="page-link"
                                                   href="${call_back}?page=${page.numero}${spec_parameters}">${page.numero}</a>
                                            </li>
                                        </c:otherwise>
                                    </c:choose>
                                </c:forEach>
                                <c:choose>
                                    <c:when test="${current != last}">
                                        <li class="page-item">
                                            <a class="page-link"
                                               href="${call_back}?page=${current+1}${spec_parameters}">Suivant</a>
                                        </li>
                                    </c:when>
                                    <c:otherwise>
                                        <li class="page-item disabled">
                                            <a class="page-link"
                                               href="${call_back}?page=${current+1}${spec_parameters}">Suivant</a>
                                        </li>
                                    </c:otherwise>
                                </c:choose>
                            </ul>
                        </div>
                    </c:if>
                </c:if>

                <c:if test="${empty entities}">
                    <div>
                        <p class="alert alert-danger">Aucune liste trouvée...</p>
                        <p class="alert alert-info">
                            Vous pouvez entrer un nouveau nom ci-dessous, ou cliquer sur <a href="protected/afficher_reseau?id=${connected_user.id}">ce
                            lien</a>
                            pour afficher tous vos amis.
                        </p>
                    </div>
                </c:if>
                <c:if test="${not is_mobile}">
                    <script type="text/javascript">
                        $(document).ready(function() {
                            $("#bottom_mes_listes_search").autocomplete({
                                source : "protected/service/name_resolver",
                                minLength : 2,
                                position: { my : "left bottom", at: "left top", of : "#bottom_mes_listes_search" },
                                select : function(event, ui) {
                                    $("#bottom_mes_listes_search").val(ui.item.email);
                                    $("#afficherliste_bottommeslistes").submit();
                                    return false;
                                }
                            }).data( "ui-autocomplete" )._renderItem = function( ul, item ) {
                                return $( "<li class=\"ui-menu-item\"></li>" )
                                .data( "item.autocomplete", item )
                                .append( '<div class="ui-menu-item-wrapper"> <div class="row align-items-center"><div class="col-4 col-sm-3 col-md-2 center"><img class="avatar" src="' + item.imgsrc + '"/></div><div class="col-8 col-md-9">' + item.value + '</div></div></div>')
                                .appendTo( ul );
                            };
                        });
                    </script>
                    <div class="alert alert-warning">
                        <div class="row align-items-center">
                            <c:choose>
                                <c:when test="${fn:length(entities) eq 1}">
                                    <div class="col-auto">
                                        Consultez une autre liste :
                                    </div>
                                    <form id="afficherliste_bottommeslistes" class="form-inline" method="POST"
                                          action="protected/afficher_listes">
                                        <input type="text" class="form-control" name="name"
                                               id="bottom_mes_listes_search" placeholder="Entrez un nom ou un email"/>
                                        <button class="btn btn-primary mx-2" type="submit">Rechercher !</button>
                                    </form>
                                </c:when>
                                <c:otherwise>
                                    <div class="col-auto">
                                        Vous ne trouvez pas votre bonheur ?<br/> Recherchez une liste particulière :
                                    </div>
                                    <form id="afficherliste_bottommeslistes" class="form-inline" method="POST"
                                          action="protected/afficher_listes">
                                        <input type="text" class="form-control" name="name"
                                               id="bottom_mes_listes_search" placeholder="Entrez un nom ou un email"/>
                                        <button class="btn btn-primary mx-2" type="submit">Rechercher !</button>
                                    </form>
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </div>
                </c:if>
            </div>
        </div>
    </jsp:body>
</t:normal_protected>