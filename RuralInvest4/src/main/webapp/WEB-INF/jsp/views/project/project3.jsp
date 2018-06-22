<%@ page pageEncoding="UTF-8"%><%@ include file="/WEB-INF/jsp/inc/include.jsp" %>
<html><head><title><spring:message code="project.step3"/></title>
<script>
$(function() {
	$('#justificationQualitative, #projDescQualitative, #activitiesQualitative, #adminMisc1Qualitative, #adminMisc2Qualitative, #adminMisc3Qualitative').barrating({
      theme: 'css-stars'
    });
});
</script>
</head>
<body>
<form:form name="form" method="post" commandName="project">
	<tags:errors />
	
	<tags:textbox field="justification" multiline="true" helpText="project.justification.help" helpTitle="project.justification"
		qualitativeEnabled="${rivConfig.setting.qualJustificationEnabled}" qualitativeValue="${project.justificationQualitative}" qualitativeField="justificationQualitative">
		i. <spring:message code="project.justification"/></tags:textbox>
	<tags:textbox field="projDesc" multiline="true" helpText="project.projectDescription.help" helpTitle="project.projectDescription"
		qualitativeEnabled="${rivConfig.setting.qualProjDescEnabled}" qualitativeValue="${project.projDescQualitative}" qualitativeField="projDescQualitative">
		ii. <spring:message code="project.projectDescription"/></tags:textbox>
	<tags:textbox field="activities" multiline="true" helpText="project.activities.help" helpTitle="project.activities"
		qualitativeEnabled="${rivConfig.setting.qualActivitiesEnabled}" qualitativeValue="${project.activitiesQualitative}" qualitativeField="activitiesQualitative">
		iii. <spring:message code="project.activities"/></tags:textbox>
	<c:if test="${rivConfig.setting.adminMisc1Enabled}">
		<fieldset>
			<legend><tags:help noKey="true" title="${rivConfig.setting.adminMisc1Title}" text="${rivConfig.setting.adminMisc1Help}">${rivConfig.setting.adminMisc1Title}</tags:help></legend>
			<div class="dataentry">
				<c:if test="${rivConfig.setting.qualAdminMisc1Enabled}">
					<form:select path="adminMisc1Qualitative">
						<form:option value="1"></form:option>
						<form:option value="2"></form:option>
						<form:option value="3"></form:option>
						<form:option value="4"></form:option>
						<form:option value="5"></form:option>
					</form:select>
					 <br/>
				</c:if>
				<form:textarea path="adminMisc1" cols="98" rows="15" />
			</div>
		</fieldset>
	</c:if>
	<c:if test="${rivConfig.setting.adminMisc2Enabled}">
		<fieldset>
			<legend><tags:help noKey="true" title="${rivConfig.setting.adminMisc2Title}" text="${rivConfig.setting.adminMisc2Help}">${rivConfig.setting.adminMisc2Title}</tags:help></legend>
			<div class="dataentry">
				<c:if test="${rivConfig.setting.qualAdminMisc2Enabled}">
					<form:select path="${adminMisc2Qualitative}">
						<form:option value="1"></form:option>
						<form:option value="2"></form:option>
						<form:option value="3"></form:option>
						<form:option value="4"></form:option>
						<form:option value="5"></form:option>
					</form:select>
					 <br/>
				</c:if>
				<form:textarea path="adminMisc2" cols="98" rows="15" />
			</div>
		</fieldset>
	</c:if>
	<c:if test="${rivConfig.setting.adminMisc3Enabled}">
		<fieldset>
			<legend><tags:help noKey="true" title="${rivConfig.setting.adminMisc3Title}" text="${rivConfig.setting.adminMisc3Help}">${rivConfig.setting.adminMisc3Title}</tags:help></legend>
			<div class="dataentry">
				<c:if test="${rivConfig.setting.qualAdminMisc3Enabled}">
					<form:select path="${adminMisc3Qualitative}">
						<form:option value="1"></form:option>
						<form:option value="2"></form:option>
						<form:option value="3"></form:option>
						<form:option value="4"></form:option>
						<form:option value="5"></form:option>
					</form:select>
					 <br/>
				</c:if>
				<form:textarea path="adminMisc3" cols="98" rows="15" />
			</div>
		</fieldset>
	</c:if>
	<tags:submit><spring:message code="misc.goto"/> <spring:message code="project.step4"/></tags:submit>
	
</form:form>
</body></html>