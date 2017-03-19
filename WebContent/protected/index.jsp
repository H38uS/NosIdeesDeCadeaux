<%@  taglib  uri="http://java.sun.com/jsp/jstl/sql"  prefix="sql"%>
<%@  taglib  uri="http://java.sun.com/jsp/jstl/core"  prefix="c"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<t:normal_proctected>
	<jsp:body>
		<h1>Prochain anniversaire</h1>
		<c:choose>
			<c:when test="${not empty userBirthday}">
				<table>
					<c:forEach var="user" items="${userBirthday}" >
					<tr>
						<td>
							<c:choose>
								<c:when test="${user.nbDaysBeforeBirthday == 0}">
									C'est l'anniversaire de ${user.name} aujourd'hui !
								</c:when>
								<c:when test="${user.nbDaysBeforeBirthday == 1}">
									L'anniversaire de ${user.name} est demain !
								</c:when>
								<c:otherwise>
									L'anniversaire de ${user.name} arrive dans ${user.nbDaysBeforeBirthday} jours !
								</c:otherwise>
							</c:choose>
						</td>
					</tr>
					</c:forEach>
				</table>
			</c:when>
			<c:otherwise>
				Aucun anniversaire dans peu de temps... C'est le bon moment pour récupérer des repas !
			</c:otherwise>
		</c:choose>
	</jsp:body>
</t:normal_proctected>
