<%@  taglib  uri="http://java.sun.com/jsp/jstl/core"  prefix="c"%>
<%@tag description="Index template plage" pageEncoding="UTF-8"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html> 
	<t:template_head_includes>
		<c:choose>
			<c:when test="${is_mobile}">
				<link rel="stylesheet" type="text/css" href="resources/css/mobile/index.css" />
			</c:when>
			<c:otherwise>
				<link rel="stylesheet" type="text/css" href="resources/css/normal/index.css" />
			</c:otherwise>
		</c:choose>
	</t:template_head_includes>
	<t:template_body_public>
		<jsp:doBody></jsp:doBody>
	</t:template_body_public>
</html>