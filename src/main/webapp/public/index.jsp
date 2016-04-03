<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<t:normal>
	<jsp:body>
		<h1>Nosidéeskdo, des cadeaux qui font vraiment plaisir !</h1>
		<img alt="" src="public/image/index/main.jpg" width="1240px" >
		
		<div>
			Envie de nous rejoindre ? Découvrez l'univers des cadeaux qui font plaisir !
			<ul>
				<li>
					<a href="public/comment_ca_marche.jsp">Comment ça marche ?</a>
				</li>
				<li>
					<a href="public/todo.jsp">Démonstration</a>
				</li>
				<li>
					<a href="public/creation_compte.jsp">Créer un compte !</a>
				</li>
				<li>
					Pas encore de groupe ?
					<ul>
						<li>
							<a href="protected/rechercher_groupe.jsp">Rejoingez-en un</a> !
						</li>
						<li>
							Ou <a href="protected/creation_groupe">créer</a> le vôtre !
						</li>
					</ul>
				</li>
			</ul>
		</div>
		<div>
			Du blabla !
		</div>
		<div>Déjà membre ? Accéder <a href="protected/index.jsp">à mon espace.</a></div>
		
		<div>
			Bonjour <c:out value="${username}" /> - 
			<a href="${pageContext.request.contextPath}/logout">me deconnecter.</a>
		</div>
	</jsp:body>
</t:normal>