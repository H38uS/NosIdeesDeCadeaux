<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<t:template_head_includes>
	<script src="resources/js/afficher_reseau.js" type="text/javascript"></script>
</t:template_head_includes>
<t:template_body_protected>
	<jsp:body>
		<span id="userId" class="d-none">${id}</span>
		<c:choose>
			<c:when test="${is_mobile}">
				<script type="text/javascript">
					$(document).ready(function() {
						jQuery.ui.autocomplete.prototype._resizeMenu = function () {
							var ul = this.menu.element;
							ul.outerWidth(
									Math.max( $("#mobile_res_search").outerWidth(), this.element.outerWidth())
								);
						}
						$("#looking_for").autocomplete({
							source: function(request, response) {
								$.getJSON(
									"protected/service/name_resolver",
									{ term: $("#looking_for").val(), userId: $("#userId").html() },
									response
								);
							},
							minLength : 2,
							appendTo: "#mobile_res_search_afficher_reseau",
							position: { my : "left top", at: "left top", of : "#mobile_res_search_afficher_reseau" },
							select : function(event, ui) {
								$("#looking_for").val(ui.item.email);
								$("#form_rechercher_dans_reseau").submit();
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
			</c:when>
			<c:otherwise>
				<script type="text/javascript">
					$(document).ready(function() {
						$("#looking_for").autocomplete({
							source: function(request, response) {
								$.getJSON(
									"protected/service/name_resolver",
									{ term: $("#looking_for").val(), userId: $("#userId").html() },
									response
								);
							},
							minLength : 2,
							select : function(event, ui) {
								$("#looking_for").val(ui.item.email);
								$("#form_rechercher_dans_reseau").submit();
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
			</c:otherwise>
		</c:choose>

		<c:if test="${not empty accepted}">
			<h3>Succès</h3>
			<div class="alert alert-success">
				Les demandes suivantes ont été acceptées avec succès.
				<ul>
					<c:forEach var="accept" items="${accepted}">
						<li>
							${accept.name} :
							<a href="protected/suggerer_relations?id=${accept.id}">Suggérer</a> des relations
						</li>
					</c:forEach>
				</ul>
			</div>
		</c:if>
		<c:if test="${not empty demandes}">
			<div class="container border border-info bg-light rounded p-3 mb-3">
				<h3>Demandes reçues</h3>
				<form method="POST" action="protected/resoudre_demande_ami">
					<c:forEach var="demande" items="${demandes}">
						<div class="row align-items-center">
							<div class="col-6 word-break-all">
								<span>${demande.sent_by.name} (${demande.sent_by.email})</span>
							</div>
							<div class="col-3">
								<input type="radio" id="acc_choix_${demande.sent_by.id}" name="choix_${demande.sent_by.id}" value="Accepter">
								<label for="acc_choix_${demande.sent_by.id}">Accepter</label>
							</div>
							<div class="col-3">
								<input type="radio" id="ref_choix_${demande.sent_by.id}" name="choix_${demande.sent_by.id}" value="Refuser">
								<label for="ref_choix_${demande.sent_by.id}">Refuser</label>
							</div>
						</div>
					</c:forEach>
					<div class="center">
						<button class="btn btn-primary" type="submit" id="submit" name="submit">Sauvegarder</button>
					</div>
					<input type="hidden" name="id" value="${id}" />
					<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
				</form>
			</div>
		</c:if>
		<c:if test="${not empty suggestions && suggestions}">
			Vos amis vous suggèrent de nouvelles relations ! <a href="protected/suggestion_amis">Aller voir</a>...
		</c:if>
		<h3 class="pb-1">Rechercher des personnes dans le réseau ${name}</h3>
			<form id="form_rechercher_dans_reseau" class="form-inline" method="POST" action="protected/rechercher_reseau">
				<div class="row align-items-center">
					<div class="col-auto d-none d-md-inline-block">
						<label for="name">Nom / Email de la personne</label>
					</div>
					<div class="col-7 col-sm-auto">
						<input type="text" class="form-control" name="looking_for" id="looking_for" value="${looking_for}" />
					</div>
					<div class="col-auto px-0">
						<button class="btn btn-primary" type="submit">Rechercher !</button>
					</div>
				</div>
				<input type="hidden" name="id" value="${id}" />
				<input type="hidden" name="page" value="1" />
				<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
			</form>
		<div id="mobile_res_search_afficher_reseau" class="mobile_res_search"></div>
		<h3 class="pt-4">Réseau ${name}</h3>
		<c:if test="${not empty pages}">
			<div>
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
									<a class="page-link" href="${call_back}?page=${page.numero}${spec_parameters}">${page.numero}</a>
								</li>
							</c:when>
							<c:otherwise>
								<li class="page-item active">
									<a class="page-link" href="${call_back}?page=${page.numero}${spec_parameters}">${page.numero}</a>
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
		<c:choose>
			<c:when test="${empty entities}">
				Aucune relation trouvée. <a href="protected/rechercher_personne.jsp" >Rechercher</a> des personnes à ajouter !
			</c:when>
			<c:otherwise>
				<div class="row align-items-start mx-0 justify-content-around">
					<c:forEach var="relation" items="${entities}">
						<div class="card col-auto px-0 m-2 person_card">
							<div class="row align-items-center mx-auto person_card_pic">
								<img src="${avatars}/${relation.second.avatarSrcLarge}">
							</div>
							<div class="card-body">
								<h5 class="card-title">
									<a href="protected/voir_liste?id=${relation.second.id}">${relation.second.name}</a>
								</h5>
							</div>
							<div class="card-footer">
								<c:choose>
									<c:when test="${relation.second.id != connected_user.id && relation.secondIsInMyNetwork}">
										Aller voir <a href="protected/voir_liste?id=${relation.second.id}">sa liste</a>.<br/>
										Aller voir <a href="protected/afficher_reseau?id=${relation.second.id}">ses amis</a>.<br/>
										<a href="protected/suggerer_relations?id=${relation.second.id}">Suggérer</a> des relations.<br/>
										Lui <a href="protected/ajouter_idee_ami?id=${relation.second.id}">ajouter</a> une idée.<br/>
										<a class="drop_relationship" href="protected/supprimer_relation?id=${relation.second.id}">Supprimer</a> cette personne.
									</c:when>
									<c:when test="${relation.second.id == connected_user.id}">
										Vous ne pouvez pas intéragir avec vous-même !
									</c:when>
									<c:otherwise>
										Vous n'êtes pas encore ami avec cette personne.<br/>
										<c:choose>
											<c:when test="${not empty relation.second.freeComment}">
												${relation.second.freeComment}
											</c:when>
											<c:otherwise>
												<form method="POST" action="protected/demande_rejoindre_reseau">
													<input type="hidden" name="user_id" value="${relation.second.id}" >
													<input type="submit" name="submit" id="submit" value="Envoyer une demande" />
													<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
												</form>
											</c:otherwise>
										</c:choose>
									</c:otherwise>
								</c:choose>
							</div>
						</div>
					</c:forEach>
				</div>
			</c:otherwise>
		</c:choose>
		<c:if test="${not empty pages}">
			<div class="mt-3">
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
									<a class="page-link" href="${call_back}?page=${page.numero}${spec_parameters}">${page.numero}</a>
								</li>
							</c:when>
							<c:otherwise>
								<li class="page-item active">
									<a class="page-link" href="${call_back}?page=${page.numero}${spec_parameters}">${page.numero}</a>
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
	</jsp:body>
</t:template_body_protected>
</html>
