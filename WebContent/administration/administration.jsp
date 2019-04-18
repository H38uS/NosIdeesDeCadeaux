<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<t:template_head_includes>
	<script src="resources/js/admin.js" type="text/javascript"></script>
</t:template_head_includes>
<t:template_body_protected>
	<jsp:body>
		<div class="container">
			<div class="alert alert-info">
				Logs: ${log_folder}
				<ul>
					<c:forEach var="log" items="${log_files}">
						<li>
							<a href="protected/files/logs/${log.name}">${log.name}</a>
						</li>
					</c:forEach>
				</ul>
			</div>
		</div>
			<div class="row align-items-start mx-0 justify-content-around">
				<c:forEach var="user" items="${users}">
					<div class="card col-auto px-0 m-2 person_card">
						<div class="row align-items-center mx-auto person_card_pic">
							<img src="${avatars}/${user.avatarSrcLarge}">
						</div>
						<div class="card-body">
							<h5 class="card-title">
								<div>${user.name}</div>
								<div>${user.email}</div>
							</h5>
							<div>
								Dernière connexion:
							</div>
							<div>
								${user.lastLogin}&nbsp;
							</div>
							<div class="mt-1">
								Inscription:
							</div>
							<div>
								${user.creationDate}&nbsp;
							</div>
						</div>
						<div class="card-footer center">
							<div>
								<form method="POST" action="protected/connexion_enfant">
									<input type="hidden" name="name" value="${user.id}" />
									<button class="btn btn-primary" type="submit">Se connecter avec ce compte</button>
								</form>
							</div>
							<div class="mt-2">
								<form class="form_suppression_compte" method="POST" action="administration/suppression_compte">
									<input type="hidden" name="name" value="${user.id}" />
									<button class="btn btn-secondary form_suppression_compte_submit" type="submit">Supprimer ce compte</button>
								</form>
							</div>
						</div>
					</div>
				</c:forEach>
			</div>
	</jsp:body>
</t:template_body_protected>