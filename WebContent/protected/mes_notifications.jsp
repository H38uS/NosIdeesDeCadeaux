<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<t:template_head_includes>
	<link rel="stylesheet" type="text/css" href="resources/css/lib/thickbox.css" />
	<script src="resources/js/lib/thickbox.js" type="text/javascript"></script>
	<script src="resources/js/notification.js" type="text/javascript"></script>
</t:template_head_includes>
<t:template_body_protected>
	<jsp:body>
		<h2>Mes notifications</h2>
		<c:if test="${not empty unread_notifications}">
			<table>
				<thead>
					<tr>
						<th>Text</th>
						<th>Reçue le</th>
						<th>Action</th>
					</tr>
				</thead>
				<c:forEach var="notif" items="${unread_notifications}">
					<tr>
						<td>${notif.text}</td>
						<td>${notif.creationTime}</td>
						<td>
							<a href="protected/notification_lue?notif_id=${notif.id}">Marquer comme lue</a> - 
							<a class="notif_delete" href="protected/supprimer_notification?notif_id=${notif.id}">Supprimer</a>
						</td>
					</tr>
				</c:forEach>
			</table>
		</c:if>
		<h2>Mes anciennes notifications</h2>
		<c:if test="${not empty read_notifications}">
			<table>
				<thead>
					<tr>
						<th>Text</th>
						<th>Reçue le</th>
						<th>Marquée comme lue le</th>
						<th>Action</th>
					</tr>
				</thead>
				<c:forEach var="notif" items="${read_notifications}">
					<tr>
						<td>${notif.text}</td>
						<td>${notif.creationTime}</td>
						<td>${notif.readOn}</td>
						<td>
							<a href="protected/notification_non_lue?notif_id=${notif.id}">Marquer comme non lue</a> - 
							<a class="notif_delete" href="protected/supprimer_notification?notif_id=${notif.id}">Supprimer</a>
						</td>
					</tr>
				</c:forEach>
			</table>
		</c:if>
	</jsp:body>
</t:template_body_protected>
</html>
