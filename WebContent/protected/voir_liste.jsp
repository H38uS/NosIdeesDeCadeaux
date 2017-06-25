<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<t:normal_proctected>
		<jsp:body>
		<c:if test="${userid != user.id}">
			<h2>Liste de cadeaux de ${user.name}</h2>
			<c:if test="${fn:length(user.ideas) > 0}">
				<table>
					<thead>
						<tr>
							<th>Type</th>
							<th>Idée</th>
							<th>Réservation</th>
							<th>Action</th>
						</tr>
					</thead>
					<c:forEach var="idee" items="${user.ideas}">
						<tr>
							<td>
								<c:if test="${not empty idee.category}">
									<img src="public/image/type/${idee.category.image}" title="${idee.category.title}" alt="${idee.category.alt}" />
								</c:if>
							</td>
							<td>${idee.html}</td>
							<td>
								<c:if test="${idee.isBooked()}">
									<c:if test="${not empty idee.bookingOwner}">
										<c:if test="${userid == idee.bookingOwner.id}">
											Réservée par vous le ${idee.bookingDate} - <a href="protected/dereserver?&idee=${idee.id}">Annuler</a> !
										</c:if>
										<c:if test="${userid != idee.bookingOwner.id}">
											Réservée par ${idee.bookingOwner.name} le ${idee.bookingDate}
										</c:if>
									</c:if>
									<c:if test="${empty idee.bookingOwner}">
										L'idée est réservée par un groupe (créé le ${idee.bookingDate}).
										<a href="protected/detail_du_groupe?groupid=${idee.groupKDO}">Voir le détail du groupe</a>
									</c:if>
								</c:if>
								<c:if test="${not idee.isBooked()}">
									L'idée n'a pas encore été réservée. <a href="protected/reserver?idee=${idee.id}">Je veux la réserver</a>
									ou <a href="protected/create_a_group?idee=${idee.id}">Créer un groupe</a>
								</c:if>
							</td>
							<td>
								<a href="protected/est_a_jour?idee=${idee.id}">Demander</a> si c'est à jour.<br />
								<a href="protected/idee_commentaires?idee=${idee.id}">Ajouter un commentaire / voir les existant</a>.
							</td>
						</tr>
					</c:forEach>
				</table>
			</c:if>
			<c:if test="${fn:length(user.ideas) == 0}">
				<span>${user.name} n'a pas encore d'idées.</span>
			</c:if>
		</c:if>
	</jsp:body>
</t:normal_proctected>