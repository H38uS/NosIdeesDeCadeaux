<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<t:normal_proctected>
		<jsp:body>
		<h2>Mes notifications</h2>
		<c:if test="${not empty notifications}">
			<table>
				<thead>
					<tr>
						<th>Type</th>
						<th>Text</th>
						<th>Action</th>
					</tr>
				</thead>
				<c:forEach var="notif" items="${notifications}">
					<tr>
						<td>${notif.type}</td>
						<td>${notif.text}</td>
						<td>
							<a href="protected/supprimer_notification?notif_id=${notif.id}">Supprimer</a>
						</td>
					</tr>
				</c:forEach>
			</table>
		</c:if>
	</jsp:body>
</t:normal_proctected>