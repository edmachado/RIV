<%@ page pageEncoding="UTF-8"%><%@ include file="/WEB-INF/jsp/inc/include.jsp" %>
<html><head><title><spring:message code="project.step2"/></title></head>
<body>
	<form:form name="form" method="post" commandName="donor">
		<tags:errors />
		<div>
			<fieldset>
				<legend><spring:message code="misc.addItem"/> (<spring:message code="project.donor.description"/>)</legend>
				<tags:dataentry field="description" labelKey="project.donor.description" helpText="project.donor.description.help" inputClass="text" size="20" maxLength="150"/>
				<div class="dataentry">
					<label><tags:help text="project.donor.type.help" title="project.donor.type"><spring:message code="project.donor.type"/></tags:help></label>
					<form:select path="contribType">
						<form:option value="0"><spring:message code="projectContribution.contribType.govtCentral"/></form:option>
						<form:option value="1"><spring:message code="projectContribution.contribType.govtLocal"/></form:option>
						<form:option value="2"><spring:message code="projectContribution.contribType.ngoLocal"/></form:option>
						<form:option value="3"><spring:message code="projectContribution.contribType.ngoIntl"/></form:option>
						<form:option value="5"><spring:message code="projectContribution.contribType.beneficiary"/></form:option>
						<form:option value="4"><spring:message code="projectContribution.contribType.other"/></form:option>
					</form:select>
				</div>
			</fieldset>
		</div>
		<tags:submit><spring:message code="misc.saveItem"/></tags:submit>
	</form:form>
</body></html>