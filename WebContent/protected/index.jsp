<%@  taglib  uri="http://java.sun.com/jsp/jstl/sql"  prefix="sql"%>
<%@  taglib  uri="http://java.sun.com/jsp/jstl/core"  prefix="c"%>

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>

<t:normal_protected>
	<jsp:body>
		<c:if test="${no_birth_date_set}">
			<p class="alert alert-warning">
				Vous n'avez pas encore entré votre date de naissance... Vous devez le faire pour apparaitre ici auprès de vos amis !<br/>
				Vous pouvez le faire à tout moment en suivant le lien <a href="protected/mon_compte">mon compte</a>.
			</p>
		</c:if>
		<h2>Prochain anniversaire</h2>
		<div class="mb-2">
			<c:choose>
				<c:when test="${not empty userBirthday}">
					<table>
						<c:forEach var="user" items="${userBirthday}" >
						<tr>
							<td>
								<c:choose>
									<c:when test="${user.nbDaysBeforeBirthday == 0}">
										C'est l'anniversaire ${user.myDName} aujourd'hui !
									</c:when>
									<c:when test="${user.nbDaysBeforeBirthday == 1}">
										L'anniversaire ${user.myDName} est demain !
									</c:when>
									<c:otherwise>
										L'anniversaire ${user.myDName} arrive dans ${user.nbDaysBeforeBirthday} jours !
									</c:otherwise>
								</c:choose>
							</td>
							<td>
								<a href="protected/voir_liste?id=${user.id}">Aller jeter un coup d'oeil à sa liste...</a>
							</td>
						</tr>
						</c:forEach>
					</table>
				</c:when>
				<c:otherwise>
					Aucun anniversaire dans peu de temps... C'est le bon moment pour récupérer des repas !
				</c:otherwise>
			</c:choose>
		</div>
		
		<div class="container-fluid mt-sm-3 mt-lg-5">
			<div class="row justify-content-center">
				<div class="col-auto col-sm-4 col-lg-5 col-xl-4 ml-sm-auto ml-lg-0 mb-2">
					<a href="protected/mes_listes" class="img">
						<img class="clickable" alt="Afficher mes listes partagées" title="Afficher mes listes partagées"
											src="resources/image/index/CheckListeOUT.png" onmouseout="this.src='resources/image/index/CheckListeOUT.png'"
											onmouseover="this.src='resources/image/index/CheckListeOVER.png'" />
					</a>
					<span class="ml-3 d-none d-lg-inline">Afficher mes listes partagées</span>
				</div>
				<div class="col-auto col-sm-5 col-xl-5 mr-sm-auto mr-lg-0 mb-2">
					<a href="protected/ma_liste" class="img">
						<img class="clickable" alt="Compléter ma liste" title="Compléter ma liste"
											src="resources/image/index/AjouterIdeeOUT.png" onmouseout="this.src='resources/image/index/AjouterIdeeOUT.png'"
											onmouseover="this.src='resources/image/index/AjouterIdeeOVER.png'" />
					</a>
					<span class="ml-3 d-none d-lg-inline">Compléter ma liste</span>
				</div>
				<div class="col-auto col-sm-4 col-lg-5 col-xl-4 ml-sm-auto ml-lg-0 mb-2">
					<a href="protected/afficher_reseau?id=${userid}" class="img">
						<img class="clickable" alt="Mes amis" title="Mes amis" src="resources/image/index/CreerGroupeOUT.png"
											onmouseout="this.src='resources/image/index/CreerGroupeOUT.png'"
											onmouseover="this.src='resources/image/index/CreerGroupeOVER.png'" />
					</a>
					<span class="ml-3 d-none d-lg-inline">Afficher mes amis</span>
				</div>
				<div class="col-auto col-sm-5 col-xl-5 mr-sm-auto mr-lg-0 mb-2">
					<a href="protected/rechercher_personne.jsp" class="img">
						<img class="clickable" alt="Ajouter un ami" title="Ajouter un ami" src="resources/image/index/RejoindreGroupeOUT.png"
											onmouseout="this.src='resources/image/index/RejoindreGroupeOUT.png'"
											onmouseover="this.src='resources/image/index/RejoindreGroupeOVER.png'" />
					</a>
					<span class="ml-3 d-none d-lg-inline">Ajouter un nouvel ami</span>
				</div>
			</div>
		</div>
	</jsp:body>
</t:normal_protected>
