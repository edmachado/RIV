<%@ page pageEncoding="UTF-8"%><%@ include file="/WEB-INF/jsp/inc/include.jsp" %>
<c:set var="menuType" value="config" scope="request"/>
<c:if test="${type eq 'appConfig1'}">
	<c:set var="title">${rivConfig.setting.admin1Title}</c:set>
	<c:set var="currentStep" value="9" scope="request"/>
</c:if>
<c:if test="${type eq 'appConfig2'}">
	<c:set var="title">${rivConfig.setting.admin2Title}</c:set>
	<c:if test="${rivConfig.setting.admin1Enabled}"><c:set var="currentStep" value="10" scope="request"/></c:if>
	<c:if test="${not rivConfig.setting.admin1Enabled}"><c:set var="currentStep" value="9" scope="request"/></c:if>
</c:if>
<html><head><title>${title}</title></head>
<body>
<form:form name="form" commandName="appConfig" method="post">
	<form:hidden path="configId"/><sec:csrfMetaTags />
	<tags:errors />
	<div style="width:500px;">
	    <fieldset>
			<legend><spring:message code="customFields.add"/></legend>      
			<tags:dataentry field="description" labelKey="customFields.description" size="30" maxLength="50" inputClass="text"/>
		</fieldset>
	 </div>
	<tags:submit><spring:message code="customFields.save"/></tags:submit>
</form:form>
</body>