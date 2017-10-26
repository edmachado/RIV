<%@ page pageEncoding="UTF-8"%><%@ include file="/WEB-INF/jsp/inc/include.jsp" %>
<html><head><title><spring:message code="project.step6"/></title></head>
<body>
<form:form name="form" method="post" commandName="project">
	<tags:errors />
	
	<tags:textbox field="organization" multiline="true" helpText="project.organization.help" helpTitle="project.organization"
		qualitativeEnabled="${rivConfig.setting.qualOrganizationEnabled}" qualitativeValue="${project.organizationQualitative}" qualitativeField="organizationQualitative">
		<spring:message code="project.organization"/></tags:textbox>
	<tags:textbox field="assumptions" multiline="true" helpText="project.assumptions.help" helpTitle="project.assumptions"
		qualitativeEnabled="${rivConfig.setting.qualAssumptionsEnabled}" qualitativeValue="${project.assumptionsQualitative}" qualitativeField="assumptionsQualitative">
		<spring:message code="project.assumptions"/></tags:textbox>
	
	<tags:submit><spring:message code="misc.goto"/> <spring:message code="project.step7"/></tags:submit>
	
</form:form>
</body></html>