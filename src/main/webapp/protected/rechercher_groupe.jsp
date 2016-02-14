<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<t:normal>
	<jsp:body>
		<h2>Rechercher un groupe</h2>
		<div>
			<form method="POST" action="protected/rechercher_groupe">
				<table>
					<tr>
						<td>
							<label for="name" class="required">Nom du groupe</label>
						</td>
						<td>
							<input type="text" name="name" id="name" value="${param.name}" />
						</td>
					</tr>
					<tr>
						<td colspan="2" align="center">
							<input type="submit" name="submit" id="submit" value="Rechercher !" />
						</td>
					</tr>
				</table>
			</form>
		</div>
		<div>
			<c:if test="${fn:length(groupes) > 0}">
				<table>
					<thead>
						<tr>
							<th>Nom du groupe</th>
							<th>Nombre de membres</th>
						</tr>
					</thead>
				<c:forEach var="groupe" items="${groupes}">
					<tr>
						<td>${groupe.name}</td>
						<td>${groupe.nbMembers}</td>
					</tr>
				</c:forEach>
				</table>
			</c:if>
		</div>
	</jsp:body>
</t:normal>