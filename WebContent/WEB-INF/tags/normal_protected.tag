<%@tag description="Index template plage" pageEncoding="UTF-8"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@  taglib  uri="http://java.sun.com/jsp/jstl/core"  prefix="c"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<t:template_head_includes>
	<link rel="stylesheet" type="text/css" href="public/css/common.css" />
	<link rel="stylesheet" type="text/css" href="public/css/normal/normal.css" />
	<c:if test="${is_mobile}">
		<link rel="stylesheet" type="text/css" href="public/css/mobile/normal.css" />
	</c:if>
	<link rel="stylesheet" type="text/css" href="public/css/lib/thickbox.css" />
	<script src="public/js/lib/thickbox.js" type="text/javascript"></script>
</t:template_head_includes>
<t:template_body_protected>
		<jsp:doBody />
</t:template_body_protected>
</html>
