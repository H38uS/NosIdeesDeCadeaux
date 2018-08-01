<%@  taglib  uri="http://java.sun.com/jsp/jstl/core"  prefix="c"%>
<%@ tag language="java" pageEncoding="UTF-8"%>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
		<title>Nos idées cadeaux</title>
		<base href="${pageContext.request.contextPath}/">
		<link rel="shortcut icon" href="resources/image/cadeaux.ico" />
		<link rel="stylesheet" type="text/css" href="resources/css/lib/jquery-ui.min.css" />
		<link rel="stylesheet" type="text/css" href="resources/css/lib/tooltipster.css" />
		<link rel="stylesheet" type="text/css" href="resources/css/common.css" />
		<c:choose>
			<c:when test="${is_mobile}">
				<link rel="stylesheet" type="text/css" href="resources/css/mobile/layout.css" />
				<link rel="stylesheet" type="text/css" href="resources/css/mobile/normal.css" />
				<link rel="stylesheet" type="text/css" href="resources/css/mobile/forms.css" />
			</c:when>
			<c:otherwise>
				<link rel="stylesheet" type="text/css" href="resources/css/normal/layout.css" />
				<link rel="stylesheet" type="text/css" href="resources/css/normal/normal.css" />
			</c:otherwise>
		</c:choose>
		<script src="resources/js/jquery-3.2.1.min.js" type="text/javascript"></script>
		<script src="resources/js/lib/jquery-ui.js"></script>
		<script src="resources/js/lib/jquery.tooltipster.min.js" type="text/javascript"></script>
		<script src="resources/js/global.js" type="text/javascript"></script>
		<script src="resources/js/idea.js" type="text/javascript"></script>
		<jsp:doBody />
	</head>
