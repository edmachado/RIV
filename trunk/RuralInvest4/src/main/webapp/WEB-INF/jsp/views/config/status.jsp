<%@ page pageEncoding="UTF-8"%><%@ include file="/WEB-INF/jsp/inc/include.jsp" %>
<c:set var="menuType" value="config" scope="request"/><c:set var="currentStep" value="7" scope="request"/>
<html><head><title><spring:message code="projectStatus.addStatus"/></title></head>
<body>
<form:form name="form" commandName="appConfig" method="post">
	<form:hidden path="configId"/>
	<tags:errors />
	<div style="width:500px;">
	    <fieldset>
			<legend><span class="header"><spring:message code="projectStatus.addStatus"/></span></legend>      
			<tags:dataentry field="description" labelKey="projectStatus.description" size="30" maxLength="50" inputClass="text"/>
		</fieldset>
	 </div>
	<tags:submit><spring:message code="projectStatus.save"/></tags:submit>
</form:form>
</body>