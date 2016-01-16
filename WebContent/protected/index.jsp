<%@  taglib  uri="http://java.sun.com/jsp/jstl/sql"  prefix="sql"%>
<%@  taglib  uri="http://java.sun.com/jsp/jstl/core"  prefix="c"%>

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; utf-8">
<title>Insert title here</title>
</head>
<body>
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
			Bonjour <c:out value="${pageContext.request.userPrincipal.name}" /> - 
			<a href="${pageContext.request.contextPath}/logout">me deconnecter.</a>
		</div>
</body>
</html>