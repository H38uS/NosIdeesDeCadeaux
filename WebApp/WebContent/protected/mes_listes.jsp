<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<t:normal_protected>
    <jsp:body>
        <div id="resultPlaceholderForShortcuts">
        </div>
        <div></div>
        <div class="row justify-content-around">
            <div class="col-12">

                <div class="container">

                    <c:if test="${not is_mobile}">
                        <c:if test="${fn:length(entities) gt 1}">
                            <div class="alert alert-warning">
                                <div class="row align-items-center pb-2">
                                    <div class="col-auto">
                                        Vous ne trouvez pas votre bonheur ?<br/> Recherchez une liste particulière :
                                    </div>
                                    <form id="afficherliste_topmeslistes" class="form-inline" method="GET"
                                          action="protected/afficher_listes">
                                        <input type="text" class="form-control" name="name" id="top_mes_listes_search"
                                               placeholder="Entrez un nom ou un email"/>
                                        <button class="btn btn-primary mx-2" type="submit">Rechercher !</button>
                                    </form>
                                </div>
                            </div>
                            <script type="text/javascript">
                                $(document).ready(personAutoComplete("#top_mes_listes_search",
                                                                     -1,
                                                                     function(event, ui) {
                                                                         $("#top_mes_listes_search").val(ui.item.email);
                                                                         $("#afficherliste_topmeslistes").submit();
                                                                         return false;
                                                                     },
                                                                     "#mobile_res_search")); // osef: uniquement en non mobile
                            </script>
                        </c:if>
                    </c:if>

                    <div id="resultPlaceholderForLists">
                    </div>
                    <script src="resources/js/afficher_liste_idees.js" type="text/javascript"></script>
                    <script>
                        displayUsersIdeasList("${identic_call_back}", "${call_back}");
                    </script>

                    <c:if test="${not is_mobile}">
                        <div class="alert alert-warning">
                            <div class="row align-items-center">
                                <c:choose>
                                    <c:when test="${fn:length(entities) eq 1}">
                                        <div class="col-auto">
                                            Consultez une autre liste :
                                        </div>
                                        <form id="afficherliste_bottommeslistes" class="form-inline" method="GET"
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
                                        <form id="afficherliste_bottommeslistes" class="form-inline" method="GET"
                                              action="protected/afficher_listes">
                                            <input type="text" class="form-control" name="name"
                                                   id="bottom_mes_listes_search" placeholder="Entrez un nom ou un email"/>
                                            <button class="btn btn-primary mx-2" type="submit">Rechercher !</button>
                                        </form>
                                    </c:otherwise>
                                </c:choose>
                            </div>
                        </div>
                        <script type="text/javascript">
                            $(document).ready(personAutoComplete("#bottom_mes_listes_search",
                                                                 -1,
                                                                 function(event, ui) {
                                                                     $("#bottom_mes_listes_search").val(ui.item.email);
                                                                     $("#afficherliste_bottommeslistes").submit();
                                                                     return false;
                                                                 },
                                                                 "#mobile_res_search",
                                                                 "right bottom",
                                                                 "right top"));
                        </script>
                    </c:if>
                </div>
            </div>
        </div>
    </jsp:body>
</t:normal_protected>