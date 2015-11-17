<%@ include file="/WEB-INF/jsp/inc/include.jsp" %>
<c:set var="menuType" value="help" scope="request"/><c:set var="currentStep" value="1" scope="request"/>
<html><head><title><spring:message code="mainMenu.help.manual"/></title>
</head>
<body>
	<h2><spring:message code="mainMenu.help.manual"/></h2>
		<spring:message code="manual.description"/><br/><br/>
		<spring:message code="manual.description.2"/>
		<br/>
		<c:if test="${lang != 'ru' && lang != 'fr' && lang != 'es'}"><c:set var="lang" value="en"/></c:if>
		<ul>
			<li><a href="../docs/module1_${lang}.pdf" target="_blank"><spring:message code="manual.mod1"/></a></li>
			<li><a href="../docs/module2_${lang}.pdf" target="_blank"><spring:message code="manual.mod2"/></a></li>
			
			<c:if test="${lang=='es'}"><c:set var="mod2formatLang" value="en"/></c:if>
			<c:if test="${lang!='es'}"><c:set var="mod2formatLang" value="${lang}"/></c:if>
			<li><a href="../docs/module2formats_${mod2formatLang}.doc" target="_blank"><spring:message code="manual.mod2.formats"/></a></li>		
		
			
			<li><a href="../docs/module3_${lang}.pdf" target="_blank"><spring:message code="manual.mod3"/></a></li>
			
			<c:if test="${lang=='ru'}"><c:set var="lang" value="en"/></c:if>
			<li><a href="../docs/guide_${lang}.pdf" target="_blank"><spring:message code="manual.guide"/></a></li>
			
			
		</ul>
</body>
</html>