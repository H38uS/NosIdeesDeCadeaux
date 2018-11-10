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
		<h3>Mes notifications</h3>
		<div class="container">
			<div class="row align-items-start mx-0 justify-content-around">
				<c:forEach var="notif" items="${unread_notifications}">
					<div class="card my-3" style="width:300px">
						<div class="card-header bg-dark" style="color:white">
							${notif.description}
						</div>
						<div class="card-body">${notif.text}</div>
						<ul class="list-group list-group-flush">
							<li class="list-group-item center">
								<a class="btn btn-primary" href="protected/notification_lue?notif_id=${notif.id}">Notification lue</a>
								<a class="btn btn-secondary notif_delete" href="protected/supprimer_notification?notif_id=${notif.id}">Supprimer</a>
							</li>
						</ul>
						<div class="card-footer text-muted text-right">
							${notif.creationTime}
						</div>
					</div>
				</c:forEach>
			</div>
		</div>
		<h3 class="my-2">Mes anciennes notifications</h3>
		<div class="container">
			<div class="card-deck">
				<c:forEach var="notif" items="${read_notifications}">
					<div class="card my-2">
						<div class="card-header bg-dark" style="color:white">
							${notif.description}
						</div>
						<div class="card-body">${notif.text}</div>
						<ul class="list-group list-group-flush">
							<li class="list-group-item center">
								<a class="btn btn-primary" href="protected/notification_non_lue?notif_id=${notif.id}">Marquer non lue</a>
								<a class="btn btn-secondary notif_delete" href="protected/supprimer_notification?notif_id=${notif.id}">Supprimer</a>
							</li>
						</ul>
						<div class="card-footer text-muted text-right">
							${notif.creationTime}
						</div>
					</div>
				</c:forEach>
			</div>
		</div>
	</jsp:body>
</t:template_body_protected>
</html>
