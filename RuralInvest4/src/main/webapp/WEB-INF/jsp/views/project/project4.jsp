<%@ page pageEncoding="UTF-8"%><%@ include file="/WEB-INF/jsp/inc/include.jsp" %>
<html><head><title><spring:message code="project.step4"/></title>
<script>
$(function() {
	$('#technologyQualitative, #requirementsQualitative').barrating({
      theme: 'css-stars'
    });
});
</script>
</head>
<body>
<form:form name="form" method="post" commandName="project">
	<tags:errors />
	
	<tags:textbox field="technology" multiline="true" helpText="project.technology.help" helpTitle="project.technology"
		qualitativeEnabled="${rivConfig.setting.qualTechnologyEnabled}" qualitativeValue="${project.technologyQualitative}" qualitativeField="technologyQualitative">
		i. <spring:message code="project.technology"/></tags:textbox>
	<tags:textbox field="requirements" multiline="true" helpText="project.requirements.help" helpTitle="project.requirements"
		qualitativeEnabled="${rivConfig.setting.qualRequirementsEnabled}" qualitativeValue="${project.requirementsQualitative}" qualitativeField="requirementsQualitative">
		ii. <spring:message code="project.requirements"/></tags:textbox>
	
	<tags:submit><spring:message code="misc.goto"/> <spring:message code="project.step5"/></tags:submit>
	
</form:form>
</body></html>