<%@ page pageEncoding="UTF-8"%><%@ include file="/WEB-INF/jsp/inc/include.jsp" %>
<c:set var="menuType" value="config" scope="request"/><c:set var="currentStep" value="6" scope="request"/>
<html><head><title><spring:message code="enviroCategory.addCat"/></title></head>
<body>
<form:form name="form" commandName="appConfig" method="post">
	<form:hidden path="configId"/>
	<tags:errors />
	<div style="width:500px;">
	    <fieldset>
			<legend><span class="header"><spring:message code="enviroCategory.addCat"/></span></legend>      
			<tags:dataentry field="description" labelKey="enviroCategory.description" size="30" maxLength="50" inputClass="text"/>
		</fieldset>
	 </div>
	<tags:submit cancel="../enviroCategory"><spring:message code="enviroCategory.save"/></tags:submit>
</form:form>
</body>