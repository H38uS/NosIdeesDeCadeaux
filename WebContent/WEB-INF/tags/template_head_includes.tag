<%@  taglib  uri="http://java.sun.com/jsp/jstl/core"  prefix="c"%>
<%@ tag language="java" pageEncoding="UTF-8"%>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
		<title>Nos idées cadeaux</title>
		<base href="${pageContext.request.contextPath}/">
		<link rel="shortcut icon" href="resources/image/cadeaux.ico" />
		<c:choose>
			<c:when test="${is_mobile}">
				<link rel="stylesheet" type="text/css" href="resources/css/mobile/layout.css" />
			</c:when>
			<c:otherwise>
				<link rel="stylesheet" type="text/css" href="resources/css/normal/layout.css" />
			</c:otherwise>
		</c:choose>
		<link rel="stylesheet" type="text/css" href="resources/css/lib/jquery-ui.min.css" />
		<link rel="stylesheet" type="text/css" href="resources/css/lib/tooltipster.css" />
		<script src="resources/js/jquery-3.2.1.min.js" type="text/javascript"></script>
		<script src="https://code.jquery.com/ui/1.12.1/jquery-ui.js"></script>
		<script src="resources/js/lib/jquery.tooltipster.min.js" type="text/javascript"></script>
		<script src="resources/js/global.js" type="text/javascript"></script>
		<link rel="stylesheet" type="text/css" href="resources/css/common.css" />
		<link rel="stylesheet" type="text/css" href="resources/css/normal/normal.css" />
		<c:if test="${is_mobile}">
			<link rel="stylesheet" type="text/css" href="resources/css/mobile/normal.css" />
		</c:if>
		<jsp:doBody />
	</head>
