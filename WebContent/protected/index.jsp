<%@  taglib  uri="http://java.sun.com/jsp/jstl/sql"  prefix="sql"%>
<%@  taglib  uri="http://java.sun.com/jsp/jstl/core"  prefix="c"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<t:normal>
	<jsp:body>
		<h1>Hello World from the application !</h1>
		<p>Pouet.</p>
		<sql:query var="rs" dataSource="jdbc/web-db">
			select id, nom from priorites
		</sql:query>
		<c:forEach var="row" items="${rs.rows}">
			Prio.id  : ${row.id}<br />
			Prio.nom : ${row.nom}<br />
		</c:forEach>
		<div>
			Bonjour <c:out value="${username}" /> - 
			<a href="${pageContext.request.contextPath}/logout">me deconnecter.</a>
		</div>
		<div>Id: <c:out value="${userid}" /></div>
		
		<sec:authorize access="isFullyAuthenticated()">
			<div>Connecté via un formulaire.</div>
		</sec:authorize>
		<sec:authorize access="isRememberMe()">
			<div>Connecté via remember me.</div>
		</sec:authorize>
		
		<ul>
			<li>
				<a href="protected/ma_liste">Compléter ma liste</a>
			</li>
			<li>
				<a href="protected/todo.jsp">Afficher mes listes partagées</a>
			</li>
			<li>
				<a href="protected/creation_groupe">Créer un groupe !</a>
			</li>
			<li>
				<a href="protected/rechercher_groupe.jsp">Rejoindre un groupe</a>
			</li>
			<li>
				<a href="protected/todo.jsp">Inviter des personnes !</a>
			</li>
			<li>
				<a href="protected/todo.jsp">Vos envies</a>
			</li>
			<li>
				<a href="protected/todo.jsp">Faites plaisir</a>
			</li>
		</ul>
	</jsp:body>
</t:normal>
