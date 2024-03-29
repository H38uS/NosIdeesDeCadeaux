<%@  taglib  uri="http://java.sun.com/jsp/jstl/sql"  prefix="sql"%>
<%@  taglib  uri="http://java.sun.com/jsp/jstl/core"  prefix="c"%>

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>

<t:normal_protected>
	<jsp:body>
		<script>
			if ( isMobileView() ) {
				// Mode normal, on sélectionne le champs recherche par défaut sur l'index
				$("#header_name").focus();
			}
		</script>
		<c:if test="${!connected_user.hasSetUpAnAvatar()}">
			<p class="alert alert-warning">
				Vous n'avez pas encore d'image de profile. Cela aidera vos amis à vous trouver parmi leur liste !<br/>
				Vous pouvez le faire à tout moment dans votre compte en suivant ce lien <a href="protected/mon_compte">mon compte</a>.
			</p>
		</c:if>
		<c:if test="${no_birth_date_set}">
			<p class="alert alert-warning">
				Vous n'avez pas encore entré votre date de naissance... Vous devez le faire pour apparaitre ici auprès de vos amis !<br/>
				Vous pouvez le faire à tout moment dans votre compte en suivant ce lien <a href="protected/mon_compte">mon compte</a>.
			</p>
		</c:if>
		<h2>Prochains évènements</h2>
		<div class="mb-2">
			<c:choose>
				<c:when test="${empty nothingMessage}">
					<c:if test="${not empty userBirthday}">
						<span>${birthdayMessage}</span>
						<ul>
							<c:forEach var="user" items="${userBirthday}" >
							<li>
                                <c:choose>
                                    <c:when test="${user.hasBookedOneOfItsIdeas}">
                                        <span title="Vous avez déjà réservé des idées" class="badge badge-success">Réservé</span>
                                    </c:when>
                                    <c:otherwise>
                                        <span title="Vous n'avez pas encore réservé d'idée !" class="badge badge-warning">Non réservé</span>
                                    </c:otherwise>
                                </c:choose>
                                <c:choose>
                                    <c:when test="${user.user.nbDaysBeforeBirthday == 0}">
                                        C'est l'anniversaire ${user.user.myDName} aujourd'hui !
                                    </c:when>
                                    <c:when test="${user.user.nbDaysBeforeBirthday == 1}">
                                        L'anniversaire ${user.user.myDName} est demain !
                                    </c:when>
                                    <c:otherwise>
                                        L'anniversaire ${user.user.myDName} arrive dans ${user.user.nbDaysBeforeBirthday} jours !
                                    </c:otherwise>
                                </c:choose>
                                <span class="px-1 d-none d-lg-inline">
                                    <a href="protected/voir_liste?id=${user.user.id}">Aller jeter un coup d'oeil à sa liste...</a>
                                </span>
                                <span class="px-1 d-lg-none">
                                    <a href="protected/voir_liste?id=${user.user.id}">Voir sa liste</a>
                                </span>
							</li>
							</c:forEach>
						</ul>
					</c:if>
					<c:if test="${not empty christmasMessage}">
						<p>${christmasMessage}</p>
					</c:if>
				</c:when>
				<c:otherwise>
					${nothingMessage}
				</c:otherwise>
			</c:choose>
		</div>
        <c:if test="${nb_of_reservations > 0}">
            <div class="alert alert-info mb-2">
                Vous avez actuellement réservé ${nb_of_reservations} idée(s). <a href="protected/mes_reservations">Aller les voir</a> toutes.
            </div>
        </c:if>

		<div class="container-fluid mt-sm-3 mt-lg-5">
			<div class="row justify-content-center">
				<div class="col-auto col-sm-4 col-lg-5 col-xl-4 ml-sm-auto ml-lg-0 mb-2">
					<a href="protected/voir_liste?id=${connected_user.id}" class="img">
						<img class="clickable" alt="Afficher mes listes partagées" title="Afficher ma liste"
											src="resources/image/index/CheckListeOUT.png" onmouseout="this.src='resources/image/index/CheckListeOUT.png'"
											onmouseover="this.src='resources/image/index/CheckListeOVER.png'" />
					</a>
					<span class="ml-3 d-none d-lg-inline">Afficher ma liste</span>
				</div>
				<div class="col-auto col-sm-5 col-xl-5 mr-sm-auto mr-lg-0 mb-2">
					<a href="protected/ajouter_idee" class="img">
						<img class="clickable" alt="Compléter ma liste" title="Compléter ma liste"
											src="resources/image/index/AjouterIdeeOUT.png" onmouseout="this.src='resources/image/index/AjouterIdeeOUT.png'"
											onmouseover="this.src='resources/image/index/AjouterIdeeOVER.png'" />
					</a>
					<span class="ml-3 d-none d-lg-inline">Compléter ma liste</span>
				</div>
				<div class="col-auto col-sm-4 col-lg-5 col-xl-4 ml-sm-auto ml-lg-0 mb-2">
					<a href="protected/afficher_reseau?id=${connected_user.id}" class="img">
						<img class="clickable" alt="Mes amis" title="Mes amis" src="resources/image/index/CreerGroupeOUT.png"
											onmouseout="this.src='resources/image/index/CreerGroupeOUT.png'"
											onmouseover="this.src='resources/image/index/CreerGroupeOVER.png'" />
					</a>
					<span class="ml-3 d-none d-lg-inline">Afficher mes amis</span>
				</div>
				<div class="col-auto col-sm-5 col-xl-5 mr-sm-auto mr-lg-0 mb-2">
					<a href="protected/rechercher_personne" class="img">
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
