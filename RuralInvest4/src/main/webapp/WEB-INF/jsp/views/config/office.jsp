<%@ page pageEncoding="UTF-8"%><%@ include file="/WEB-INF/jsp/inc/include.jsp" %>
<c:set var="menuType" value="config" scope="request"/><c:set var="currentStep" value="3" scope="request"/>
<html><head><title><spring:message code="fieldOffice.addOffice"/></title></head>
<body>
<form:form name="form" commandName="appConfig" method="post">
	<form:hidden path="configId"/>
	<tags:errors />
	<div style="width:500px;">
	    <fieldset>
			<legend><span class="header"><spring:message code="fieldOffice.addOffice"/></span></legend>
			<tags:dataentry field="description" labelKey="fieldOffice.description" size="30" maxLength="50" inputClass="text"/>
		</fieldset>
	 </div>
	<tags:submit cancel="../office"><spring:message code="fieldOffice.saveOffice"/></tags:submit>
</form:form>
</body>