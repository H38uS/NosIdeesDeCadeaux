<%@  taglib  uri="http://java.sun.com/jsp/jstl/sql"  prefix="sql"%>
<%@  taglib  uri="http://java.sun.com/jsp/jstl/core"  prefix="c"%>

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>

<t:normal_protected>
	<jsp:body>
		<h1>Prochain anniversaire</h1>
		<c:choose>
			<c:when test="${not empty userBirthday}">
				<table>
					<c:forEach var="user" items="${userBirthday}" >
					<tr>
						<td>
							<c:choose>
								<c:when test="${user.nbDaysBeforeBirthday == 0}">
									C'est l'anniversaire de ${user.name} aujourd'hui !
								</c:when>
								<c:when test="${user.nbDaysBeforeBirthday == 1}">
									L'anniversaire de ${user.name} est demain !
								</c:when>
								<c:otherwise>
									L'anniversaire de ${user.name} arrive dans ${user.nbDaysBeforeBirthday} jours !
								</c:otherwise>
							</c:choose>
						</td>
					</tr>
					</c:forEach>
				</table>
			</c:when>
			<c:otherwise>
				Aucun anniversaire dans peu de temps... C'est le bon moment pour récupérer des repas !
			</c:otherwise>
		</c:choose>
		<table id="indexactions">
			<tr>
				<td>
					<a href="protected/mes_listes">
						<img class="clickable" alt="Afficher mes listes partagées" title="Afficher mes listes partagées"
											src="public/image/index/CheckListeOUT.png" onmouseout="this.src='public/image/index/CheckListeOUT.png'"
											onmouseover="this.src='public/image/index/CheckListeOVER.png'" />
					</a>
				</td>
				<td>
					<a href="protected/ma_liste">
						<img class="clickable" alt="Compléter ma liste" title="Compléter ma liste"
											src="public/image/index/AjouterIdeeOUT.png" onmouseout="this.src='public/image/index/AjouterIdeeOUT.png'"
											onmouseover="this.src='public/image/index/AjouterIdeeOVER.png'" />
					</a>
				</td>
			</tr>
			<tr>
				<td>
					<a href="protected/afficher_reseau?id=${userid}">
						<img class="clickable" alt="Mes amis" title="Mes amis" src="public/image/index/CreerGroupeOUT.png"
											onmouseout="this.src='public/image/index/CreerGroupeOUT.png'"
											onmouseover="this.src='public/image/index/CreerGroupeOVER.png'" />
					</a>
				</td>
				<td>
					<a href="protected/rechercher_personne.jsp">
						<img class="clickable" alt="Ajouter un ami" title="Ajouter un ami" src="public/image/index/RejoindreGroupeOUT.png"
											onmouseout="this.src='public/image/index/RejoindreGroupeOUT.png'"
											onmouseover="this.src='public/image/index/RejoindreGroupeOVER.png'" />
					</a>
				</td>
			</tr>
		</table>
	</jsp:body>
</t:normal_protected>
