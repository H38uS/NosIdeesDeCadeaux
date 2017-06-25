<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<t:index>
	<jsp:body>
		<div class="indexbox">
			<span class="indexaction">
				<a href="protected/index.jsp">Se connecter</a>
			</span>
			<span class="indexaction">
					<a href="public/creation_compte.jsp">Créer un compte</a>
			</span>
		</div>
		<div id="middleindexbox">
			Envie de nous rejoindre ? Découvrez l'univers des cadeaux qui font plaisir !
		</div>
		<div class="indexbox">
			<span class="indexaction">
				<a href="protected/index.jsp">Créer un groupe !</a>
			</span>
			<span class="indexaction">
				<a href="protected/index.jsp">Rejoindre un groupe</a>
			</span>
			<span class="indexaction">
				<a href="protected/mes_listes">Afficher mes listes partagées</a>
			</span>
			<span class="indexaction">
				<a href="protected/ma_liste">Compléter ma liste</a>
			</span>
		</div>
	</jsp:body>
</t:index>