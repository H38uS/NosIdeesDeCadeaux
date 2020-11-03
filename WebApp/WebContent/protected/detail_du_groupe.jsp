<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<t:normal_protected>
    <jsp:body>
        <h2>L'idée ${idee.owner.myDName} - <a href="protected/voir_liste?id=${idee.owner.id}">Sa liste</a></h2>
        <div class="container">
            <div id="idea_placeholder"></div>
            <script>
                refreshIdea($("#idea_placeholder"), ${idee.id});
            </script>
        </div>
        <h2>Détail du groupe</h2>
        <div>
            <c:if test="${not empty group}">
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
            </c:if>
            <c:if test="${empty group}">
                Le groupe n'existe pas ou vous ne pouvez pas intéragir avec.
            </c:if>
        </div>
        <h2>Vos actions</h2>
        <div id="myActions">
        <c:choose>
            <c:when test="${not is_in_group}">
                <div>Vous ne participez pas encore à ce groupe.</div>
                <form method="POST" class="form-inline" action="protected/detail_du_groupe">
                    <input type="hidden" name="groupid" value="${group.id}" />
                    <label class="d-none d-sm-inline-block">S'inscrire: </label>
                    <input class="form-control mx-2" name="amount" type="text" value="" />
                    <button class="btn btn-primary mt-2 mt-sm-0 mx-auto mx-sm-0" type="submit" name="submit" id="submit">Participer</button>
                </form>
            </c:when>
            <c:otherwise>
                <c:forEach var="share" items="${group.shares}">
                    <c:if test="${connected_user.id == share.user.id}">
                        <form class="form-inline d-inline" method="POST" action="protected/detail_du_groupe">
                            <label for="amount" class="d-none d-lg-inline-block">Modifier le montant :</label>
                            <input id="amount" class="form-control mt-2 mt-md-0" name="amount" type="text" value="${share.shareAmount}" />
                            <input type="hidden" name="groupid" value="${group.id}" />
                            <button class="btn btn-primary mt-2 mt-md-0" type="submit" name="submit" id="submit">Modifier !</button>
                        </form>
                        <div class="form-inline d-inline-block">
                            <input type="hidden" name="groupid" value="${group.id}" />
                            <button class="btn btn-primary mt-2 mt-md-0" type="submit" name="submit" id="submitAnnulation">Annuler ma participation</button>
                            <script>
                                $("#submitAnnulation").click(function() {
                                    var groupId = $(this).prev().val();
                                    servicePost('protected/service/group/annulation',
                                                { groupid : groupId },
                                                function(data) {
                                                    $("#myActions").html(`
                                                        <div>Vous ne participez pas encore à ce groupe.</div>
                                                        <form method="POST" class="form-inline" action="protected/detail_du_groupe">
                                                            <input type="hidden" name="groupid" value="${groupId}" />
                                                            <label class="d-none d-sm-inline-block">S'inscrire: </label>
                                                            <input class="form-control mx-2" name="amount" type="text" value="" />
                                                            <button class="btn btn-primary mt-2 mt-sm-0 mx-auto mx-sm-0" type="submit" name="submit" id="submit">Participer</button>
                                                        </form>
                                                    `);
                                                },
                                                "Annulation de votre participation en cours...",
                                                "Votre participation a bien été annulée.");
                                });
                            </script>
                        </div>
                    </c:if>
                </c:forEach>
            </c:otherwise>
        </c:choose>
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
        <c:if test="${group.total > currentTotal}">
            <h3 class="mt-3">Suggérer ce groupe à quelqu'un</h3>
            Il manque un peu (${remaining}€ très exactement)... N'hésitez plus, <a href="protected/suggerer_groupe_idee?groupid=${group.id}">suggérer</a> ce groupe à d'autres personnes !
        </c:if>
    </jsp:body>
</t:normal_protected>