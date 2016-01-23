<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
		<base href="${pageContext.request.contextPath}/">
		<link type="text/css" rel="stylesheet" href="public/css/common.css" />
		<title>Insert title here</title>
	</head>
	<body>
		<t:menu></t:menu>
		<h1>Nosidéeskdo, des cadeaux qui font vraiment plaisir !</h1>
		<img alt="" src="public/image/main.jpg" width="1240px" >
		
		<div>
			Pas encore membre ? Découvrez l'univers des cadeaux qui font plaisir !
			<ul>
				<li>
					<a href="public/todo.jsp">Comment ça marche ?</a>
				</li>
				<li>
					<a href="public/todo.jsp">Démonstration</a>
				</li>
				<li>
					<a href="public/todo.jsp">Créer un compte !</a>
				</li>
			</ul>
		</div>
		<div>
			Du blabla !
		</div>
		<div>Déjà membre ? Accéder <a href="protected/index.jsp">à mon espace.</a></div>
		
	</body>
</html>