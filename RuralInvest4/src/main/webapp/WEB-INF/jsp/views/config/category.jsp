<%@ page pageEncoding="UTF-8"%><%@ include file="/WEB-INF/jsp/inc/include.jsp" %>
<c:set var="menuType" value="config" scope="request"/><c:set var="currentStep" value="4" scope="request"/>
<html><head><title><spring:message code="projectCategory.addCat"/></title></head>
<body>
<c:set var="accessOK" value="${rivConfig.admin}" scope="request"/>
<form:form name="form" commandName="appConfig" method="post">
	<form:hidden path="configId"/>
	<tags:errors />
	<div style="width:500px;">
	    <fieldset>
			<legend><span class="header"><spring:message code="projectCategory.addCat"/></span></legend>      
			<tags:dataentry field="description" labelKey="projectCategory.description" size="20" inputClass="text"/>
			<form:label path="incomeGen"><spring:message code="projectCategory.incomeGenerating"/></form:label>
			<spring:bind path="incomeGen">
				<input type="hidden" name="_<c:out value="${status.expression}"/>">
				<input type="checkbox" name="incomeGen" <c:if test="${status.value}">checked</c:if> value="true">
			</spring:bind>
		</fieldset>
	 </div>
	<tags:submit><spring:message code="projectCategory.saveCat"/></tags:submit>
</form:form>
</body>