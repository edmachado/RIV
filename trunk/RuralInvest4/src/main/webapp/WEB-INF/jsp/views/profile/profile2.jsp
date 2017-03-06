<%@ page pageEncoding="UTF-8"%><%@ include file="/WEB-INF/jsp/inc/include.jsp" %>
<html><head><title><spring:message code="profile.step2"/></title></head>
<body>
<form:form name="form" method="post" commandName="profile">
	<tags:errors />
	
	<tags:textbox field="benefName" helpText="profile.benefName.help" helpTitle="profile.benefName">i. <spring:message code="profile.benefName"/></tags:textbox>
	<tags:textbox field="benefDesc" multiline="true" helpText="profile.benefDesc.help" helpTitle="profile.benefDesc">ii. <spring:message code="profile.benefDesc"/></tags:textbox>
	<tags:textbox field="projDesc" multiline="true" helpText="profile.objective.help" helpTitle="profile.objective">iii. <spring:message code="profile.objective"/></tags:textbox>
	
	<c:if test="${not profile.incomeGen}">
		<tags:textbox field="sourceFunds" multiline="true" helpText="profile.sourceFunds.help" helpTitle="profile.sourceFunds">iv. <spring:message code="profile.sourceFunds"/></tags:textbox>
	</c:if>
	<tags:submit><spring:message code="misc.goto"/> <spring:message code="profile.step3"/></tags:submit>
</form:form>
</body></html>
		