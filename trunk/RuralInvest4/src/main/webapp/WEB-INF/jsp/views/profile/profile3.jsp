<%@ page pageEncoding="UTF-8"%><%@ include file="/WEB-INF/jsp/inc/include.jsp" %>
<html><head><title><spring:message code="profile.step3"/></title></head>
<body>
<form:form name="form" method="post" commandName="profile">
	<tags:errors />
	
	<c:if test="${profile.incomeGen}">
   		<tags:textbox field="market" multiline="true" helpText="profile.market.help" helpTitle="profile.market"><spring:message code="profile.market"/></tags:textbox>
	</c:if>
   	<c:if test="${!profile.incomeGen}">
   		<tags:textbox field="market" multiline="true" helpText="profile.demand.help" helpTitle="profile.demand"><spring:message code="profile.demand"/></tags:textbox>
	</c:if>
	
	<tags:textbox field="enviroImpact" multiline="true" helpText="profile.envirImpact.help" helpTitle="profile.envirImpact"><spring:message code="profile.envirImpact"/></tags:textbox>
	
	<tags:submit><spring:message code="misc.goto"/> <spring:message code="profile.step4"/></tags:submit>
</form:form>
</body></html>