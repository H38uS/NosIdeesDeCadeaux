<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>

<t:normal_public>
	<jsp:body>
		<h1>Succès !</h1>
		<p>Un email vient d'être envoyé à l'adresse ${email} pour réinitialiser votre mot de passe.</p>
		<div>Si vous ne recevez pas d'email d'ici 5 minutes, pensez à vérifier vos courriers indésirables.</div>
	</jsp:body>
</t:normal_public>