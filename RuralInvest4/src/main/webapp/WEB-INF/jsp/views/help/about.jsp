<%@ include file="/WEB-INF/jsp/inc/include.jsp" %>
<c:set var="menuType" value="help" scope="request"/><c:set var="currentStep" value="3" scope="request"/>
<html><head><title><spring:message code="mainMenu.help.about"/></title></head>
<body>
	<h2>RuralInvest</h2>
	<div style="margin-right:100px; text-align:justify">
		Version  ${version}<br/><br/>
		<spring:message code="about.description"/>
	</div>
</body>
</html>