<%@ include file="/WEB-INF/jsp/inc/include.jsp" %>
<c:set var="menuType" value="help" scope="request"/><c:set var="currentStep" value="1" scope="request"/>
<html><head><title><spring:message code="mainMenu.help.manual"/></title>
<style>
#content p a { font-weight:bold; color:#EBAF01 }
#content p a:hover { text-decoration:underline }
</style>
</head>
<body>
	<h2><spring:message code="resources"/></h2>
	<p><spring:message code="manual.description"/></p>
	<ul>
		<li><spring:message code="manual.mod1"/></li>
		<li><spring:message code="manual.mod2"/></li>
		<li><spring:message code="manual.mod3"/></li>
	</ul>
	<p><spring:message code="manual.description.2"/></p>
</body>
</html>