<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>

<%@  taglib  uri="http://java.sun.com/jsp/jstl/sql"  prefix="sql"%>
<%@  taglib  uri="http://java.sun.com/jsp/jstl/core"  prefix="c"%>

<sql:query var="users" dataSource="jdbc/web-db">
	select login, email from personnes
</sql:query>
<t:normal>
	<jsp:body>
		<h2>Liste des utilisateurs</h2>
		<table>
			<thead>
				<td>Login</td>
				<td>Email</td>
			</thead>
			<c:forEach var="row" items="${users.rows}">
				<tr>
					<td>${row.login}</td>
					<td>${row.email}</td>
				</tr>
			</c:forEach>
		</table>
	</jsp:body>
</t:normal>