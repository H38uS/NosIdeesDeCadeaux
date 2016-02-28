<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<t:normal>
	<jsp:body>
		<h2>Membres de votre groupe</h2>
		<div>
			<table>
				<thead>
					<tr>
						<th>Nom du membre</th>
					</tr>
				</thead>
			<c:forEach var="member" items="${members}">
				<tr>
					<td>${member.name}</td>
				</tr>
			</c:forEach>
			</table>
		</div>
	</jsp:body>
</t:normal>