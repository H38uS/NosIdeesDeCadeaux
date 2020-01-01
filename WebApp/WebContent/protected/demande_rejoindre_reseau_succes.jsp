<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<t:normal_protected>
	<jsp:body>
		<h2>Succès !</h2>
		<div class="alert alert-success">Une demande d'amis à ${name} vient d'être envoyée.</div>
	</jsp:body>
</t:normal_protected>