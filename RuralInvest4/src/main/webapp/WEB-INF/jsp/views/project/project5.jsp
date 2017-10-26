<%@ page pageEncoding="UTF-8"%><%@ include file="/WEB-INF/jsp/inc/include.jsp" %>
<html><head><title><spring:message code="project.step5"/></title></head>
<body>
<form:form name="form" method="post" commandName="project">
	<tags:errors />
	
	<c:if test="${not project.incomeGen}">
		<tags:textbox field="sustainability" multiline="true" helpText="project.sustainability.nongen.help" helpTitle="project.sustainability.nongen"
			qualitativeEnabled="${rivConfig.setting.qualSustainabilityEnabled}" qualitativeValue="${project.sustainabilityQualitative}" qualitativeField="sustainabilityQualitative">
			<spring:message code="project.sustainability.nongen"/></tags:textbox>
	</c:if>
	
	<tags:textbox field="enviroImpact" multiline="true" helpText="project.enviroImpact.help" helpTitle="project.enviroImpact"
		qualitativeEnabled="${rivConfig.setting.qualEnviroImpactEnabled}" qualitativeValue="${project.enviroImpactQualitative}" qualitativeField="enviroImpactQualitative">
		<spring:message code="project.enviroImpact"/></tags:textbox>
	
	<c:if test="${project.incomeGen}">
		<tags:textbox field="market" multiline="true" helpText="project.market.help" helpTitle="project.market"
			qualitativeEnabled="${rivConfig.setting.qualMarketEnabled}" qualitativeValue="${project.marketQualitative}" qualitativeField="marketQualitative">
			<spring:message code="project.market"/></tags:textbox>
	</c:if>
	<c:if test="${not project.incomeGen}">
		<tags:textbox field="market" multiline="true" helpText="project.demand.help" helpTitle="project.demand"
			qualitativeEnabled="${rivConfig.setting.qualMarketEnabled}" qualitativeValue="${project.marketQualitative}" qualitativeField="marketQualitative">
			<spring:message code="project.demand"/></tags:textbox>
	</c:if>
	
	<tags:submit><spring:message code="misc.goto"/> <spring:message code="project.step6"/></tags:submit>
	
</form:form>
</body></html>