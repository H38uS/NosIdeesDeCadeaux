<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>

<t:normal_public>
	<jsp:body>
		<h3>Bravo ${user}, votre compte est maintenant disponible</h3>
		<div class="alert alert-success">Ne perdez plus de temps, accéder <a href="protected/index.jsp">à votre espace.</a></div>
		<div class="alert alert-info">
			Si vous venez de créer ce compte pour un de vos enfants, prennez un moment pour ajouter votre compte en tant que parent.<br/>
			Pour cela :
			<ul>
				<li>Rendez-vous sur <a href="protected/mon_compte"> la page d'administration du compte</a> de cet enfant</li>
				<li>Puis ajouter un compte parent (le vôtre) à cet enfant</li>
			</ul>
		</div>
	</jsp:body>
</t:normal_public>