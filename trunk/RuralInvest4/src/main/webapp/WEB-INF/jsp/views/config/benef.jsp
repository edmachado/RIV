<%@ page pageEncoding="UTF-8"%><%@ include file="/WEB-INF/jsp/inc/include.jsp" %>
<c:set var="menuType" value="config" scope="request"/><c:set var="currentStep" value="5" scope="request"/>
<html><head><title><spring:message code="beneficiary.addBenef"/></title></head>
<body>
<form:form name="form" commandName="appConfig" method="post">
	<form:hidden path="configId"/>
	<tags:errors />
	<div style="width:500px;">
	    <fieldset>
			<legend><spring:message code="beneficiary.addBenef"/></legend>      
			<tags:dataentry field="description" labelKey="beneficiary.description" size="30" maxLength="50" inputClass="text"/>
		</fieldset>
	 </div>
	<tags:submit cancel="../beneficiary"><spring:message code="beneficiary.saveBenef"/></tags:submit>
</form:form>
</body>