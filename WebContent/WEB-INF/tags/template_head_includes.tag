<%@  taglib  uri="http://java.sun.com/jsp/jstl/core"  prefix="c"%>
<%@ tag language="java" pageEncoding="UTF-8"%>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
		<title>Nos idées cadeaux</title>
		<base href="${pageContext.request.contextPath}/">
		<link rel="shortcut icon" href="public/image/cadeaux.ico" />
		<link rel="stylesheet" type="text/css" href="public/css/normal/layout.css" />
		<link rel="stylesheet" type="text/css" href="public/css/lib/jquery-ui.min.css" />
		<link rel="stylesheet" type="text/css" href="public/css/lib/tooltipster.css" />
		<script src="public/js/jquery-3.2.1.min.js" type="text/javascript"></script>
		<script src="https://code.jquery.com/ui/1.12.1/jquery-ui.js"></script>
		<script src="public/js/lib/jquery.tooltipster.min.js" type="text/javascript"></script>
		<script src="public/js/global.js" type="text/javascript"></script>
		<link rel="stylesheet" type="text/css" href="public/css/common.css" />
		<link rel="stylesheet" type="text/css" href="public/css/normal/normal.css" />
		<c:if test="${is_mobile}">
			<link rel="stylesheet" type="text/css" href="public/css/mobile/normal.css" />
		</c:if>
		<jsp:doBody />
	</head>
