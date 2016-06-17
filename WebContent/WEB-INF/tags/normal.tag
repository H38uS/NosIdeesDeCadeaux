<%@tag description="Overall Page template" pageEncoding="UTF-8"%>

<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml"> 
    <head>  
        <meta http-equiv="Content-Type" content="text/html; charset=utf-8" /> 
        <title>Nos idées cadeaux</title>
		<base href="${pageContext.request.contextPath}/">
        <link rel="shortcut icon" href="public/image/cadeaux.ico"/>
        <link rel="stylesheet" type="text/css" href="public/css/common.css" />
        <link rel="stylesheet" type="text/css" href="public/css/normal/global.css" />
        <link rel="stylesheet" type="text/css" href="public/css/normal/menu.css" />
    </head> 
    <body>
		<t:menu></t:menu>
    	<div id="container">
	        <jsp:doBody/>
    	</div>
    </body>
</html>